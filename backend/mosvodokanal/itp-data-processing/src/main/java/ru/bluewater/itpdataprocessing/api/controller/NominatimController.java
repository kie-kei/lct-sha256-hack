package ru.bluewater.itpdataprocessing.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bluewater.itpdataprocessing.api.dto.response.NominatimResponse;
import ru.bluewater.itpdataprocessing.api.exception.CoordinatesNotFoundException;
import ru.bluewater.itpdataprocessing.api.exception.NominatimServiceUnavailableException;
import ru.bluewater.itpdataprocessing.service.NominatimService;

@RestController
@RequestMapping("/api/v1/nominatim")
public class NominatimController {
    private final NominatimService nominatimService;

    public NominatimController(NominatimService nominatimService) {
        this.nominatimService = nominatimService;
    }

    @GetMapping("/coordinates")
    public ResponseEntity<NominatimResponse> getCoordinatesByAddress(@RequestParam("address") String address)
            throws CoordinatesNotFoundException, NominatimServiceUnavailableException {
        return ResponseEntity.ok(nominatimService.getCoordinatesByAddress(address));
    }
}
