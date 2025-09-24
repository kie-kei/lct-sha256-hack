package ru.bluewater.externaldataconsumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITP {
    private UUID id; // Идентификатор ИТП
    private String number; // Номер ИТП
}
