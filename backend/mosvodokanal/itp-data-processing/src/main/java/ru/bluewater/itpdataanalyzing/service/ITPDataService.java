package ru.bluewater.itpdataanalyzing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.message.ITPDataMessage;
import ru.bluewater.itpdataanalyzing.dto.NominatimResponse;
import ru.bluewater.itpdataanalyzing.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataanalyzing.exception.InvalidAddressException;
import ru.bluewater.itpdataanalyzing.exception.NominatimServiceUnavailableException;
import ru.bluewater.itpdataanalyzing.export.ProcessedITPDataExporter;
import ru.bluewater.itpdataanalyzing.util.NumberUtil;

import java.math.BigDecimal;

@Service
public class ITPDataService {
    @Value("${nominatim.url}")
    private String nominatimUrl;

    private final RestTemplate restTemplate;

    private final ProcessedITPDataExporter processedITPDataExporter;
    private final NumberUtil numberUtil;

    public ITPDataService(RestTemplate restTemplate, ProcessedITPDataExporter processedITPDataExporter, NumberUtil numberUtil) {
        this.restTemplate = restTemplate;
        this.processedITPDataExporter = processedITPDataExporter;
        this.numberUtil = numberUtil;
    }

    public void processItpData(String itpId, ITPDataMessage itpDataMessage) throws InvalidAddressException, NominatimServiceUnavailableException {
        String address = itpDataMessage.getMkd().getAddress();

        if (address == null || address.trim().isEmpty()) {
            throw new InvalidAddressException();
        }

        try {
            String url = nominatimUrl + "/search?q=" + address + "&format=json&limit=1";

            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            if (response.length == 0) {
                throw new CoordinatesNotFoundException();
            }

            itpDataMessage.getMkd().setLongitude(numberUtil.convertStringToBigDecimal(response[0].getLon()));
            itpDataMessage.getMkd().setLatitude(numberUtil.convertStringToBigDecimal(response[0].getLat()));

            processedITPDataExporter.exportITPData(itpId, itpDataMessage);
        } catch (RestClientException e) {
            throw new NominatimServiceUnavailableException();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
