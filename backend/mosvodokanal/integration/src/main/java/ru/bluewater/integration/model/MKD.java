package ru.bluewater.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MKD {
    private String address; // Адрес
    private String fias; // ФИАС
    private String unom; // УНОМ
}
