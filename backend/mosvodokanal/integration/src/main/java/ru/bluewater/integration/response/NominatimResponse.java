package ru.bluewater.integration.response;

import lombok.Getter;

@Getter
public class NominatimResponse {
    private String lat;
    private String lon;
    private NominatimAddressResponse address;
}
