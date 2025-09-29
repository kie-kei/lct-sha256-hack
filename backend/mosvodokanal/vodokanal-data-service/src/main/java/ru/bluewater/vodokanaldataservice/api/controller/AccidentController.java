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
import ru.bluewater.integration.type.ProbabilityType;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.AccidentUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.AccidentResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.AccidentStatisticsResponse;
import ru.bluewater.vodokanaldataservice.service.AccidentService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accidents")
@RequiredArgsConstructor
@Slf4j
public class AccidentController {
    private final AccidentService accidentService;

    @GetMapping
    public ResponseEntity<List<AccidentResponse>> getAllAccidents() {
        log.debug("GET /api/v1/accidents - Getting all accidents");
        List<AccidentResponse> accidents = accidentService.findAll();
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<AccidentResponse>> getAllAccidents(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/v1/accidents/paged - Getting all accidents");
        Page<AccidentResponse> accidents = accidentService.findAll(pageable);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccidentResponse> getAccidentById(@PathVariable UUID id) {
        log.debug("GET /api/v1/accidents/{} - Getting accident", id);
        AccidentResponse accident = accidentService.findById(id);
        return ResponseEntity.ok(accident);
    }

    @GetMapping("/itp/{itpId}")
    public ResponseEntity<List<AccidentResponse>> getAccidentsByItp(@PathVariable UUID itpId) {
        log.debug("GET /api/v1/accidents/itp/{} - Getting accidents for ITP", itpId);
        List<AccidentResponse> accidents = accidentService.findByItpId(itpId);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/itp/{itpId}/paged")
    public ResponseEntity<Page<AccidentResponse>> getAccidentsByItpPaged(
            @PathVariable UUID itpId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/v1/accidents/itp/{}/paged - Getting paged accidents for ITP", itpId);
        Page<AccidentResponse> accidents = accidentService.findByItpId(itpId, pageable);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/probability/{probabilityType}")
    public ResponseEntity<List<AccidentResponse>> getAccidentsByProbability(@PathVariable ProbabilityType probabilityType) {
        log.debug("GET /api/v1/accidents/probability/{} - Getting accidents by probability", probabilityType);
        List<AccidentResponse> accidents = accidentService.findByProbabilityType(probabilityType);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/probability/{probabilityType}/paged")
    public ResponseEntity<Page<AccidentResponse>> getAccidentsByProbabilityPaged(
            @PathVariable ProbabilityType probabilityType,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/v1/accidents/probability/{}/paged - Getting paged accidents by probability", probabilityType);
        Page<AccidentResponse> accidents = accidentService.findByProbabilityType(probabilityType, pageable);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AccidentResponse>> getAccidentsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.debug("GET /api/v1/accidents/date-range - Getting accidents for period");
        List<AccidentResponse> accidents = accidentService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/date-range/paged")
    public ResponseEntity<Page<AccidentResponse>> getAccidentsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/v1/accidents/date-range/paged - Getting accidents for period");
        Page<AccidentResponse> accidents = accidentService.findByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/itp/{itpId}/date-range")
    public ResponseEntity<List<AccidentResponse>> getAccidentsByItpAndDateRange(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        log.debug("GET /api/v1/accidents/itp/{}/date-range - Getting accidents for ITP and period", itpId);
        List<AccidentResponse> accidents = accidentService.findByItpIdAndDateRange(itpId, startDate, endDate);
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/itp/{itpId}/date-range/paged")
    public ResponseEntity<Page<AccidentResponse>> getAccidentsByItpAndDateRange(
            @PathVariable UUID itpId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("GET /api/v1/accidents/itp/{}/date-range/paged - Getting accidents for ITP and period", itpId);
        Page<AccidentResponse> accidents = accidentService.findByItpIdAndDateRange(itpId, startDate, endDate, pageable);
        return ResponseEntity.ok(accidents);
    }


    @GetMapping("/anomalies")
    public ResponseEntity<List<AccidentResponse>> getAllAccidentsWithAnomalies(
            @RequestParam(defaultValue = "false") Boolean isGvsFirstChannelFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsSecondChannelFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isHvsConsumptionFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isHvsGvsConsumptionFlowsAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsChannelsFlowsRatioAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    ) {
        log.debug("GET /api/v1/accidents/anomalies - Getting all accidents with anomalies");
        List<AccidentResponse> accidents = accidentService.findAllByAnomalies(
                isGvsFirstChannelFlowAnomaly,
                isGvsSecondChannelFlowAnomaly,
                isHvsConsumptionFlowAnomaly,
                isHvsGvsConsumptionFlowsAnomaly,
                isGvsChannelsFlowsRatioAnomaly,
                isGvsChannelsFlowsNegativeRatioAnomaly
        );
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/itp/{itpId}/anomalies")
    public ResponseEntity<List<AccidentResponse>> getAnomaliesByItp(
            @PathVariable UUID itpId,
            @RequestParam(defaultValue = "false") Boolean isGvsFirstChannelFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsSecondChannelFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isHvsConsumptionFlowAnomaly,
            @RequestParam(defaultValue = "false") Boolean isHvsGvsConsumptionFlowsAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsChannelsFlowsRatioAnomaly,
            @RequestParam(defaultValue = "false") Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    ) {
        log.debug("GET /api/v1/accidents/itp/{}/anomalies - Getting anomalies for ITP", itpId);
        List<AccidentResponse> accidents = accidentService.findAnomaliesByItpId(
                itpId,
                isGvsFirstChannelFlowAnomaly,
                isGvsSecondChannelFlowAnomaly,
                isHvsConsumptionFlowAnomaly,
                isHvsGvsConsumptionFlowsAnomaly,
                isGvsChannelsFlowsRatioAnomaly,
                isGvsChannelsFlowsNegativeRatioAnomaly
        );
        return ResponseEntity.ok(accidents);
    }

    @GetMapping("/itp/{itpId}/statistics")
    public ResponseEntity<AccidentStatisticsResponse> getStatisticsByItp(@PathVariable UUID itpId) {
        log.debug("GET /api/v1/accidents/itp/{}/statistics - Getting statistics for ITP", itpId);
        AccidentStatisticsResponse statistics = accidentService.getStatisticsByItpId(itpId);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping
    public ResponseEntity<AccidentResponse> createAccident(@Valid @RequestBody AccidentCreateRequest request) {
        log.debug("POST /api/v1/accidents - Creating accident");
        AccidentResponse created = accidentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccidentResponse> updateAccident(
            @PathVariable UUID id,
            @Valid @RequestBody AccidentUpdateRequest request) {
        log.debug("PUT /api/v1/accidents/{} - Updating accident", id);
        AccidentResponse updated = accidentService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccident(@PathVariable UUID id) {
        log.debug("DELETE /api/v1/accidents/{} - Deleting accident", id);
        accidentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}