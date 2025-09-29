package ru.bluewater.externaldataconsumer.mapper;

import org.mapstruct.Mapper;
import ru.bluewater.externaldataconsumer.api.dto.request.ITPDataRequest;
import ru.bluewater.externaldataconsumer.dto.*;
import ru.bluewater.integration.message.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITPDataMapper {
    ITPDataMessage toModel(ITPDataRequest request);
    ITPMessage toModel(ITPDto dto);
    MKDMessage toModel(MKDDto dto);
    List<ODPUGVSDeviceMessage> toODPUDeviceList(List<ODPUGVSDeviceDto> dtos);
    ODPUGVSDeviceMessage toModel(ODPUGVSDeviceDto dto);
    List<WaterMeterXVSITPMessage> toWaterMeterList(List<WaterMeterXVSITPDto> dtos);
    WaterMeterXVSITPMessage toModel(WaterMeterXVSITPDto dto);
}
