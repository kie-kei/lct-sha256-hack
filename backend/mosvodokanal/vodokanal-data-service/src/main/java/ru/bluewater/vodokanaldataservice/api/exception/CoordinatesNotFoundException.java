package ru.bluewater.vodokanaldataservice.api.exception;

public class CoordinatesNotFoundException extends Exception {
    public CoordinatesNotFoundException() {
        super("Coordinates not found");
    }
}
