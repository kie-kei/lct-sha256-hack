package ru.bluewater.vodokanaldataservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ITPUpdateRequest {
    @NotBlank
    private String number;
}