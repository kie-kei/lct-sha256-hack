package ru.bluewater.itpdataprocessing.exception;

public class NominatimServiceUnavailableException extends Exception {
    public NominatimServiceUnavailableException() {
        super("Nominatim service unavailable");
    }
}
