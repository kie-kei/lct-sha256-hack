package ru.bluewater.itpdataprocessing.api.exception;

public class NominatimServiceUnavailableException extends Exception {
    public NominatimServiceUnavailableException() {
        super("Nominatim service unavailable");
    }
}
