package ru.bluewater.vodokanaldataservice.api.controller;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.WaterMeterDataResponse;
import ru.bluewater.vodokanaldataservice.api.exception.IncorrectTimeInWaterMeterDataException;
import ru.bluewater.vodokanaldataservice.api.exception.IncorrectWaterMeterDataFileExtensionException;
import ru.bluewater.vodokanaldataservice.api.exception.WaterMeterDataValidationException;
import ru.bluewater.vodokanaldataservice.service.WaterMeterDataService;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/water-meter-data")
@RequiredArgsConstructor
@Slf4j
public class WaterMeterDataController {
    private final WaterMeterDataService waterMeterDataService;

    @GetMapping
    public ResponseEntity<Page<WaterMeterDataResponse>> getAllWaterMeterData(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(waterMeterDataService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterMeterDataResponse> getWaterMeterDataById(@PathVariable UUID id) {
        return ResponseEntity.ok(waterMeterDataService.findById(id));
    }

    @GetMapping("/itp/{itpId}")
    public ResponseEntity<List<WaterMeterDataResponse>> getWaterMeterDataByITP(@PathVariable UUID itpId) {
        return ResponseEntity.ok(waterMeterDataService.findByItpId(itpId));
    }

    @GetMapping("/itp/{itpId}/paged")
    public ResponseEntity<Page<WaterMeterDataResponse>> getWaterMeterDataByITPPaged(
            @PathVariable UUID itpId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(waterMeterDataService.findByItpIdPaged(itpId, pageable));
    }

    @GetMapping("/itp/{itpId}/period")
    public ResponseEntity<List<WaterMeterDataResponse>> getWaterMeterDataByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(waterMeterDataService.findByItpIdAndPeriod(itpId, startDate, endDate));
    }

    @GetMapping("/itp/{itpId}/latest")
    public ResponseEntity<List<WaterMeterDataResponse>> getLatestWaterMeterDataByITP(@PathVariable UUID itpId) {
        return ResponseEntity.ok(waterMeterDataService.findLatestByItpId(itpId));
    }

    @GetMapping("/itp/{itpId}/average/gvs")
    public ResponseEntity<Double> getAverageGvsFlowByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(waterMeterDataService.getAverageGvsFlowByItpIdAndPeriod(itpId, startDate, endDate));
    }

    @GetMapping("/itp/{itpId}/average/hvs")
    public ResponseEntity<Double> getAverageHvsFlowByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(waterMeterDataService.getAverageHvsFlowByItpIdAndPeriod(itpId, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<WaterMeterDataResponse> createWaterMeterData(@Valid @RequestBody WaterMeterDataCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waterMeterDataService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterMeterDataResponse> updateWaterMeterData(
            @PathVariable UUID id,
            @Valid @RequestBody WaterMeterDataUpdateRequest request) {
        return ResponseEntity.ok(waterMeterDataService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterMeterData(@PathVariable UUID id) {
        waterMeterDataService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload/itp/{itpId}")
    public ResponseEntity<List<WaterMeterDataResponse>> uploadWaterMeterData(@PathVariable UUID itpId,
                                                                       @RequestParam("gvsData") MultipartFile gvsData,
                                                                       @RequestParam("hvsData") MultipartFile hvsData)
            throws CsvValidationException, IncorrectTimeInWaterMeterDataException,
            IncorrectWaterMeterDataFileExtensionException, IOException, ParseException, WaterMeterDataValidationException {
        return ResponseEntity.ok(waterMeterDataService.uploadWaterMeterData(itpId, gvsData, hvsData));
    }
}