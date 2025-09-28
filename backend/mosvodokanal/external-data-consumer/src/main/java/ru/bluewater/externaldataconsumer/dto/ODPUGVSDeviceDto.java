package ru.bluewater.externaldataconsumer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ODPUGVSDeviceDto {
    @NotNull
    private UUID heatMeterIdentifier;
    @NotNull
    private UUID firstChannelFlowmeterIdentifier;
    @NotNull
    private UUID secondChannelFlowmeterIdentifier;
    @NotNull
    private Float firstChannelFlowValue;
    @NotNull
    private Float secondChannelFlowValue;
}