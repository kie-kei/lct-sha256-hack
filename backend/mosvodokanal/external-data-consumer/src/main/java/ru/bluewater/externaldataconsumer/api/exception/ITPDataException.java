package ru.bluewater.externaldataconsumer.api.exception;

public class ITPDataException extends Exception {
    public ITPDataException(String message) {
        super(message);
    }

    public ITPDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
