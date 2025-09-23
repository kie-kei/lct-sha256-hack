package ru.bluewater.integration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITPData {
    private ITP itp;
    private MKD mkd;
    private List<ODPUGVSDevice> odpuGvsDevices;
    private List<WaterMeterXVSITP> waterMeters;
    private String latitude;
    private String longitude;
    private Date timestamp;

    public UUID getITPId() {
        return itp != null ? itp.getId() : null;
    }
}
