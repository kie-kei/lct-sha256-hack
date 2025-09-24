package ru.bluewater.integration.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ODPUGVSDeviceMessage {
    private UUID heatMeterIdentifier; // Идентификатор тепловычислителя
    private UUID firstChannelFlowmeterIdentifier; // Идентификатор расходомера первого канала
    private UUID secondChannelFlowmeterIdentifier; // Идентификатор расходомера второго канала
    private Integer flowValue; // Значение расхода
}