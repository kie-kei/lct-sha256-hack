package ru.bluewater.itpdataprocessing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.bluewater.integration.response.NominatimResponse;
import ru.bluewater.itpdataprocessing.api.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataprocessing.api.exception.NominatimServiceUnavailableException;


@Service
public class NominatimService {
    @Value("${nominatim.url}")
    private String nominatimUrl;

    private final RestTemplate restTemplate;

    public NominatimService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public NominatimResponse getCoordinatesByAddress(String address) throws CoordinatesNotFoundException, NominatimServiceUnavailableException {
        try {
            String url = nominatimUrl + "/search?q=" + address + "&format=json&limit=1&addressdetails=1";

            NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

            if (response.length == 0) {
                throw new CoordinatesNotFoundException();
            }

            return response[0];
        } catch (RestClientException e) {
            throw new NominatimServiceUnavailableException();
        }
    }
}
