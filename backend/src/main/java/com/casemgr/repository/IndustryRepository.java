package com.casemgr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casemgr.entity.Industry;

public interface IndustryRepository extends JpaRepository<Industry, Long> {
	
	/**
	 * 根據名稱查詢產業
	 * @param name 產業名稱
	 * @return 產業實體
	 */
	Industry findByName(String name);

//    /**
//     * 根據 URL 路徑查找 Industry (英文)
//     * @param urlPath URL 路徑
//     * @return 對應的 Industry (Optional)
//     */
//    Optional<Industry> findByUrlPath(String urlPath);
    
    /**
     * 根據名稱或標題查詢產業
     * @param name 產業名稱
     * @param title 產業標題
     * @return 產業實體（Optional）
     */
    @Query("SELECT i FROM Industry i WHERE i.name = :name OR i.title = :title")
    Optional<Industry> findByNameOrTitle(@Param("name") String name, @Param("title") String title);
    
    /**
     * 根據父產業 ID 查詢所有子產業
     * @param parentId 父產業 ID
     * @return 子產業列表
     */
    java.util.List<Industry> findByParentIndustryIdAndEnabledTrue(Long parentId);
    
    /**
     * 查詢所有父產業（按名稱排序）
     * 父產業定義為 parentIndustry 為 null 的產業
     * @return 父產業列表，按名稱升序排列
     */
    java.util.List<Industry> findByParentIndustryIsNullAndEnabledTrueOrderByName();
}
