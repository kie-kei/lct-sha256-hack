package ru.bluewater.externaldataconsumer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class WaterMeterXVSITPDto {
    @NotNull
    private UUID identifier;
    @NotNull
    private Float flowValue;
}