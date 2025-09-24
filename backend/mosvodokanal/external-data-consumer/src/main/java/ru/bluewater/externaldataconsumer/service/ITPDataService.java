package ru.bluewater.externaldataconsumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bluewater.externaldataconsumer.api.dto.request.ITPDataRequest;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;
import ru.bluewater.externaldataconsumer.export.ITPDataExporter;
import ru.bluewater.externaldataconsumer.mapper.ITPDataMapper;
import ru.bluewater.externaldataconsumer.model.*;
import ru.bluewater.externaldataconsumer.validator.ITPDataValidator;

@Service
@RequiredArgsConstructor
@Slf4j
public class ITPDataService {
    private final ITPDataExporter itpDataExporter;
    private final ITPDataMapper itpDataMapper;
    private final ITPDataValidator itpDataValidator;

    public void processITPData(ITPDataRequest request) throws ITPDataException {
        log.debug("Processing ITP data for ITP ID: {}", request.getItp().getId());

        try {
            ITPData itpData = itpDataMapper.toModel(request);

            itpDataValidator.validate(itpData);

            itpDataExporter.exportITPData(itpData.getITPId().toString(), itpData);

            log.debug("Successfully processed ITP data for ITP ID: {}", itpData.getITPId());
        } catch (ITPDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing ITP data", e);
            throw new ITPDataException("Failed to process ITP data: " + e.getMessage());
        }
    }
}