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
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPDetailResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPResponse;
import ru.bluewater.vodokanaldataservice.service.ITPService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/itp")
@RequiredArgsConstructor
@Slf4j
public class ITPController {
    private final ITPService itpService;

    @GetMapping
    public ResponseEntity<Page<ITPResponse>> getAllITPs(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/itp - Getting all ITPs");
        Page<ITPResponse> itps = itpService.findAll(pageable);
        return ResponseEntity.ok(itps);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ITPDetailResponse> getITPById(@PathVariable UUID id) {
        log.info("GET /api/v1/itp/{} - Getting ITP with details", id);
        ITPDetailResponse itp = itpService.findById(id);
        return ResponseEntity.ok(itp);
    }

    @GetMapping("/{id}/simple")
    public ResponseEntity<ITPResponse> getITPByIdSimple(@PathVariable UUID id) {
        log.info("GET /api/v1/itp/{}/simple - Getting ITP simple", id);
        ITPResponse itp = itpService.findByIdSimple(id);
        return ResponseEntity.ok(itp);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ITPResponse>> searchITPs(
            @RequestParam String number,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/itp/search?number={} - Searching ITPs", number);
        Page<ITPResponse> itps = itpService.findByNumberContaining(number, pageable);
        return ResponseEntity.ok(itps);
    }

    @PostMapping
    public ResponseEntity<ITPResponse> createITP(@Valid @RequestBody ITPCreateRequest request) {
        log.info("POST /api/v1/itp - Creating ITP");
        ITPResponse createdITP = itpService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdITP);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ITPResponse> updateITP(
            @PathVariable UUID id,
            @Valid @RequestBody ITPUpdateRequest request) {
        log.info("PUT /api/v1/itp/{} - Updating ITP", id);
        ITPResponse updatedITP = itpService.update(id, request);
        return ResponseEntity.ok(updatedITP);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteITP(@PathVariable UUID id) {
        log.info("DELETE /api/v1/itp/{} - Deleting ITP", id);
        itpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}