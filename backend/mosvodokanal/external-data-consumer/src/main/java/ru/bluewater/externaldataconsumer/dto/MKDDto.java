package ru.bluewater.externaldataconsumer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MKDDto {
    @NotBlank
    private String address;
    @NotBlank
    private String fias;
    @NotBlank
    private String unom;
}