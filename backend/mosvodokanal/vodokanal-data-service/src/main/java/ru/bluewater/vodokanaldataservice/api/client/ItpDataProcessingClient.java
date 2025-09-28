package ru.bluewater.vodokanaldataservice.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.response.NominatimResponse;
import ru.bluewater.vodokanaldataservice.api.exception.CoordinatesNotFoundException;

@Component
public class ItpDataProcessingClient {
    @Value("${itp-data-processing.url}")
    private String itpDataProcessingUrl;
    private final RestTemplate restTemplate;

    public ItpDataProcessingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NominatimResponse getCoordinatesByAddress(String address) throws CoordinatesNotFoundException {
        String url = itpDataProcessingUrl + "/api/v1/nominatim/coordinates?address=" + address;

        NominatimResponse response = restTemplate.getForObject(url, NominatimResponse.class);

        if (response.getLat() == null || response.getLon() == null) {
            throw new CoordinatesNotFoundException();
        }

        return response;
    }
}
