package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.bluewater.integration.type.TimeStepType;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class StatisticResponse {
    private List<GvsFlowGraphDataResponse> gvsFlowGraph; // График прихода и обратки ГВС
    private List<HvsFlowGraphDataResponse> hvsFlowGraph; // График потребления ХВС
    private List<FlowDifferenceResponse> flowDifferences; // Разности ГВС и ХВС
    private Float totalGvsConsumption; // Общее потребление ГВС
    private Float totalGvsSupply; // Общая подача ГВС
    private Float totalGvsReturn; // Общая обратка ГВС
    private Float totalHvsFlow; // Общее потребление ХВС
    private Float averageGvsSupply; // Средняя подача ГВС
    private Float averageGvsReturn; // Средняя обратка ГВС
    private Float averageGvsConsumption; // Среднее потребление ГВС
    private Float averageHvsFlow; // Средний ХВС
    private Date startDate;
    private Date endDate;
    private TimeStepType timeStep;
}
