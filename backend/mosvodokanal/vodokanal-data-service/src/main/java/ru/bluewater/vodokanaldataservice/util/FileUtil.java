package ru.bluewater.vodokanaldataservice.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
    public boolean validateExcelOrCsvFilesExtension(MultipartFile file1, MultipartFile file2) {
        if (file1 == null || file2 == null) {
            return false;
        }

        String file1Ext = getFileExtension(file1);
        String file2Ext = getFileExtension(file2);

        return isValidExtension(file1Ext) && isValidExtension(file2Ext);
    }

    public String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidExtension(String extension) {
        return "csv".equals(extension) || "xls".equals(extension) || "xlsx".equals(extension);
    }
}
