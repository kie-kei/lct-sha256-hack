package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;
import ru.bluewater.integration.type.ProbabilityType;

import java.util.Map;
import java.util.UUID;

@Data
public class AccidentStatisticsResponse {
    private UUID itpId;
    private String itpNumber;
    private Long totalAccidents;
    private Map<ProbabilityType, Long> accidentsByProbability;
}