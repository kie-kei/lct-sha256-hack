package ru.bluewater.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterMeterXVSITP {
    private UUID identifier; // Идентификатор
    private Integer flowValue; // Значение расхода
}