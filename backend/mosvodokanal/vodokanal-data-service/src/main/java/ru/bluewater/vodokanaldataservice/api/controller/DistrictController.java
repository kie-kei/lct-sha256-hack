package ru.bluewater.vodokanaldataservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bluewater.vodokanaldataservice.api.dto.response.DistrictResponse;
import ru.bluewater.vodokanaldataservice.service.DistrictService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/district")
@RequiredArgsConstructor
@Slf4j
public class DistrictController {
    private final DistrictService districtService;

    @GetMapping
    public ResponseEntity<List<DistrictResponse>> getAllDistricts() {
        return ResponseEntity.ok(districtService.getAllDistricts());
    }
}
