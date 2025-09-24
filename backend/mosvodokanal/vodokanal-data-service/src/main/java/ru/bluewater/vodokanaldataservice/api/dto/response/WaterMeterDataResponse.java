package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WaterMeterDataResponse {
    private UUID id;
    private UUID itpId;

    // ГВС данные
    private UUID heatMeterIdentifier;
    private UUID firstChannelFlowmeterIdentifier;
    private UUID secondChannelFlowmeterIdentifier;
    private Integer gvsFlowValue;

    // ХВС данные
    private UUID waterMeterIdentifier;
    private Integer hvsFlowValue;

    private LocalDateTime measurementTimestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}