package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.*;
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPDetailResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPResponse;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MKDMapper.class, WaterMeterDataMapper.class})
public interface ITPMapper {
    ITPResponse toResponse(ITPEntity entity);

    ITPDetailResponse toDetailResponse(ITPEntity entity);

    List<ITPResponse> toResponseList(List<ITPEntity> entities);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "waterMeterData", ignore = true)
    ITPEntity toEntity(ITPCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mkd", ignore = true)
    @Mapping(target = "waterMeterData", ignore = true)
    void updateEntity(@MappingTarget ITPEntity entity, ITPUpdateRequest request);
}