package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bluewater.integration.type.TimeStepType;
import ru.bluewater.vodokanaldataservice.api.dto.response.FlowDifferenceResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.GvsFlowGraphDataResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.HvsFlowGraphDataResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.StatisticResponse;
import ru.bluewater.vodokanaldataservice.api.exception.BusinessException;
import ru.bluewater.vodokanaldataservice.entity.DistrictEntity;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;
import ru.bluewater.vodokanaldataservice.repository.DistrictRepository;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;
import ru.bluewater.vodokanaldataservice.repository.WaterMeterDataRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {
    private final WaterMeterDataRepository waterMeterDataRepository;
    private final ITPRepository itpRepository;
    private final DistrictRepository districtRepository;


    public StatisticResponse getOverallStatistics(Date startDate, Date endDate, TimeStepType timeStep) {
        List<WaterMeterDataEntity> allData = waterMeterDataRepository
                .findAllByMeasurementTimestampBetweenOrderByMeasurementTimestampAsc(startDate, endDate);
        return computeStatistics(allData, startDate, endDate, timeStep);
    }

    public StatisticResponse getStatisticsByDistrict(String districtName, Date startDate, Date endDate, TimeStepType timeStep) {
        DistrictEntity district = districtRepository.findByName(districtName)
                .orElseThrow(() -> new BusinessException("District not found"));
        List<ITPEntity> itpsInDistrict = district.getMkdEntityList().stream()
                .map(MKDEntity::getItp)
                .toList();
        List<WaterMeterDataEntity> data = new ArrayList<>();
        for (ITPEntity itp : itpsInDistrict) {
            data.addAll(waterMeterDataRepository
                    .findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampAsc(itp.getId(), startDate, endDate));
        }
        return computeStatistics(data, startDate, endDate, timeStep);
    }

    public StatisticResponse getStatisticsByITP(UUID itpId, Date startDate, Date endDate, TimeStepType timeStep) {
        if (!itpRepository.existsById(itpId)) {
            throw new BusinessException("ITP not found");
        }
        List<WaterMeterDataEntity> data = waterMeterDataRepository
                .findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampAsc(itpId, startDate, endDate);
        return computeStatistics(data, startDate, endDate, timeStep);
    }

    private StatisticResponse computeStatistics(List<WaterMeterDataEntity> data, Date startDate, Date endDate, TimeStepType timeStep) {
        if (data.isEmpty()) {
            throw new BusinessException("Statistics for this period is empty");
        }

        List<WaterMeterDataEntity> aggregatedData = aggregateDataByTimeStep(data, timeStep);

        List<GvsFlowGraphDataResponse> gvsFlowGraph = aggregatedData.stream()
                .map(d -> GvsFlowGraphDataResponse.builder()
                        .timestamp(d.getMeasurementTimestamp())
                        .returnFlow(d.getGvsSecondChannelFlowValue())
                        .consumption(d.getGvsConsumptionFlowValue())
                        .supply(d.getGvsFirstChannelFlowValue())
                        .build())
                .toList();

        List<HvsFlowGraphDataResponse> hvsFlowGraph = aggregatedData.stream()
                .map(d -> HvsFlowGraphDataResponse.builder()
                        .timestamp(d.getMeasurementTimestamp())
                        .consumption(d.getHvsFlowValue())
                        .build())
                .toList();

        List<FlowDifferenceResponse> flowDifferences = aggregatedData.stream()
                .map(d -> FlowDifferenceResponse.builder()
                        .timestamp(d.getMeasurementTimestamp())
                        .hvsFlow(d.getHvsFlowValue())
                        .gvsConsumption(d.getGvsConsumptionFlowValue())
                        .difference(d.getGvsConsumptionFlowValue() - d.getHvsFlowValue())
                        .build())
                .toList();

        Float totalGvsConsumption = (float) data.stream()
                .mapToDouble(d -> d.getGvsConsumptionFlowValue() != null ? d.getGvsConsumptionFlowValue() : 0)
                .sum();
        Float totalGvsSupply = (float) data.stream()
                .mapToDouble(d -> d.getGvsFirstChannelFlowValue() != null ? d.getGvsFirstChannelFlowValue() : 0)
                .sum();
        Float totalGvsReturn = (float) data.stream()
                .mapToDouble(d -> d.getGvsSecondChannelFlowValue() != null ? d.getGvsSecondChannelFlowValue() : 0)
                .sum();
        Float totalHvsFlow = (float) data.stream()
                .mapToDouble(d -> d.getHvsFlowValue() != null ? d.getHvsFlowValue() : 0)
                .sum();
        Float averageGvsConsumption = (float) data.stream()
                .mapToDouble(d -> d.getGvsConsumptionFlowValue() != null ? d.getGvsConsumptionFlowValue() : 0)
                .average()
                .orElse(0.0);
        Float averageGvsSupply = (float) data.stream()
                .mapToDouble(d -> d.getGvsFirstChannelFlowValue() != null ? d.getGvsFirstChannelFlowValue() : 0)
                .average()
                .orElse(0.0);
        Float averageGvsReturn = (float) data.stream()
                .mapToDouble(d -> d.getGvsSecondChannelFlowValue() != null ? d.getGvsSecondChannelFlowValue() : 0)
                .average()
                .orElse(0.0);
        Float averageHvsFlow = (float) data.stream()
                .mapToDouble(d -> d.getHvsFlowValue() != null ? d.getHvsFlowValue() : 0)
                .average()
                .orElse(0.0);

        return StatisticResponse.builder()
                .gvsFlowGraph(gvsFlowGraph)
                .hvsFlowGraph(hvsFlowGraph)
                .flowDifferences(flowDifferences)
                .totalGvsConsumption(totalGvsConsumption)
                .totalGvsSupply(totalGvsSupply)
                .totalGvsReturn(totalGvsReturn)
                .totalHvsFlow(totalHvsFlow)
                .averageGvsConsumption(averageGvsConsumption)
                .averageGvsSupply(averageGvsSupply)
                .averageGvsReturn(averageGvsReturn)
                .averageHvsFlow(averageHvsFlow)
                .startDate(startDate)
                .endDate(endDate)
                .timeStep(timeStep)
                .build();
    }

    private List<WaterMeterDataEntity> aggregateDataByTimeStep(List<WaterMeterDataEntity> data, TimeStepType timeStep) {
        Map<String, List<WaterMeterDataEntity>> groupedData = data.stream()
                .collect(Collectors.groupingBy(
                        entity -> truncateTimestamp(entity.getMeasurementTimestamp(), timeStep),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return groupedData.entrySet().stream()
                .map(entry -> aggregateGroup(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
    }

    private String truncateTimestamp(Date timestamp, TimeStepType timeStep) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        switch (timeStep) {
            case HOUR:
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case DAY:
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case WEEK:
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case MONTH:
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
        }
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cal.getTime());
    }

    private WaterMeterDataEntity aggregateGroup(List<WaterMeterDataEntity> group, String truncatedTimestamp) {
        WaterMeterDataEntity aggregated = new WaterMeterDataEntity();

        try {
            aggregated.setMeasurementTimestamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(truncatedTimestamp));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        float gvsConsumptionSum = group.stream()
                .map(WaterMeterDataEntity::getGvsConsumptionFlowValue)
                .filter(Objects::nonNull)
                .reduce(0f, Float::sum);
        aggregated.setGvsConsumptionFlowValue(gvsConsumptionSum);

        float hvsFlowSum = group.stream()
                .map(WaterMeterDataEntity::getHvsFlowValue)
                .filter(Objects::nonNull)
                .reduce(0f, Float::sum);
        aggregated.setHvsFlowValue(hvsFlowSum);

        float gvsFirstChannelSum = group.stream()
                .map(WaterMeterDataEntity::getGvsFirstChannelFlowValue)
                .filter(Objects::nonNull)
                .reduce(0f, Float::sum);
        aggregated.setGvsFirstChannelFlowValue(gvsFirstChannelSum);

        float gvsSecondChannelSum = group.stream()
                .map(WaterMeterDataEntity::getGvsSecondChannelFlowValue)
                .filter(Objects::nonNull)
                .reduce(0f, Float::sum);
        aggregated.setGvsSecondChannelFlowValue(gvsSecondChannelSum);

        return aggregated;
    }
}
