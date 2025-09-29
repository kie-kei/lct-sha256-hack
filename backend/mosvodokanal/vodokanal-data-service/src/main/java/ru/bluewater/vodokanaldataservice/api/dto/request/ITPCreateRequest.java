package ru.bluewater.vodokanaldataservice.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ITPCreateRequest {
    @NotNull
    private UUID id;
    @NotBlank
    private String number;
    @Valid
    private MKDCreateRequest mkd;
}