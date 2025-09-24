package ru.bluewater.vodokanaldataservice.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.MKDResponse;
import ru.bluewater.vodokanaldataservice.service.MKDService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mkd")
@RequiredArgsConstructor
@Slf4j
public class MKDController {
    private final MKDService mkdService;

    @GetMapping
    public ResponseEntity<Page<MKDResponse>> getAllMKDs(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/mkd - Getting all MKDs");
        Page<MKDResponse> mkds = mkdService.findAll(pageable);
        return ResponseEntity.ok(mkds);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MKDResponse> getMKDById(@PathVariable UUID id) {
        log.info("GET /api/v1/mkd/{} - Getting MKD", id);
        MKDResponse mkd = mkdService.findById(id);
        return ResponseEntity.ok(mkd);
    }

    @GetMapping("/itp/{itpId}")
    public ResponseEntity<MKDResponse> getMKDByItpId(@PathVariable UUID itpId) {
        log.info("GET /api/v1/mkd/itp/{} - Getting MKD by ITP id", itpId);
        MKDResponse mkd = mkdService.findByItpId(itpId);
        return ResponseEntity.ok(mkd);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MKDResponse>> searchMKDs(
            @RequestParam String address,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/mkd/search?address={} - Searching MKDs", address);
        Page<MKDResponse> mkds = mkdService.findByAddressContaining(address, pageable);
        return ResponseEntity.ok(mkds);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<MKDResponse>> getNearbyMKDs(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "0.01") BigDecimal radius) {
        log.info("GET /api/v1/mkd/nearby - Getting MKDs near location");
        List<MKDResponse> mkds = mkdService.findByLocationNear(latitude, longitude, radius);
        return ResponseEntity.ok(mkds);
    }

    @PostMapping("/itp/{itpId}")
    public ResponseEntity<MKDResponse> createMKD(
            @PathVariable UUID itpId,
            @Valid @RequestBody MKDCreateRequest request) {
        log.info("POST /api/v1/mkd/itp/{} - Creating MKD for ITP", itpId);
        MKDResponse createdMKD = mkdService.create(itpId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMKD);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MKDResponse> updateMKD(
            @PathVariable UUID id,
            @Valid @RequestBody MKDUpdateRequest request) {
        log.info("PUT /api/v1/mkd/{} - Updating MKD", id);
        MKDResponse updatedMKD = mkdService.update(id, request);
        return ResponseEntity.ok(updatedMKD);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMKD(@PathVariable UUID id) {
        log.info("DELETE /api/v1/mkd/{} - Deleting MKD", id);
        mkdService.delete(id);
        return ResponseEntity.noContent().build();
    }
}