package ru.bluewater.itpdataprocessing.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NumberUtil {
    public BigDecimal convertStringToBigDecimal(String str) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(str));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(str + " is not a valid number");
        }
    }
}
