package ru.bluewater.externaldataconsumer.mapper;

import org.mapstruct.Mapper;
import ru.bluewater.externaldataconsumer.api.dto.request.ITPDataRequest;
import ru.bluewater.externaldataconsumer.dto.*;
import ru.bluewater.integration.model.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ITPDataMapper {
    ITPData toModel(ITPDataRequest request);
    ITP toModel(ITPDto dto);
    MKD toModel(MKDDto dto);
    List<ODPUGVSDevice> toODPUDeviceList(List<ODPUGVSDeviceDto> dtos);
    ODPUGVSDevice toModel(ODPUGVSDeviceDto dto);
    List<WaterMeterXVSITP> toWaterMeterList(List<WaterMeterXVSITPDto> dtos);
    WaterMeterXVSITP toModel(WaterMeterXVSITPDto dto);
}
