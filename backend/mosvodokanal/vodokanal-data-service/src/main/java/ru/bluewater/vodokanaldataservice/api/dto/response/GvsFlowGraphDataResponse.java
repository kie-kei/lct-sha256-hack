package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class GvsFlowGraphDataResponse {
    private Date timestamp;
    private Float supply; // подача (приход)
    private Float returnFlow; // обратка
    private Float consumption; // Потребление
}
