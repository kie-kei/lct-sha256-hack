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
        if (itpDataMessage.getItp() == null) {
            throw new ITPDataException("ITP information is required");
        }

        if (itpDataMessage.getItp().getId() == null) {
            throw new ITPDataException("ITP identifier is required");
        }

        if (itpDataMessage.getItp().getNumber() == null || itpDataMessage.getItp().getNumber().trim().isEmpty()) {
            throw new ITPDataException("ITP number is required");
        }
    }

    private void validateMKD(ITPDataMessage itpDataMessage) throws ITPDataException {
        if (itpDataMessage.getMkd() == null) {
            throw new ITPDataException("MKD data is required");
        }

        if (itpDataMessage.getMkd().getAddress() == null || itpDataMessage.getMkd().getAddress().trim().isEmpty()) {
            throw new ITPDataException("MKD address is required");
        }
    }
}
