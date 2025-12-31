package com.casemgr.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.LocationConverter;
import com.casemgr.entity.Location;
import com.casemgr.repository.LocationRepository;
import com.casemgr.request.LocationRequest;
import com.casemgr.response.LocationResponse;
import com.casemgr.service.LocationService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("locationService")
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationConverter locationConverter;

    /**
     * 創建新的 Location 實體。
     *
     * @param locationRequest 包含 Location 資訊的請求物件。
     * @return 創建後的 LocationResponse 物件。
     */
    @Transactional
    @Override
    public LocationResponse create(LocationRequest locationRequest) {
        // 檢查位置名稱是否已存在
        if (locationRepository.existsByLocation(locationRequest.getLocation())) {
            throw new IllegalArgumentException("Location already exists: " + locationRequest.getLocation());
        }

        Location location = locationConverter.toEntity(locationRequest);
        Location savedLocation = locationRepository.save(location);
        return locationConverter.toResponse(savedLocation);
    }

    /**
     * 更新現有的 Location 實體。
     *
     * @param id 要更新的 Location 的 ID。
     * @param locationRequest 包含更新的 Location 資訊的請求物件。
     * @return 更新後的 LocationResponse 物件。
     * @throws EntityNotFoundException 如果要更新的 Location 不存在。
     */
    @Transactional
    @Override
    public LocationResponse update(Long id, LocationRequest locationRequest) throws EntityNotFoundException {
        Location existingLocation = locationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));

        // 檢查位置名稱是否已被其他記錄使用
        Location existingByLocation = locationRepository.findByLocation(locationRequest.getLocation());
        if (existingByLocation != null && !existingByLocation.getId().equals(id)) {
            throw new IllegalArgumentException("Location already exists: " + locationRequest.getLocation());
        }

        existingLocation.setLocation(locationRequest.getLocation());
        Location updatedLocation = locationRepository.save(existingLocation);
        return locationConverter.toResponse(updatedLocation);
    }

    /**
     * 獲取指定 ID 的 Location 詳細資訊。
     *
     * @param id Location 的 ID。
     * @return LocationResponse 物件。
     * @throws EntityNotFoundException 如果找不到指定 ID 的 Location。
     */
    @Override
    @Transactional(readOnly = true)
    public LocationResponse detail(Long id) throws EntityNotFoundException {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));
        return locationConverter.toResponse(location);
    }

    /**
     * 列出所有 Location。
     *
     * @return LocationResponse 物件列表。
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> list() {
        List<Location> locations = locationRepository.findAll();
        return locations.stream()
            .map(locationConverter::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * 根據 ID 刪除 Location。
     *
     * @param id 要刪除的 Location 的 ID。
     * @throws EntityNotFoundException 如果找不到指定 ID 的 Location。
     */
    @Transactional
    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));
        locationRepository.delete(location);
    }

    /**
     * 根據位置名稱查找 Location。
     *
     * @param location Location 的位置名稱。
     * @return LocationResponse 物件。
     * @throws EntityNotFoundException 如果找不到具有該位置名稱的 Location。
     */
    @Override
    @Transactional(readOnly = true)
    public LocationResponse findByLocation(String location) throws EntityNotFoundException {
        Location locationEntity = locationRepository.findByLocation(location);
        if (locationEntity == null) {
            throw new EntityNotFoundException("Location not found with location: " + location);
        }
        return locationConverter.toResponse(locationEntity);
    }
}