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
        return ResponseEntity.ok(itpService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ITPDetailResponse> getITPById(@PathVariable UUID id) {
        ITPDetailResponse itp = itpService.findById(id);
        return ResponseEntity.ok(itp);
    }

    @GetMapping("/{id}/simple")
    public ResponseEntity<ITPResponse> getITPByIdSimple(@PathVariable UUID id) {
        return ResponseEntity.ok(itpService.findByIdSimple(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ITPResponse>> searchITPs(
            @RequestParam String number,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(itpService.findByNumberContaining(number, pageable));
    }

    @PostMapping
    public ResponseEntity<ITPResponse> createITP(@Valid @RequestBody ITPCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itpService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ITPResponse> updateITP(
            @PathVariable UUID id,
            @Valid @RequestBody ITPUpdateRequest request) {
        return ResponseEntity.ok(itpService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteITP(@PathVariable UUID id) {
        itpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}