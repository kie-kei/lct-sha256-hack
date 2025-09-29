package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ITPDetailResponse {
    private UUID id;
    private String number;
    private MKDResponse mkd;
    private List<WaterMeterDataResponse> waterMeterData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}