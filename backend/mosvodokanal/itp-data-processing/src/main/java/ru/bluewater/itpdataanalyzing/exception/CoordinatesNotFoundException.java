package ru.bluewater.itpdataanalyzing.exception;

public class CoordinatesNotFoundException extends Exception {
    public CoordinatesNotFoundException() {
        super("Coordinates not found");
    }
}
