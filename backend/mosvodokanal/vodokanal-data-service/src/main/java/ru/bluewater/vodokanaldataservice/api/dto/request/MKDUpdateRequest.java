package ru.bluewater.vodokanaldataservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MKDUpdateRequest {
    @NotBlank
    private String address;
    @NotBlank
    private String fias;
    @NotBlank
    private String unom;
    private BigDecimal latitude;
    private BigDecimal longitude;
}