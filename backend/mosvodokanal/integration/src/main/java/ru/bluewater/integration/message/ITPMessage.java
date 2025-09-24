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
public class ITPMessage {
    private UUID id; // Идентификатор ИТП
    private String number; // Номер ИТП
}