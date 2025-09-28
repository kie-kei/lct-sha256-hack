package ru.bluewater.vodokanaldataservice.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class CsvUtil {
    public Boolean validateCsvFilesLineCount(MultipartFile file1, MultipartFile file2) throws IOException, CsvValidationException {
        if (file1 == null || file2 == null || file1.isEmpty() || file2.isEmpty()) {
            throw new IllegalArgumentException("One or both .csv files are null or empty");
        }

        long file1RowCount = 0;
        long file2RowCount = 0;

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file1.getInputStream()))) {
            while (csvReader.readNext() != null) {
                file1RowCount++;
            }
        }

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file2.getInputStream()))) {
            while (csvReader.readNext() != null) {
                file2RowCount++;
            }
        }

        return file1RowCount == file2RowCount;
    }
}
