package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bluewater.integration.type.ProbabilityType;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.AccidentResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.AccidentStatisticsResponse;
import ru.bluewater.vodokanaldataservice.entity.AccidentEntity;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.api.exception.ResourceNotFoundException;
import ru.bluewater.vodokanaldataservice.mapper.AccidentMapper;
import ru.bluewater.vodokanaldataservice.repository.AccidentRepository;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccidentService {
    private final AccidentRepository accidentRepository;
    private final ITPRepository itpRepository;
    private final AccidentMapper accidentMapper;

    public List<AccidentResponse> findAll() {
        log.debug("Getting all accidents");
        return accidentRepository.findAll().stream()
                .map(accidentMapper::toResponse)
                .toList();
    }

    public Page<AccidentResponse> findAll(Pageable pageable) {
        log.debug("Getting all accidents with pagination: {}", pageable);
        return accidentRepository.findAll(pageable)
                .map(accidentMapper::toResponse);
    }

    public AccidentResponse findById(UUID id) {
        log.debug("Getting accident by id: {}", id);
        AccidentEntity entity = accidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accident not found with id: " + id));
        return accidentMapper.toResponse(entity);
    }

    public List<AccidentResponse> findByItpId(UUID itpId) {
        log.debug("Getting accidents by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<AccidentEntity> entities = accidentRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId);
        return accidentMapper.toResponseList(entities);
    }

    public Page<AccidentResponse> findByItpId(UUID itpId, Pageable pageable) {
        log.debug("Getting paged accidents by ITP id: {}", itpId);
        validateITPExists(itpId);
        return accidentRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId, pageable)
                .map(accidentMapper::toResponse);
    }

    public List<AccidentResponse> findByProbabilityType(ProbabilityType probabilityType) {
        log.debug("Getting accidents by probability type: {}", probabilityType);
        List<AccidentEntity> entities = accidentRepository.findByProbabilityTypeOrderByMeasurementTimestampDesc(probabilityType);
        return accidentMapper.toResponseList(entities);
    }

    public Page<AccidentResponse> findByProbabilityType(ProbabilityType probabilityType, Pageable pageable) {
        log.debug("Getting paged accidents by probability type: {}", probabilityType);
        return accidentRepository.findByProbabilityTypeOrderByMeasurementTimestampDesc(probabilityType, pageable)
                .map(accidentMapper::toResponse);
    }

    public List<AccidentResponse> findByDateRange(Date startDate, Date endDate) {
        log.debug("Getting accidents by date range: {} to {}", startDate, endDate);
        List<AccidentEntity> entities = accidentRepository.findByMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(startDate, endDate);
        return accidentMapper.toResponseList(entities);
    }

    public Page<AccidentResponse> findByDateRange(Date startDate, Date endDate, Pageable pageable) {
        log.debug("Getting paged accidents by date range: {} to {}", startDate, endDate);
        return accidentRepository.findByMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(startDate, endDate, pageable)
                .map(accidentMapper::toResponse);
    }

    public List<AccidentResponse> findByItpIdAndDateRange(UUID itpId, Date startDate, Date endDate) {
        log.debug("Getting accidents by ITP id: {} and date range: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        List<AccidentEntity> entities = accidentRepository.findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(itpId, startDate, endDate);
        return accidentMapper.toResponseList(entities);
    }

    public Page<AccidentResponse> findByItpIdAndDateRange(UUID itpId, Date startDate, Date endDate, Pageable pageable) {
        log.debug("Getting paged accidents by ITP id: {} and date range: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        return accidentRepository.findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(itpId, startDate, endDate, pageable)
                .map(accidentMapper::toResponse);
    }

    public List<AccidentResponse> findAllByAnomalies(
            Boolean isGvsFirstChannelFlowAnomaly,
            Boolean isGvsSecondChannelFlowAnomaly,
            Boolean isHvsConsumptionFlowAnomaly,
            Boolean isHvsGvsConsumptionFlowsAnomaly,
            Boolean isGvsChannelsFlowsRatioAnomaly,
            Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    ) {
        log.debug("Getting all accidents with anomalies");
        List<AccidentEntity> entities = accidentRepository.findAllByAnomalies(
                isGvsFirstChannelFlowAnomaly,
                isGvsSecondChannelFlowAnomaly,
                isHvsConsumptionFlowAnomaly,
                isHvsGvsConsumptionFlowsAnomaly,
                isGvsChannelsFlowsRatioAnomaly,
                isGvsChannelsFlowsNegativeRatioAnomaly
        );
        return accidentMapper.toResponseList(entities);
    }

    public List<AccidentResponse> findAnomaliesByItpId(
            UUID itpId,
            Boolean isGvsFirstChannelFlowAnomaly,
            Boolean isGvsSecondChannelFlowAnomaly,
            Boolean isHvsConsumptionFlowAnomaly,
            Boolean isHvsGvsConsumptionFlowsAnomaly,
            Boolean isGvsChannelsFlowsRatioAnomaly,
            Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    ) {
        log.debug("Getting accidents with anomalies by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<AccidentEntity> entities = accidentRepository.findAllByItpIdAndAnomalies(
                itpId,
                isGvsFirstChannelFlowAnomaly,
                isGvsSecondChannelFlowAnomaly,
                isHvsConsumptionFlowAnomaly,
                isHvsGvsConsumptionFlowsAnomaly,
                isGvsChannelsFlowsRatioAnomaly,
                isGvsChannelsFlowsNegativeRatioAnomaly
        );
        return accidentMapper.toResponseList(entities);
    }

    public AccidentStatisticsResponse getStatisticsByItpId(UUID itpId) {
        log.debug("Getting statistics for ITP id: {}", itpId);
        ITPEntity itp = itpRepository.findById(itpId)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + itpId));

        Long totalAccidents = accidentRepository.countByItpId(itpId);

        Map<ProbabilityType, Long> accidentsByProbability = Arrays.stream(ProbabilityType.values())
                .collect(Collectors.toMap(
                        type -> type,
                        type -> accidentRepository.countByItpIdAndProbabilityType(itpId, type)
                ));

        List<AccidentEntity> latest = accidentRepository.findLatestByItpId(itpId, 1);

        AccidentStatisticsResponse response = new AccidentStatisticsResponse();
        response.setItpId(itpId);
        response.setItpNumber(itp.getNumber());
        response.setTotalAccidents(totalAccidents);
        response.setAccidentsByProbability(accidentsByProbability);

        return response;
    }

    @Transactional
    public AccidentResponse create(AccidentCreateRequest request) {
        log.debug("Creating accident for ITP id: {}", request.getItpId());

        ITPEntity itp = itpRepository.findById(request.getItpId())
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + request.getItpId()));

        AccidentEntity entity = accidentMapper.toEntity(request);
        entity.setItp(itp);

        entity = accidentRepository.save(entity);
        log.debug("Created accident with id: {}", entity.getId());

        return accidentMapper.toResponse(entity);
    }

    @Transactional
    public AccidentResponse update(UUID id, AccidentUpdateRequest request) {
        log.debug("Updating accident with id: {}", id);

        AccidentEntity entity = accidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Accident not found with id: " + id));

        accidentMapper.updateEntity(entity, request);
        entity = accidentRepository.save(entity);

        log.debug("Updated accident with id: {}", id);
        return accidentMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting accident with id: {}", id);

        if (!accidentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Accident not found with id: " + id);
        }

        accidentRepository.deleteById(id);
        log.debug("Deleted accident with id: {}", id);
    }

    private void validateITPExists(UUID itpId) {
        if (!itpRepository.existsById(itpId)) {
            throw new ResourceNotFoundException("ITP not found with id: " + itpId);
        }
    }
}