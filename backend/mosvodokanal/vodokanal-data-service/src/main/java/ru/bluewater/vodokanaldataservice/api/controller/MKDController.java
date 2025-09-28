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
import ru.bluewater.vodokanaldataservice.api.exception.CoordinatesNotFoundException;
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
        return ResponseEntity.ok(mkdService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MKDResponse> getMKDById(@PathVariable UUID id) {
        return ResponseEntity.ok(mkdService.findById(id));
    }

    @GetMapping("/itp/{itpId}")
    public ResponseEntity<MKDResponse> getMKDByItpId(@PathVariable UUID itpId) {
        return ResponseEntity.ok(mkdService.findByItpId(itpId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MKDResponse>> searchMKDs(
            @RequestParam String address,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/mkd/search?address={} - Searching MKDs", address);
        return ResponseEntity.ok(mkdService.findByAddressContaining(address, pageable));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<MKDResponse>> getNearbyMKDs(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(defaultValue = "0.01") BigDecimal radius) {
        return ResponseEntity.ok(mkdService.findByLocationNear(latitude, longitude, radius));
    }

    @PostMapping("/itp/{itpId}")
    public ResponseEntity<MKDResponse> createMKD(
            @PathVariable UUID itpId,
            @Valid @RequestBody MKDCreateRequest request) throws CoordinatesNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(mkdService.create(itpId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MKDResponse> updateMKD(
            @PathVariable UUID id,
            @Valid @RequestBody MKDUpdateRequest request) throws CoordinatesNotFoundException {
        return ResponseEntity.ok(mkdService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMKD(@PathVariable UUID id) {
        mkdService.delete(id);
        return ResponseEntity.noContent().build();
    }
}