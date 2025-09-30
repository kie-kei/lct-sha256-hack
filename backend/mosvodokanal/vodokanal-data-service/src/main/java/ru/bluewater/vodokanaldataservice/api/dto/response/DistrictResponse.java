package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class DistrictResponse {
    private UUID id;
    private String name;
}
