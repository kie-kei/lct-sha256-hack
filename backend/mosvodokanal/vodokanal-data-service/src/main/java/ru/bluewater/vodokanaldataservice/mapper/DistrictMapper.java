package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.Mapper;
import ru.bluewater.vodokanaldataservice.api.dto.response.DistrictResponse;
import ru.bluewater.vodokanaldataservice.entity.DistrictEntity;

import java.util.List;

@Mapper(componentModel = "spring")

public interface DistrictMapper {
    DistrictResponse toResponse(DistrictEntity districtEntity);
    List<DistrictResponse> toResponseList(List<DistrictEntity> districtEntities);
}
