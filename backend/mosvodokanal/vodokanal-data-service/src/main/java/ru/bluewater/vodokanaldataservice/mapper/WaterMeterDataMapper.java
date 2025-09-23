package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.*;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.WaterMeterDataResponse;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WaterMeterDataMapper {
    @Mapping(source = "itp.id", target = "itpId")
    WaterMeterDataResponse toResponse(WaterMeterDataEntity entity);

    List<WaterMeterDataResponse> toResponseList(List<WaterMeterDataEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WaterMeterDataEntity toEntity(WaterMeterDataCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget WaterMeterDataEntity entity, WaterMeterDataUpdateRequest request);
}