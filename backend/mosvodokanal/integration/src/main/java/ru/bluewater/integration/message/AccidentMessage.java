package ru.bluewater.integration.message;

import lombok.Data;
import ru.bluewater.integration.type.ProbabilityType;

import java.util.Date;
import java.util.UUID;

@Data
public class AccidentMessage {
    private UUID itpId;
    private Date measurementTimestamp;
    private ProbabilityType probabilityType;

    // ГВС Приход
    private Boolean isGvsFirstChannelFlowAnomaly;
    private Float gvsStandardFirstChannelFlowValue;
    private Float gvsActualFirstChannelFlowValue;

    // ГВС Обратка
    private Boolean isGvsSecondChannelFlowAnomaly;
    private Float gvsStandardSecondChannelFlowValue;
    private Float gvsActualSecondChannelFlowValue;

    // ХВС Потребление
    private Boolean isHvsConsumptionFlowAnomaly;
    private Float hvsStandardConsumptionFlowValue;
    private Float hvsActualConsumptionFlowValue;

    // Несовпадение потоков
    private Boolean isHvsGvsConsumptionFlowsAnomaly;
    private Float hvsGvsConsumptionFlowsDelta;

    // Соотношения ГВС
    private Boolean isGvsChannelsFlowsRatioAnomaly;
    private Float gvsChannelsFlowsRatio;

    private Boolean isGvsChannelsFlowsNegativeRatioAnomaly;
    private Float gvsChannelsFlowsNegativeRatio;
}