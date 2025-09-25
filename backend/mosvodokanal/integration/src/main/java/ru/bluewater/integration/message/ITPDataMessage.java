package ru.bluewater.integration.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITPDataMessage {
    private ITPMessage itp;
    private MKDMessage mkd;
    private List<ODPUGVSDeviceMessage> odpuGvsDevice;
    private List<WaterMeterXVSITPMessage> waterMeters;
    private String latitude;
    private String longitude;
    private Date timestamp;

    public UUID getITPId() {
        return itp != null ? itp.getId() : null;
    }
}
