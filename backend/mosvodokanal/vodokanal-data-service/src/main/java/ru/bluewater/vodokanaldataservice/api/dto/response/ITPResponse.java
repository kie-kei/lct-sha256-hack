package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ITPResponse {
    private UUID id;
    private String number;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}