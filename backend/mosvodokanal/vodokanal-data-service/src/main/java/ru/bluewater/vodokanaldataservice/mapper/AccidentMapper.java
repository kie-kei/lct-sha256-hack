package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.*;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.AccidentResponse;
import ru.bluewater.vodokanaldataservice.entity.AccidentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccidentMapper {
    @Mapping(source = "itp.id", target = "itpId")
    @Mapping(source = "itp.number", target = "itpNumber")
    AccidentResponse toResponse(AccidentEntity entity);

    List<AccidentResponse> toResponseList(List<AccidentEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AccidentEntity toEntity(AccidentCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget AccidentEntity entity, AccidentUpdateRequest request);
}