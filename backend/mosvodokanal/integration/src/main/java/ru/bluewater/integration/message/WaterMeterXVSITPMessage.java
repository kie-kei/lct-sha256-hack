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
public class WaterMeterXVSITPMessage {
    private UUID identifier; // Идентификатор
    private Float flowValue; // Значение расхода
}