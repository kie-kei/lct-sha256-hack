package ru.bluewater.vodokanaldataservice.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.bluewater.integration.type.ProbabilityType;

import java.util.Date;
import java.util.UUID;

@Data
public class AccidentCreateRequest {
    @NotNull
    private UUID itpId;
    @NotNull
    private Date measurementTimestamp;
    private ProbabilityType probabilityType;

    // Приход ГВС
    private Boolean isGvsFirstChannelFlowAnomaly;
    private Float gvsStandardFirstChannelFlowValue;
    private Float gvsActualFirstChannelFlowValue;

    // Обратка ГВС
    private Boolean isGvsSecondChannelFlowAnomaly;
    private Float gvsStandardSecondChannelFlowValue;
    private Float gvsActualSecondChannelFlowValue;

    // Потребление ХВС
    private Boolean isHvsConsumptionFlowAnomaly;
    private Float hvsStandardConsumptionFlowValue;
    private Float hvsActualConsumptionFlowValue;

    // Несовпадение потоков
    private Boolean isHvsGvsConsumptionFlowsAnomaly;
    private Float hvsGvsConsumptionFlowsDelta;

    // Соотношение ГВС каналов
    private Boolean isGvsChannelsFlowsRatioAnomaly;
    private Float gvsChannelsFlowsRatio;

    // Отрицательное соотношение ГВС
    private Boolean isGvsChannelsFlowsNegativeRatioAnomaly;
    private Float gvsChannelsFlowsNegativeRatio;
}