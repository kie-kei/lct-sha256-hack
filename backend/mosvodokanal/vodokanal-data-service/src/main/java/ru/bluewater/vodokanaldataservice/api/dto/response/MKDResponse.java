package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MKDResponse {
    private UUID id;
    private String address;
    private String fias;
    private String unom;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private UUID itpId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}