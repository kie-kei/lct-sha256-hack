package ru.bluewater.externaldataconsumer.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bluewater.externaldataconsumer.api.dto.request.ITPDataRequest;
import ru.bluewater.externaldataconsumer.api.exception.ITPDataException;
import ru.bluewater.externaldataconsumer.service.ITPDataService;

@RestController
@RequestMapping("/api/v1/itp-data")
@RequiredArgsConstructor
@Slf4j
public class ITPDataController {
    private final ITPDataService itpDataService;

    @PostMapping
    public ResponseEntity<Void> receiveITPData(@Valid @RequestBody ITPDataRequest request) throws ITPDataException {
        log.debug("Received ITP data via REST API for ITP: {}", request.getItp().getId());
        itpDataService.processITPData(request);
        return ResponseEntity.ok().build();
    }
}