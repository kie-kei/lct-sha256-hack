package ru.bluewater.itpdataprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.message.ITPDataMessage;
import ru.bluewater.integration.response.NominatimResponse;
import ru.bluewater.itpdataprocessing.api.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataprocessing.api.exception.InvalidAddressException;
import ru.bluewater.itpdataprocessing.api.exception.NominatimServiceUnavailableException;
import ru.bluewater.itpdataprocessing.export.ProcessedITPDataExporter;
import ru.bluewater.itpdataprocessing.util.NumberUtil;

@Service
public class ITPDataService {
    private final ProcessedITPDataExporter processedITPDataExporter;
    private final NumberUtil numberUtil;
    private final NominatimService nominatimService;

    public ITPDataService(ProcessedITPDataExporter processedITPDataExporter, NumberUtil numberUtil, NominatimService nominatimService) {
        this.processedITPDataExporter = processedITPDataExporter;
        this.numberUtil = numberUtil;
        this.nominatimService = nominatimService;
    }

    public void processItpData(String itpId, ITPDataMessage itpDataMessage) throws InvalidAddressException, NominatimServiceUnavailableException, CoordinatesNotFoundException {
        String address = itpDataMessage.getMkd().getAddress();

        if (address == null || address.trim().isEmpty()) {
            throw new InvalidAddressException();
        }

        NominatimResponse response = nominatimService.getCoordinatesByAddress(address);

        itpDataMessage.getMkd().setLongitude(numberUtil.convertStringToBigDecimal(response.getLon()));
        itpDataMessage.getMkd().setLatitude(numberUtil.convertStringToBigDecimal(response.getLat()));
        itpDataMessage.getMkd().setDistrict(response.getAddress().getSuburb());

        processedITPDataExporter.exportITPData(itpId, itpDataMessage);
    }
}
