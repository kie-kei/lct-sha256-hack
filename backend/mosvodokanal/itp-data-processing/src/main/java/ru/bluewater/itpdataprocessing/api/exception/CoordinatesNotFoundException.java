package ru.bluewater.itpdataprocessing.api.exception;

public class CoordinatesNotFoundException extends Exception {
    public CoordinatesNotFoundException() {
        super("Coordinates not found");
    }
}
