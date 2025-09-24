package ru.bluewater.itpdataprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.model.ITPData;
import ru.bluewater.itpdataprocessing.dto.NominatimResponse;
import ru.bluewater.itpdataprocessing.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataprocessing.exception.InvalidAddressException;
import ru.bluewater.itpdataprocessing.exception.NominatimServiceUnavailableException;
import ru.bluewater.itpdataprocessing.export.ProcessedITPDataExporter;

import java.util.Date;

@Service
public class ITPDataService {

    @Value("${nominatim.url}")
    private String nominatimUrl;

    private final RestTemplate restTemplate;

    private final ProcessedITPDataExporter processedITPDataExporter;

    public ITPDataService(RestTemplate restTemplate, ProcessedITPDataExporter processedITPDataExporter) {
        this.restTemplate = restTemplate;
        this.processedITPDataExporter = processedITPDataExporter;
    }

    public void processItpData(String itpId, ITPData itpData) throws CoordinatesNotFoundException, InvalidAddressException, NominatimServiceUnavailableException {
        String address = itpData.getMkd().getAddress();

        if (address == null || address.trim().isEmpty()) {
            throw new InvalidAddressException();
        }

        try {
            String url = nominatimUrl + "/search?q=" + address + "&format=json&limit=1";

            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            if (response.length == 0) {
                throw new CoordinatesNotFoundException();
            }

            itpData.setLongitude(response[0].getLongitude());
            itpData.setLatitude(response[0].getLatitude());

            itpData.setTimestamp(new Date());

            processedITPDataExporter.exportITPData(itpId, itpData);
        } catch (RestClientException e) {
            throw new NominatimServiceUnavailableException();
        }

    }
}
