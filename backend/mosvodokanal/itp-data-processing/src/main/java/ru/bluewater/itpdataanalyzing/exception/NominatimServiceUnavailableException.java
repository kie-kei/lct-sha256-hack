package ru.bluewater.itpdataanalyzing.exception;

public class NominatimServiceUnavailableException extends Exception {
    public NominatimServiceUnavailableException() {
        super("Nominatim service unavailable");
    }
}
