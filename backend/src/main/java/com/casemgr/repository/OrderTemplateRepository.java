package com.casemgr.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.User;

public interface OrderTemplateRepository extends JpaRepository<OrderTemplate, Long> {
	
	public List<OrderTemplate> findAllByProvider(User provider);
	public List<OrderTemplate> findByProviderOrderByUpdateTimeDesc(User provider);
	public List<OrderTemplate> findByNameContainingIgnoreCase(String name);
	
	/**
	 * 根據多個產業 ID 查詢已啟用的訂單模板（分頁）
	 * @param industryIds 產業 ID 列表
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	Page<OrderTemplate> findByIndustryIdInAndEnabledTrue(List<Long> industryIds, Pageable pageable);
	
	/**
	 * 根據單一產業 ID 查詢已啟用的訂單模板（分頁）
	 * @param industryId 產業 ID
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	Page<OrderTemplate> findByIndustryIdAndEnabledTrue(Long industryId, Pageable pageable);
	
	/**
	 * 根據產業 ID 和名稱查詢已啟用的訂單模板（用於比對 subindustry）
	 * @param industryId 產業 ID
	 * @param name 名稱
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	Page<OrderTemplate> findByIndustryIdAndNameAndEnabledTrue(Long industryId, String name, Pageable pageable);
	
	/**
	 * 根據區域篩選已啟用的訂單模板（透過 provider 的 region）
	 * @param region 區域
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.provider.region = :region AND ot.enabled = true")
	Page<OrderTemplate> findByProviderRegionAndEnabledTrue(@Param("region") String region, Pageable pageable);
	
	/**
	 * 根據區域和產業 ID 篩選已啟用的訂單模板
	 * @param region 區域
	 * @param industryId 產業 ID
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.provider.region = :region AND ot.industry.id = :industryId AND ot.enabled = true")
	Page<OrderTemplate> findByProviderRegionAndIndustryIdAndEnabledTrue(@Param("region") String region, @Param("industryId") Long industryId, Pageable pageable);
	
	/**
	 * 根據名稱模糊查詢已啟用的訂單模板
	 * @param namePattern 名稱模式（支援 LIKE 查詢）
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.name LIKE %:namePattern% AND ot.enabled = true")
	Page<OrderTemplate> findByNameContainingAndEnabledTrue(@Param("namePattern") String namePattern, Pageable pageable);
	
	/**
	 * 根據區域、產業 ID 和名稱模糊查詢已啟用的訂單模板
	 * @param region 區域
	 * @param industryId 產業 ID
	 * @param namePattern 名稱模式（支援 LIKE 查詢）
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.provider.region = :region AND ot.industry.id = :industryId AND ot.name LIKE %:namePattern% AND ot.enabled = true")
	Page<OrderTemplate> findByProviderRegionAndIndustryIdAndNameContainingAndEnabledTrue(@Param("region") String region, @Param("industryId") Long industryId, @Param("namePattern") String namePattern, Pageable pageable);
	
	/**
	 * 根據多個產業 ID 和區域查詢已啟用的訂單模板
	 * @param industryIds 產業 ID 列表
	 * @param region 區域
	 * @param pageable 分頁參數
	 * @return 訂單模板分頁結果
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.industry.id IN :industryIds AND ot.provider.region = :region AND ot.enabled = true")
	Page<OrderTemplate> findByIndustryIdInAndProviderRegionAndEnabledTrue(@Param("industryIds") List<Long> industryIds, @Param("region") String region, Pageable pageable);
	
	/**
	 * 根據名稱模糊查詢已啟用的訂單模板（返回List）
	 * @param namePattern 名稱模式（支援 LIKE 查詢）
	 * @return 訂單模板列表
	 */
	@Query("SELECT ot FROM OrderTemplate ot WHERE ot.name LIKE %:namePattern% AND ot.enabled = true ORDER BY ot.name")
	List<OrderTemplate> findByNameContainingAndEnabledTrueOrderByName(@Param("namePattern") String namePattern);
}
