package ru.bluewater.itpdataprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.message.ITPDataMessage;
import ru.bluewater.itpdataprocessing.dto.NominatimResponse;
import ru.bluewater.itpdataprocessing.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataprocessing.exception.InvalidAddressException;
import ru.bluewater.itpdataprocessing.exception.NominatimServiceUnavailableException;
import ru.bluewater.itpdataprocessing.export.ProcessedITPDataExporter;

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

    public void processItpData(String itpId, ITPDataMessage itpDataMessage) throws InvalidAddressException, NominatimServiceUnavailableException {
        String address = itpDataMessage.getMkdMessage().getAddress();

        if (address == null || address.trim().isEmpty()) {
            throw new InvalidAddressException();
        }

        try {
            String url = nominatimUrl + "/search?q=" + address + "&format=json&limit=1";

            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            if (response.length == 0) {
                throw new CoordinatesNotFoundException();
            }

            itpDataMessage.setLongitude(response[0].getLongitude());
            itpDataMessage.setLatitude(response[0].getLatitude());

            processedITPDataExporter.exportITPData(itpId, itpDataMessage);
        } catch (RestClientException e) {
            throw new NominatimServiceUnavailableException();
        } catch (Exception e) {
            throw new RuntimeException("Unhandled exception during requesting nominatim server");
        }
    }
}
