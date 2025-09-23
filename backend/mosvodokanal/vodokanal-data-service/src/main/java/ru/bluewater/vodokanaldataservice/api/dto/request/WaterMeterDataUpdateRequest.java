package ru.bluewater.vodokanaldataservice.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class WaterMeterDataUpdateRequest {
    // ГВС данные
    private UUID heatMeterIdentifier;
    private UUID firstChannelFlowmeterIdentifier;
    private UUID secondChannelFlowmeterIdentifier;
    @PositiveOrZero
    private Integer gvsFlowValue;

    // ХВС данные
    private UUID waterMeterIdentifier;
    @PositiveOrZero
    private Integer hvsFlowValue;
    @NotNull
    private LocalDateTime measurementTimestamp;
}