package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.WaterMeterDataResponse;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;
import ru.bluewater.vodokanaldataservice.api.exception.ResourceNotFoundException;
import ru.bluewater.vodokanaldataservice.mapper.WaterMeterDataMapper;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;
import ru.bluewater.vodokanaldataservice.repository.WaterMeterDataRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WaterMeterDataService {
    private final WaterMeterDataRepository waterMeterDataRepository;
    private final ITPRepository itpRepository;
    private final WaterMeterDataMapper waterMeterDataMapper;

    public Page<WaterMeterDataResponse> findAll(Pageable pageable) {
        log.debug("Getting all water meter data with pagination: {}", pageable);
        return waterMeterDataRepository.findAll(pageable)
                .map(waterMeterDataMapper::toResponse);
    }

    public WaterMeterDataResponse findById(UUID id) {
        log.debug("Getting water meter data by id: {}", id);
        WaterMeterDataEntity entity = waterMeterDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Water meter data not found with id: " + id));
        return waterMeterDataMapper.toResponse(entity);
    }

    public List<WaterMeterDataResponse> findByItpId(UUID itpId) {
        log.debug("Getting water meter data by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public Page<WaterMeterDataResponse> findByItpIdPaged(UUID itpId, Pageable pageable) {
        log.debug("Getting paged water meter data by ITP id: {} with pagination: {}", itpId, pageable);
        validateITPExists(itpId);
        return waterMeterDataRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId, pageable)
                .map(waterMeterDataMapper::toResponse);
    }

    public List<WaterMeterDataResponse> findByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting water meter data by ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository
                .findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(itpId, startDate, endDate);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public List<WaterMeterDataResponse> findLatestByItpId(UUID itpId) {
        log.debug("Getting latest water meter data by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository.findLatestByItpId(itpId);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public Double getAverageGvsFlowByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting average GVS flow for ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        return waterMeterDataRepository.getAverageGvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
    }

    public Double getAverageHvsFlowByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting average HVS flow for ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        return waterMeterDataRepository.getAverageHvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
    }

    @Transactional
    public WaterMeterDataResponse create(WaterMeterDataCreateRequest request) {
        log.debug("Creating water meter data for ITP id: {}", request.getItpId());

        ITPEntity itp = itpRepository.findById(request.getItpId())
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + request.getItpId()));

        WaterMeterDataEntity entity = waterMeterDataMapper.toEntity(request);
        entity.setItp(itp);

        entity = waterMeterDataRepository.save(entity);
        log.debug("Created water meter data with id: {}", entity.getId());

        return waterMeterDataMapper.toResponse(entity);
    }

    @Transactional
    public WaterMeterDataResponse update(UUID id, WaterMeterDataUpdateRequest request) {
        log.debug("Updating water meter data with id: {}", id);

        WaterMeterDataEntity entity = waterMeterDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Water meter data not found with id: " + id));

        waterMeterDataMapper.updateEntity(entity, request);
        entity = waterMeterDataRepository.save(entity);

        log.debug("Updated water meter data with id: {}", id);
        return waterMeterDataMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting water meter data with id: {}", id);

        if (!waterMeterDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Water meter data not found with id: " + id);
        }

        waterMeterDataRepository.deleteById(id);
        log.debug("Deleted water meter data with id: {}", id);
    }

    private void validateITPExists(UUID itpId) {
        if (!itpRepository.existsById(itpId)) {
            throw new ResourceNotFoundException("ITP not found with id: " + itpId);
        }
    }
}