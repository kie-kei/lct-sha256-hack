package ru.bluewater.externaldataconsumer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bluewater.externaldataconsumer.api.dto.request.ITPDataRequest;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;
import ru.bluewater.externaldataconsumer.export.ITPDataExporter;
import ru.bluewater.externaldataconsumer.mapper.ITPDataMapper;
import ru.bluewater.externaldataconsumer.validator.ITPDataValidator;
import ru.bluewater.integration.message.ITPDataMessage;

import java.util.Date;

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
            ITPDataMessage itpDataMessage = itpDataMapper.toModel(request);

            itpDataValidator.validate(itpDataMessage);

            enrichITPData(itpDataMessage);

            itpDataExporter.exportITPData(itpDataMessage.getITPId().toString(), itpDataMessage);

            log.debug("Successfully processed ITP data for ITP ID: {}", itpDataMessage.getITPId());
        } catch (ITPDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing ITP data", e);
            throw new ITPDataException("Failed to process ITP data: " + e.getMessage());
        }
    }

    private void enrichITPData(ITPDataMessage data) {
        data.setTimestamp(new Date());
    }
}