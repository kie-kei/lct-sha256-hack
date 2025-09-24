package ru.bluewater.itpdataprocessing.exception;

public class CoordinatesNotFoundException extends Exception {
    public CoordinatesNotFoundException() {
        super("Coordinates not found");
    }
}
