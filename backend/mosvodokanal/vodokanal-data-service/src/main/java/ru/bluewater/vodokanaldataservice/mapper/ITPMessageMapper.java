package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.*;
import ru.bluewater.integration.message.ITPMessage;
import ru.bluewater.integration.message.MKDMessage;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;

@Mapper(componentModel = "spring")
public interface ITPMessageMapper {
    @Mapping(target = "mkd", ignore = true)
    @Mapping(target = "waterMeterData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ITPEntity toITPEntity(ITPMessage itpMessage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "district", ignore = true)
    MKDEntity toMKDEntity(MKDMessage mkdMessage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itp", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "district", ignore = true)
    void updateMKDEntity(@MappingTarget MKDEntity entity, MKDMessage mkdMessage);
}