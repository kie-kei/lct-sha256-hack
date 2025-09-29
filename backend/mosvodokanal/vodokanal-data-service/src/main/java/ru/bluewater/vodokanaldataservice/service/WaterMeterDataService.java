package ru.bluewater.vodokanaldataservice.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.WaterMeterDataResponse;
import ru.bluewater.vodokanaldataservice.api.exception.IncorrectTimeInWaterMeterDataException;
import ru.bluewater.vodokanaldataservice.api.exception.IncorrectWaterMeterDataFileExtensionException;
import ru.bluewater.vodokanaldataservice.api.exception.WaterMeterDataValidationException;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;
import ru.bluewater.vodokanaldataservice.api.exception.ResourceNotFoundException;
import ru.bluewater.vodokanaldataservice.mapper.WaterMeterDataMapper;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;
import ru.bluewater.vodokanaldataservice.repository.WaterMeterDataRepository;
import ru.bluewater.vodokanaldataservice.util.CsvUtil;
import ru.bluewater.vodokanaldataservice.util.ExcelUtil;
import ru.bluewater.vodokanaldataservice.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WaterMeterDataService {
    private final WaterMeterDataRepository waterMeterDataRepository;
    private final ITPRepository itpRepository;
    private final WaterMeterDataMapper waterMeterDataMapper;
    private final FileUtil fileUtil;
    private final ExcelUtil excelUtil;
    private final CsvUtil csvUtil;

    public Page<WaterMeterDataResponse> findAll(Pageable pageable) {
        log.debug("Getting all water meter data with pagination: {}", pageable);
        return waterMeterDataRepository.findAll(pageable)
                .map(waterMeterDataMapper::toResponse);
    }

    public WaterMeterDataResponse findById(UUID id) {
        log.debug("Getting water meter data by id: {}", id);
        WaterMeterDataEntity entity = waterMeterDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Water meter data not found with id: " + id));
        return waterMeterDataMapper.toResponse(entity);
    }

    public List<WaterMeterDataResponse> findByItpId(UUID itpId) {
        log.debug("Getting water meter data by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public Page<WaterMeterDataResponse> findByItpIdPaged(UUID itpId, Pageable pageable) {
        log.debug("Getting paged water meter data by ITP id: {} with pagination: {}", itpId, pageable);
        validateITPExists(itpId);
        return waterMeterDataRepository.findByItpIdOrderByMeasurementTimestampDesc(itpId, pageable)
                .map(waterMeterDataMapper::toResponse);
    }

    public List<WaterMeterDataResponse> findByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting water meter data by ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository
                .findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(itpId, startDate, endDate);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public List<WaterMeterDataResponse> findLatestByItpId(UUID itpId) {
        log.debug("Getting latest water meter data by ITP id: {}", itpId);
        validateITPExists(itpId);
        List<WaterMeterDataEntity> entities = waterMeterDataRepository.findLatestByItpId(itpId);
        return waterMeterDataMapper.toResponseList(entities);
    }

    public Double getAverageGvsFlowByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting average GVS flow for ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        return waterMeterDataRepository.getAverageGvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
    }

    public Double getAverageHvsFlowByItpIdAndPeriod(UUID itpId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting average HVS flow for ITP id: {} for period: {} to {}", itpId, startDate, endDate);
        validateITPExists(itpId);
        return waterMeterDataRepository.getAverageHvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
    }

    @Transactional
    public WaterMeterDataResponse create(WaterMeterDataCreateRequest request) {
        log.debug("Creating water meter data for ITP id: {}", request.getItpId());

        ITPEntity itp = itpRepository.findById(request.getItpId())
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + request.getItpId()));

        WaterMeterDataEntity entity = waterMeterDataMapper.toEntity(request);
        entity.setItp(itp);

        entity = waterMeterDataRepository.save(entity);
        log.debug("Created water meter data with id: {}", entity.getId());

        return waterMeterDataMapper.toResponse(entity);
    }

    @Transactional
    public WaterMeterDataResponse update(UUID id, WaterMeterDataUpdateRequest request) {
        log.debug("Updating water meter data with id: {}", id);

        WaterMeterDataEntity entity = waterMeterDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Water meter data not found with id: " + id));

        waterMeterDataMapper.updateEntity(entity, request);
        entity = waterMeterDataRepository.save(entity);

        log.debug("Updated water meter data with id: {}", id);
        return waterMeterDataMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting water meter data with id: {}", id);

        if (!waterMeterDataRepository.existsById(id)) {
            throw new ResourceNotFoundException("Water meter data not found with id: " + id);
        }

        waterMeterDataRepository.deleteById(id);
        log.debug("Deleted water meter data with id: {}", id);
    }

    @Transactional
    public List<WaterMeterDataResponse> uploadWaterMeterData(UUID itpId, MultipartFile gvsData, MultipartFile hvsData)
            throws IncorrectWaterMeterDataFileExtensionException, IOException, CsvValidationException,
            IncorrectTimeInWaterMeterDataException, ParseException, WaterMeterDataValidationException {
        if (!fileUtil.validateExcelOrCsvFilesExtension(gvsData, hvsData)) {
            throw new IncorrectWaterMeterDataFileExtensionException("Extension of water meter data files must be .csv, .xls or .xlsx");
        }

        ITPEntity itpEntity = itpRepository.findById(itpId)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + itpId));

        List<WaterMeterDataEntity> waterMeterDataList;

        if ("csv".equals(fileUtil.getFileExtension(gvsData))) {
            waterMeterDataList = parseWaterMeterDataFromCsv(itpEntity, gvsData, hvsData);
        } else if (Arrays.asList("xls", "xlsx").contains(fileUtil.getFileExtension(gvsData))) {
            waterMeterDataList = parseWaterMeterDataFromExcel(itpEntity, gvsData, hvsData);
        } else {
            throw new IncorrectWaterMeterDataFileExtensionException("Extension of water meter data files must be .csv, .xls or .xlsx");
        }

        return waterMeterDataMapper.toResponseList(waterMeterDataRepository.saveAll(waterMeterDataList));
    }

    private void validateITPExists(UUID itpId) {
        if (!itpRepository.existsById(itpId)) {
            throw new ResourceNotFoundException("ITP not found with id: " + itpId);
        }
    }

    private List<WaterMeterDataEntity> parseWaterMeterDataFromCsv(ITPEntity itpEntity, MultipartFile gvsData, MultipartFile hvsData)
            throws IOException, CsvValidationException, ParseException, IncorrectTimeInWaterMeterDataException {
        if (!csvUtil.validateCsvFilesLineCount(gvsData, hvsData)) {
            throw new CsvValidationException("CSV files must contain the same number of records");
        }

        List<WaterMeterDataEntity> waterMeterDataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        try (CSVReader gvsDataCSVReader = new CSVReader(new InputStreamReader(gvsData.getInputStream(), StandardCharsets.UTF_8));
             CSVReader hvsDataCSVReader = new CSVReader(new InputStreamReader(hvsData.getInputStream(), StandardCharsets.UTF_8))) {

            String[] gvsDataLine, hvsDataLine;

            // Пропуск шапки
            gvsDataCSVReader.readNext();
            hvsDataCSVReader.readNext();

            while ((gvsDataLine = gvsDataCSVReader.readNext()) != null && (hvsDataLine = hvsDataCSVReader.readNext()) != null) {
                String gvsDateStr = gvsDataLine[0];
                String gvsTimeStr = gvsDataLine[1].split("-")[0] + ":00";

                String hvsDateStr = hvsDataLine[0];
                String hvsTimeStr = hvsDataLine[1].split("-")[0] + ":00";

                Date gvsDataLineMeasurementTimestamp = dateFormat.parse(gvsDateStr + " " + gvsTimeStr);
                Date hvsDataLineMeasurementTimestamp = dateFormat.parse(hvsDateStr + " " + hvsTimeStr);

                if (!gvsDataLineMeasurementTimestamp.equals(hvsDataLineMeasurementTimestamp)) {
                    throw new IncorrectTimeInWaterMeterDataException("The dates in the two files must converge");
                }

                WaterMeterDataEntity waterMeterData = new WaterMeterDataEntity();

                waterMeterData.setItp(itpEntity);
                waterMeterData.setGvsFirstChannelFlowValue(Float.parseFloat(gvsDataLine[2]));
                waterMeterData.setGvsSecondChannelFlowValue(Float.parseFloat(gvsDataLine[3]));
                waterMeterData.setGvsConsumptionFlowValue(Float.parseFloat(gvsDataLine[4]));
                waterMeterData.setHvsFlowValue(Float.parseFloat(hvsDataLine[2]));
                waterMeterData.setMeasurementTimestamp(gvsDataLineMeasurementTimestamp);

                waterMeterDataList.add(waterMeterData);
            }
        }

        return waterMeterDataList;
    }

    private List<WaterMeterDataEntity> parseWaterMeterDataFromExcel(ITPEntity itpEntity, MultipartFile gvsData, MultipartFile hvsData)
            throws IOException, WaterMeterDataValidationException, IncorrectTimeInWaterMeterDataException, ParseException {
        if (!excelUtil.validateExcelFilesLineCount(gvsData, hvsData)) {
            throw new WaterMeterDataValidationException("Excel files must contain the same number of records");
        }

        List<WaterMeterDataEntity> waterMeterDataList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        try (Workbook gvsWorkbook = new XSSFWorkbook(gvsData.getInputStream());
             Workbook hvsWorkbook = new XSSFWorkbook(hvsData.getInputStream())) {

            Sheet gvsSheet = gvsWorkbook.getSheetAt(0);
            Sheet hvsSheet = hvsWorkbook.getSheetAt(0);

            int gvsRowNum = 1;
            int hvsRowNum = 1;

            Row gvsRow = gvsSheet.getRow(gvsRowNum);
            Row hvsRow = hvsSheet.getRow(hvsRowNum);

            while (gvsRow != null && hvsRow != null) {
                String gvsDateStr = excelUtil.convertToStandardFormat(excelUtil.getCellValueAsString(gvsRow.getCell(0)));
                String gvsTimeStr = excelUtil.getCellValueAsString(gvsRow.getCell(1)).split("-")[0] + ":00";

                String hvsDateStr = excelUtil.convertToStandardFormat(excelUtil.getCellValueAsString(hvsRow.getCell(0)));
                String hvsTimeStr = excelUtil.getCellValueAsString(hvsRow.getCell(1)).split("-")[0] + ":00";

                Date gvsDataLineMeasurementTimestamp = dateFormat.parse(gvsDateStr + " " + gvsTimeStr);
                Date hvsDataLineMeasurementTimestamp = dateFormat.parse(hvsDateStr + " " + hvsTimeStr);

                if (!gvsDataLineMeasurementTimestamp.equals(hvsDataLineMeasurementTimestamp)) {
                    throw new IncorrectTimeInWaterMeterDataException("The dates in the two files must converge");
                }

                WaterMeterDataEntity waterMeterData = new WaterMeterDataEntity();

                waterMeterData.setItp(itpEntity);
                waterMeterData.setGvsFirstChannelFlowValue(Float.parseFloat(excelUtil.getCellValueAsString(gvsRow.getCell(2)).replace(",", ".")));
                waterMeterData.setGvsSecondChannelFlowValue(Float.parseFloat(excelUtil.getCellValueAsString(gvsRow.getCell(3)).replace(",", ".")));
                waterMeterData.setGvsConsumptionFlowValue(Float.parseFloat(excelUtil.getCellValueAsString(gvsRow.getCell(4)).replace(",", ".")));
                waterMeterData.setHvsFlowValue(Float.parseFloat(excelUtil.getCellValueAsString(hvsRow.getCell(3)).replace(",", ".")));
                waterMeterData.setMeasurementTimestamp(gvsDataLineMeasurementTimestamp);

                waterMeterDataList.add(waterMeterData);

                gvsRowNum++;
                hvsRowNum++;
                gvsRow = gvsSheet.getRow(gvsRowNum);
                hvsRow = hvsSheet.getRow(hvsRowNum);
            }

            if (gvsRow != null || hvsRow != null) {
                throw new  WaterMeterDataValidationException("Excel files must contain the same number of records");
            }
        }

        return waterMeterDataList;
    }
}