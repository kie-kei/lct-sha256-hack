package ru.bluewater.externaldataconsumer.validator;

import org.springframework.stereotype.Component;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;
import ru.bluewater.externaldataconsumer.model.ITPData;

@Component
public class ITPDataValidator {
    public void validate(ITPData itpData) throws ITPDataException {
        if (itpData == null) {
            throw new ITPDataException("ITP data cannot be null");
        }
        validateITP(itpData);
        validateMKD(itpData);
    }

    private void validateITP(ITPData itpData) throws ITPDataException {
        if (itpData.getItp() == null) {
            throw new ITPDataException("ITP information is required");
        }

        if (itpData.getItp().getId() == null) {
            throw new ITPDataException("ITP identifier is required");
        }

        if (itpData.getItp().getNumber() == null || itpData.getItp().getNumber().trim().isEmpty()) {
            throw new ITPDataException("ITP number is required");
        }
    }

    private void validateMKD(ITPData itpData) throws ITPDataException {
        if (itpData.getMkd() == null) {
            throw new ITPDataException("MKD data is required");
        }

        if (itpData.getMkd().getAddress() == null || itpData.getMkd().getAddress().trim().isEmpty()) {
            throw new ITPDataException("MKD address is required");
        }
    }
}
