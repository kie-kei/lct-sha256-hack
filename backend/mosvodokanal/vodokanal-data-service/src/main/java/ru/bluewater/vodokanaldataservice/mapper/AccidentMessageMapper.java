package ru.bluewater.vodokanaldataservice.mapper;

import org.mapstruct.Mapper;
import ru.bluewater.integration.message.AccidentMessage;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentCreateRequest;

@Mapper(componentModel = "spring")
public interface AccidentMessageMapper {
    AccidentCreateRequest toCreateRequest(AccidentMessage message);
}