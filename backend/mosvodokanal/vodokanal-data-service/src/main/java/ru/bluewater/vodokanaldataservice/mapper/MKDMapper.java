package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.*;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.MKDResponse;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MKDMapper {
    @Mapping(source = "itp.id", target = "itpId")
    @Mapping(source = "district.name", target = "district")
    MKDResponse toResponse(MKDEntity entity);

    List<MKDResponse> toResponseList(List<MKDEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MKDEntity toEntity(MKDCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget MKDEntity entity, MKDUpdateRequest request);
}