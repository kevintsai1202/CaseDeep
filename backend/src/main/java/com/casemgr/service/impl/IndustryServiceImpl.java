package com.casemgr.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.casemgr.converter.IndustryConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.Industry;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.request.IndustryRequest;
import com.casemgr.response.IndustryResponse;
import com.casemgr.response.OrderTemplateResponse;
import com.casemgr.service.IndustryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("industryService")
@RequiredArgsConstructor
public class IndustryServiceImpl implements IndustryService {

	private final IndustryRepository industryRepository;
	private final OrderTemplateRepository orderTemplateRepository;
	private final IndustryConverter industryConverter;
	private final OrderTemplateConverter orderTemplateConverter;
	
	/**
	 * 創建新的 Industry 實體。
	 *
	 * @param industryRequest 包含 Industry 資訊的請求物件。
	 * @return 創建後的 IndustryResponse 物件。
	 * @throws EntityNotFoundException 如果關聯的父 Industry 不存在。
	 */
	@Transactional
	@Override
	public IndustryResponse create(IndustryRequest industryRequest) throws EntityNotFoundException {
		Industry industry = industryConverter.toEntity(industryRequest);

		if (industryRequest.getParentId() != null) {
			Industry parent = industryRepository.findById(industryRequest.getParentId())
					.orElseThrow(() -> new EntityNotFoundException("Parent industry not found with id: " + industryRequest.getParentId()));
			industry.setParentIndustry(parent);
		}

		Industry savedIndustry = industryRepository.save(industry);
		return industryConverter.toResponse(savedIndustry);
	}

	/**
	 * 更新現有的 Industry 實體。
	 *
	 * @param id 要更新的 Industry 的 ID。
	 * @param industryRequest 包含更新的 Industry 資訊的請求物件。
	 * @return 更新後的 IndustryResponse 物件。
	 * @throws EntityNotFoundException 如果要更新的 Industry 或其關聯的父 Industry 不存在。
	 */
	@Transactional
	@Override
	public IndustryResponse update(Long id, IndustryRequest industryRequest) throws EntityNotFoundException {
		Industry existingIndustry = industryRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Industry not found with id: " + id));

		existingIndustry.setName(industryRequest.getName());
		existingIndustry.setTitle(industryRequest.getTitle());
		existingIndustry.setDescription(industryRequest.getDescription());
		existingIndustry.setMeta(industryRequest.getMeta());
		existingIndustry.setIcon(industryRequest.getIcon());
		
		if (industryRequest.getParentId() != null) {
			Industry parent = industryRepository.findById(industryRequest.getParentId())
					.orElseThrow(() -> new EntityNotFoundException("Parent industry not found with id: " + industryRequest.getParentId()));
			existingIndustry.setParentIndustry(parent);
		} else {
			existingIndustry.setParentIndustry(null);
		}

		Industry updatedIndustry = industryRepository.save(existingIndustry);
		return industryConverter.toResponse(updatedIndustry);
	}

	/**
	 * 獲取指定 ID 的 Industry 詳細資訊。
	 *
	 * @param id Industry 的 ID。
	 * @return IndustryResponse 物件。
	 * @throws EntityNotFoundException 如果找不到指定 ID 的 Industry。
	 */
	@Override
	@Transactional(readOnly = true)
	public IndustryResponse detail(Long id) throws EntityNotFoundException {
		Industry industry = industryRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Industry not found with id: " + id));
		return industryConverter.toResponse(industry);
	}

	/**
	 * 列出所有 Industry。
	 *
	 * @return IndustryResponse 物件列表。
	 */
	@Override
	@Transactional(readOnly = true)
	public List<IndustryResponse> list() {
		List<Industry> industries = industryRepository.findAll();	
		return industries.stream()
			.map(industryConverter::toResponse)
			.collect(Collectors.toList());
	}

	/**
	 * 根據 ID 刪除 Industry。
	 *
	 * @param id 要刪除的 Industry 的 ID。
	 * @throws EntityNotFoundException 如果找不到指定 ID 的 Industry。
	 */
	@Transactional
	@Override
	public void delete(Long id) throws EntityNotFoundException {
		Industry industry = industryRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Industry not found with id: " + id));
		industryRepository.delete(industry); 
	}

	/**
	 * 根據名稱查找 Industry。
	 *
	 * @param name Industry 的名稱。
	 * @return IndustryResponse 物件。
	 * @throws EntityNotFoundException 如果找不到具有該名稱的 Industry。
	 */
	@Override
	@Transactional(readOnly = true)
	public IndustryResponse findByName(String name) throws EntityNotFoundException {
		Industry industry = industryRepository.findByName(name);
		if (industry == null) {
			throw new EntityNotFoundException("Industry not found with name: " + name);
		}
		return industryConverter.toResponse(industry);
	}

	/**
	 * 根據父產業名稱查詢 OrderTemplate
	 *
	 * @param parentIndustryName 父產業名稱
	 * @param region 區域篩選（可為 null）
	 * @param page 頁碼（從 0 開始）
	 * @param size 每頁大小
	 * @return OrderTemplateResponse 列表
	 * @throws EntityNotFoundException 當找不到對應的產業時
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OrderTemplateResponse> getOrderTemplatesByParentIndustry(
			String parentIndustryName, String region, int page, int size)
			throws EntityNotFoundException {
		
		log.info("查詢父產業的 OrderTemplates - parentIndustryName: {}, region: {}",
				parentIndustryName, region);
		
		// 1. 根據名稱找到產業
		Industry parentIndustry = findIndustryByName(parentIndustryName);
		
		if (parentIndustry == null) {
			throw new EntityNotFoundException(
				String.format("找不到產業 - name: %s", parentIndustryName));
		}
		
		// 2. 獲取所有子產業的 ID
		List<Long> industryIds = new ArrayList<>();
		industryIds.add(parentIndustry.getId());
		
		// 收集所有子產業的 ID
		if (!CollectionUtils.isEmpty(parentIndustry.getChildIndustries())) {
			industryIds.addAll(parentIndustry.getChildIndustries().stream()
					.map(Industry::getId)
					.collect(Collectors.toList()));
		}
		
		// 3. 建立分頁請求
		PageRequest pageRequest = PageRequest.of(page, size);
		
		// 4. 根據是否有區域篩選來查詢
		Page<OrderTemplate> orderTemplates;
		if (StringUtils.hasText(region)) {
			// 需要自訂查詢，同時篩選產業 ID 和區域
			orderTemplates = findOrderTemplatesByIndustryIdsAndRegion(industryIds, region, pageRequest);
		} else {
			// 只根據產業 ID 查詢
			orderTemplates = orderTemplateRepository.findByIndustryIdInAndEnabledTrue(industryIds, pageRequest);
		}
		
		// 5. 轉換為 Response
		List<OrderTemplateResponse> responses = orderTemplates.getContent().stream()
				.map(orderTemplateConverter::entityToResponse)
				.collect(Collectors.toList());
		
		log.info("找到 {} 個 OrderTemplates", responses.size());
		
		return responses;
	}
	
	/**
	 * 根據產業階層查詢 OrderTemplate
	 *
	 * @param parentIndustryName 父產業名稱
	 * @param childIndustryName 子產業名稱
	 * @param region 區域篩選（可為 null）
	 * @param page 頁碼（從 0 開始）
	 * @param size 每頁大小
	 * @return OrderTemplateResponse 列表
	 * @throws EntityNotFoundException 當找不到對應的產業時
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OrderTemplateResponse> getOrderTemplatesByIndustryHierarchy(
			String parentIndustryName, String childIndustryName,
			String region, int page, int size)
			throws EntityNotFoundException {
		
		log.info("查詢產業階層的 OrderTemplates - parent: {}, child: {}, region: {}",
				parentIndustryName, childIndustryName, region);
		
		// 1. 找到父產業
		Industry parentIndustry = findIndustryByName(parentIndustryName);
		
		if (parentIndustry == null) {
			throw new EntityNotFoundException(
				String.format("找不到父產業 - name: %s", parentIndustryName));
		}
		
		// 2. 在子產業中找到匹配的產業
		Industry targetIndustry = null;
		
		// 先在子產業中尋找
		if (!CollectionUtils.isEmpty(parentIndustry.getChildIndustries())) {
			for (Industry child : parentIndustry.getChildIndustries()) {
				if (isIndustryMatchByName(child, childIndustryName)) {
					targetIndustry = child;
					break;
				}
			}
		}
		
		if (targetIndustry == null) {
			throw new EntityNotFoundException(
				String.format("在父產業 %s 下找不到子產業 - name: %s",
					parentIndustryName, childIndustryName));
		}
		
		// 3. 建立分頁請求
		PageRequest pageRequest = PageRequest.of(page, size);
		
		// 4. 查詢 OrderTemplate - 必須匹配 name (subindustry)
		Page<OrderTemplate> orderTemplates;
		if (StringUtils.hasText(region)) {
			// 需要同時篩選產業、名稱和區域
			orderTemplates = orderTemplateRepository.findByProviderRegionAndIndustryIdAndNameContainingAndEnabledTrue(
					region, targetIndustry.getId(), childIndustryName, pageRequest);
		} else {
			// 只根據產業和名稱查詢
			orderTemplates = orderTemplateRepository.findByIndustryIdAndNameAndEnabledTrue(
					targetIndustry.getId(), childIndustryName, pageRequest);
		}
		
		// 5. 轉換為 Response
		List<OrderTemplateResponse> responses = orderTemplates.getContent().stream()
				.map(orderTemplateConverter::entityToResponse)
				.collect(Collectors.toList());
		
		log.info("找到 {} 個符合條件的 OrderTemplates", responses.size());
		
		return responses;
	}
	
	/**
	 * 根據名稱查找產業
	 *
	 * @param name 產業名稱
	 * @return 找到的產業，或 null
	 */
	private Industry findIndustryByName(String name) {
		// 1. 先嘗試用英文名稱查找
		Industry industry = industryRepository.findByName(name);
		if (industry != null) {
			return industry;
		}
		
		// 2. 嘗試用英文標題查找
		Optional<Industry> byTitle = industryRepository.findByNameOrTitle(name, name);
		if (byTitle.isPresent()) {
			return byTitle.get();
		}
		
		return null;
	}
	
	/**
	 * 檢查產業是否匹配指定的名稱
	 *
	 * @param industry 產業實體
	 * @param name 要匹配的名稱
	 * @return 是否匹配
	 */
	private boolean isIndustryMatchByName(Industry industry, String name) {
		// 檢查英文名稱和標題
		return name.equalsIgnoreCase(industry.getName()) ||
			   name.equalsIgnoreCase(industry.getTitle());
	}
	
	/**
	 * 根據產業ID列表和區域查詢OrderTemplate
	 *
	 * @param industryIds 產業ID列表
	 * @param region 區域
	 * @param pageRequest 分頁請求
	 * @return OrderTemplate分頁結果
	 */
	private Page<OrderTemplate> findOrderTemplatesByIndustryIdsAndRegion(
			List<Long> industryIds, String region, PageRequest pageRequest) {
		// 這裡需要根據實際的OrderTemplateRepository方法來實現
		// 假設有這樣的方法存在
		return orderTemplateRepository.findByIndustryIdInAndProviderRegionAndEnabledTrue(
				industryIds, region, pageRequest);
	}

	/**
	 * 獲取指定產業的所有子產業
	 *
	 * @param parentName 父產業名稱
	 * @return 子產業列表
	 * @throws EntityNotFoundException 當找不到對應的產業時
	 */
	@Override
	@Transactional(readOnly = true)
	public List<IndustryResponse> getSubIndustries(String parentName)
			throws EntityNotFoundException {
		
		log.info("獲取子產業 - parentName: {}", parentName);
		
		// 1. 找到父產業
		Industry parentIndustry = findIndustryByName(parentName);
		
		if (parentIndustry == null) {
			throw new EntityNotFoundException(
				String.format("找不到父產業 - name: %s", parentName));
		}
		
		// 2. 獲取子產業列表
		List<Industry> subIndustries = industryRepository.findByParentIndustryIdAndEnabledTrue(parentIndustry.getId());
		
		// 3. 轉換為Response
		List<IndustryResponse> responses = subIndustries.stream()
				.map(industryConverter::toResponse)
				.collect(Collectors.toList());
		
		log.info("找到 {} 個子產業", responses.size());
		
		return responses;
	}

	/**
		* 列出所有父產業
		* 父產業定義為 parentIndustry 為 null 的產業，按名稱排序
		*
		* @return 父產業列表，按名稱升序排列
		*/
	@Override
	@Transactional(readOnly = true)
	public List<IndustryResponse> listParentIndustries() {
		log.info("獲取所有父產業列表");
		
		// 1. 查詢所有父產業（parentIndustry 為 null 且 enabled 為 true）
		List<Industry> parentIndustries = industryRepository.findByParentIndustryIsNullAndEnabledTrueOrderByName();
		
		// 2. 轉換為 IndustryResponse
		List<IndustryResponse> responses = parentIndustries.stream()
				.map(industryConverter::toResponse)
				.collect(Collectors.toList());
		
		log.info("找到 {} 個父產業", responses.size());
		
		return responses;
	}
}
