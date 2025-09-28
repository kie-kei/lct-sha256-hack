package ru.bluewater.vodokanaldataservice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Component
public class ExcelUtil {
    public Boolean validateExcelFilesLineCount(MultipartFile file1, MultipartFile file2) throws IOException {
        try (Workbook wb1 = new XSSFWorkbook(file1.getInputStream());
             Workbook wb2 = new XSSFWorkbook(file2.getInputStream())) {

            Sheet sheet1 = wb1.getSheetAt(0);
            Sheet sheet2 = wb2.getSheetAt(0);

            int count1 = sheet1.getLastRowNum();
            int count2 = sheet2.getLastRowNum();

            return count1 == count2;
        }
    }

    public String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    DataFormatter formatter = new DataFormatter();
                    return formatter.formatCellValue(cell);
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public String convertToStandardFormat(String inputDate) {
        String[] possibleFormats = {
                "M/d/yy",
                "dd.MM.yyyy"
        };

        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                LocalDate date = LocalDate.parse(inputDate, formatter);

                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                return date.format(outputFormatter);

            } catch (DateTimeParseException e) {
            }
        }

        throw new IllegalArgumentException("Неизвестный формат даты: " + inputDate);
    }
}
