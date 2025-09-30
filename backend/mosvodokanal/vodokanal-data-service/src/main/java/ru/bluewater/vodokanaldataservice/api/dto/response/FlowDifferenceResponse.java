package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FlowDifferenceResponse {
    private Date timestamp;
    private Float gvsConsumption; // подача ГВС
    private Float hvsFlow; // ХВС
    private Float difference; // разность (gvsSupply - hvsFlow)
}
