package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class HvsFlowGraphDataResponse {
    private Date timestamp;
    private Float consumption; // Потребление
}
