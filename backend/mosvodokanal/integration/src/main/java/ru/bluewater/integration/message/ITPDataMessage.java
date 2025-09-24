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
    private ITPMessage itpMessage;
    private MKDMessage mkdMessage;
    private List<ODPUGVSDeviceMessage> odpuGvsDeviceMessages;
    private List<WaterMeterXVSITPMessage> waterMeters;
    private String latitude;
    private String longitude;
    private Date timestamp;

    public UUID getITPId() {
        return itpMessage != null ? itpMessage.getId() : null;
    }
}
