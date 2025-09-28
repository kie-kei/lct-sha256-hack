package ru.bluewater.integration.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MKDMessage {
    private String address; // Адрес
    private String fias; // ФИАС
    private String unom; // УНОМ
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String district;
}
