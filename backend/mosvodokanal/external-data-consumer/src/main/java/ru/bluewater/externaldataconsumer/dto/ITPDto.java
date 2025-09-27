package ru.bluewater.externaldataconsumer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ITPDto {
    @NotNull
    private UUID id;
    @NotNull
    private String number;
}