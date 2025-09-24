package ru.bluewater.vodokanaldataservice.api.controller;

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
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.WaterMeterDataUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.WaterMeterDataResponse;
import ru.bluewater.vodokanaldataservice.service.WaterMeterDataService;

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
        log.info("GET /api/v1/water-meter-data - Getting all water meter data");
        Page<WaterMeterDataResponse> data = waterMeterDataService.findAll(pageable);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterMeterDataResponse> getWaterMeterDataById(@PathVariable UUID id) {
        log.info("GET /api/v1/water-meter-data/{} - Getting water meter data", id);
        WaterMeterDataResponse data = waterMeterDataService.findById(id);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/itp/{itpId}")
    public ResponseEntity<List<WaterMeterDataResponse>> getWaterMeterDataByITP(@PathVariable UUID itpId) {
        log.info("GET /api/v1/water-meter-data/itp/{} - Getting water meter data for ITP", itpId);
        List<WaterMeterDataResponse> data = waterMeterDataService.findByItpId(itpId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/itp/{itpId}/paged")
    public ResponseEntity<Page<WaterMeterDataResponse>> getWaterMeterDataByITPPaged(
            @PathVariable UUID itpId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/water-meter-data/itp/{}/paged - Getting paged water meter data for ITP", itpId);
        Page<WaterMeterDataResponse> data = waterMeterDataService.findByItpIdPaged(itpId, pageable);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/itp/{itpId}/period")
    public ResponseEntity<List<WaterMeterDataResponse>> getWaterMeterDataByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/water-meter-data/itp/{}/period - Getting water meter data for period", itpId);
        List<WaterMeterDataResponse> data = waterMeterDataService.findByItpIdAndPeriod(itpId, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/itp/{itpId}/latest")
    public ResponseEntity<List<WaterMeterDataResponse>> getLatestWaterMeterDataByITP(@PathVariable UUID itpId) {
        log.info("GET /api/v1/water-meter-data/itp/{}/latest - Getting latest water meter data for ITP", itpId);
        List<WaterMeterDataResponse> data = waterMeterDataService.findLatestByItpId(itpId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/itp/{itpId}/average/gvs")
    public ResponseEntity<Double> getAverageGvsFlowByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/water-meter-data/itp/{}/average/gvs - Getting average GVS flow", itpId);
        Double averageFlow = waterMeterDataService.getAverageGvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
        return ResponseEntity.ok(averageFlow);
    }

    @GetMapping("/itp/{itpId}/average/hvs")
    public ResponseEntity<Double> getAverageHvsFlowByITPAndPeriod(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/v1/water-meter-data/itp/{}/average/hvs - Getting average HVS flow", itpId);
        Double averageFlow = waterMeterDataService.getAverageHvsFlowByItpIdAndPeriod(itpId, startDate, endDate);
        return ResponseEntity.ok(averageFlow);
    }

    @PostMapping
    public ResponseEntity<WaterMeterDataResponse> createWaterMeterData(@Valid @RequestBody WaterMeterDataCreateRequest request) {
        log.info("POST /api/v1/water-meter-data - Creating water meter data");
        WaterMeterDataResponse created = waterMeterDataService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterMeterDataResponse> updateWaterMeterData(
            @PathVariable UUID id,
            @Valid @RequestBody WaterMeterDataUpdateRequest request) {
        log.info("PUT /api/v1/water-meter-data/{} - Updating water meter data", id);
        WaterMeterDataResponse updated = waterMeterDataService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaterMeterData(@PathVariable UUID id) {
        log.info("DELETE /api/v1/water-meter-data/{} - Deleting water meter data", id);
        waterMeterDataService.delete(id);
        return ResponseEntity.noContent().build();
    }
}