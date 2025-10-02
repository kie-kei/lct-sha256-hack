package ru.bluewater.vodokanaldataservice.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class WaterMeterDataAveragesResponse {
    private Float avgHvsFlow;
    private Float avgGvsFirstChannelFlow;
    private Float avgGvsSecondChannelFlow;
    private Float avgGvsConsumptionFlow;

    public WaterMeterDataAveragesResponse(Double avgHvsFlow, Double avgGvsFirstChannelFlow,
                                          Double avgGvsSecondChannelFlow, Double avgGvsConsumptionFlow) {
        this.avgHvsFlow = avgHvsFlow != null ? avgHvsFlow.floatValue() : 0.0f;
        this.avgGvsFirstChannelFlow = avgGvsFirstChannelFlow != null ? avgGvsFirstChannelFlow.floatValue() : 0.0f;
        this.avgGvsSecondChannelFlow = avgGvsSecondChannelFlow != null ? avgGvsSecondChannelFlow.floatValue() : 0.0f;
        this.avgGvsConsumptionFlow = avgGvsConsumptionFlow != null ? avgGvsConsumptionFlow.floatValue() : 0.0f;
    }
}
