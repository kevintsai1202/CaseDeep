package com.casemgr.specification;

import org.springframework.data.jpa.domain.Specification;

import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.User;
import com.casemgr.enumtype.UserType;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用戶查詢規格類
 * 提供各種用戶查詢的條件組合
 */
public class UserSpecification {

    /**
     * 根據UserType和Industry進行篩選
     * @param userType 用戶類型
     * @param industry 行業名稱
     * @return 查詢規格
     */
    public static Specification<User> filterByUserTypeAndIndustry(UserType userType, String industry) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // UserType篩選
            if (userType != null) {
                predicates.add(criteriaBuilder.equal(root.get("userType"), userType));
            }
            
            // Industry篩選邏輯
            if (industry != null && !"All Industries".equals(industry)) {
                Predicate industryPredicate = null;
                
                if (userType == UserType.PROVIDER || userType == null) {
                    // PROVIDER通過BusinessProfile篩選
                    Join<User, BusinessProfile> businessJoin = root.join("businessProfile", JoinType.LEFT);
                    Predicate providerIndustry = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("userType"), UserType.PROVIDER),
                        criteriaBuilder.equal(businessJoin.get("industry"), industry)
                    );
                    industryPredicate = providerIndustry;
                }
                
                if (userType == UserType.CLIENT || userType == null) {
                    // CLIENT通過PersonProfile篩選
                    Join<User, PersonProfile> personJoin = root.join("personProfile", JoinType.LEFT);
                    Predicate clientIndustry = criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("userType"), UserType.CLIENT),
                        criteriaBuilder.equal(personJoin.get("industry"), industry)
                    );
                    
                    if (industryPredicate != null) {
                        industryPredicate = criteriaBuilder.or(industryPredicate, clientIndustry);
                    } else {
                        industryPredicate = clientIndustry;
                    }
                }
                
                if (industryPredicate != null) {
                    predicates.add(industryPredicate);
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 最近一周新增用戶篩選
     * @return 查詢規格
     */
    public static Specification<User> createdInLastWeek() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), oneWeekAgo);
        };
    }
    
    /**
     * 關鍵字搜尋篩選
     * @param keyword 搜尋關鍵字
     * @return 查詢規格
     */
    public static Specification<User> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            
            return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern)
            );
        };
    }
    
    /**
     * 舊版本相容性方法
     * @deprecated 請使用新的篩選方法
     */
    @Deprecated
    public static Specification<User> filterByIndustrySubIndustryLocation(Long industryId, Long subIndustryId, String location) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (industryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("industry").get("id"), industryId));
            }
            if (subIndustryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("subIndustry").get("id"), subIndustryId));
            }
            if (location != null && !location.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("location"), location));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}