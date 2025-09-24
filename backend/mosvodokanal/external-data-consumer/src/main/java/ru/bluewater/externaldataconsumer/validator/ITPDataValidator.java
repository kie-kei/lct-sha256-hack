package ru.bluewater.externaldataconsumer.validator;

import org.springframework.stereotype.Component;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;
import ru.bluewater.integration.message.ITPDataMessage;

@Component
public class ITPDataValidator {
    public void validate(ITPDataMessage itpDataMessage) throws ITPDataException {
        if (itpDataMessage == null) {
            throw new ITPDataException("ITP data cannot be null");
        }
        validateITP(itpDataMessage);
        validateMKD(itpDataMessage);
    }

    private void validateITP(ITPDataMessage itpDataMessage) throws ITPDataException {
        if (itpDataMessage.getItpMessage() == null) {
            throw new ITPDataException("ITP information is required");
        }

        if (itpDataMessage.getItpMessage().getId() == null) {
            throw new ITPDataException("ITP identifier is required");
        }

        if (itpDataMessage.getItpMessage().getNumber() == null || itpDataMessage.getItpMessage().getNumber().trim().isEmpty()) {
            throw new ITPDataException("ITP number is required");
        }
    }

    private void validateMKD(ITPDataMessage itpDataMessage) throws ITPDataException {
        if (itpDataMessage.getMkdMessage() == null) {
            throw new ITPDataException("MKD data is required");
        }

        if (itpDataMessage.getMkdMessage().getAddress() == null || itpDataMessage.getMkdMessage().getAddress().trim().isEmpty()) {
            throw new ITPDataException("MKD address is required");
        }
    }
}
