package ru.bluewater.vodokanaldataservice.api.dto.request;

import lombok.Data;
import ru.bluewater.integration.type.ProbabilityType;

import java.util.Date;

@Data
public class AccidentUpdateRequest {
    private Date measurementTimestamp;
    private ProbabilityType probabilityType;

    private Boolean isGvsFirstChannelFlowAnomaly;
    private Float gvsStandardFirstChannelFlowValue;
    private Float gvsActualFirstChannelFlowValue;

    private Boolean isGvsSecondChannelFlowAnomaly;
    private Float gvsStandardSecondChannelFlowValue;
    private Float gvsActualSecondChannelFlowValue;

    private Boolean isHvsConsumptionFlowAnomaly;
    private Float hvsStandardConsumptionFlowValue;
    private Float hvsActualConsumptionFlowValue;

    private Boolean isHvsGvsConsumptionFlowsAnomaly;
    private Float hvsGvsConsumptionFlowsDelta;

    private Boolean isGvsChannelsFlowsRatioAnomaly;
    private Float gvsChannelsFlowsRatio;

    private Boolean isGvsChannelsFlowsNegativeRatioAnomaly;
    private Float gvsChannelsFlowsNegativeRatio;
}