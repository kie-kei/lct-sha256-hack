package ru.bluewater.vodokanaldataservice.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bluewater.integration.type.TimeStepType;
import ru.bluewater.vodokanaldataservice.api.dto.response.StatisticResponse;
import ru.bluewater.vodokanaldataservice.service.StatisticService;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/all")
    public ResponseEntity<StatisticResponse> getOverallStatistics(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startDate,
                                                                 @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endDate,
                                                                 @RequestParam("timeStep") TimeStepType timeStep) {
        return ResponseEntity.ok(statisticService.getOverallStatistics(startDate, endDate, timeStep));
    }

    @GetMapping("/by-district")
    public ResponseEntity<StatisticResponse> getStatisticsByDistrict(@RequestParam("districtName") String districtName,
                                                                     @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startDate,
                                                                     @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endDate,
                                                                     @RequestParam("timeStep") TimeStepType timeStep) {
        return ResponseEntity.ok(statisticService.getStatisticsByDistrict(districtName, startDate, endDate, timeStep));
    }

    @GetMapping("/by-itp")
    public ResponseEntity<StatisticResponse> getStatisticsByITP(@RequestParam("itpId") UUID itpId,
                                                                @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startDate,
                                                                @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endDate,
                                                                @RequestParam("timeStep") TimeStepType timeStep) {
        return ResponseEntity.ok(statisticService.getStatisticsByITP(itpId, startDate, endDate, timeStep));
    }
}
