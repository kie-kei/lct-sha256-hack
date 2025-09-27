package ru.bluewater.externaldataconsumer.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.bluewater.externaldataconsumer.dto.ITPDto;
import ru.bluewater.externaldataconsumer.dto.MKDDto;
import ru.bluewater.externaldataconsumer.dto.ODPUGVSDeviceDto;
import ru.bluewater.externaldataconsumer.dto.WaterMeterXVSITPDto;

import java.util.List;

@Data
public class ITPDataRequest {
    @NotNull
    @Valid
    private ITPDto itp;
    @NotNull
    @Valid
    private MKDDto mkd;
    private List<ODPUGVSDeviceDto> odpuGvsDevices;
    private List<WaterMeterXVSITPDto> waterMeters;
}
