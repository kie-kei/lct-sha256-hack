package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.bluewater.vodokanaldataservice.api.dto.response.DistrictResponse;
import ru.bluewater.vodokanaldataservice.api.exception.BusinessException;
import ru.bluewater.vodokanaldataservice.entity.DistrictEntity;
import ru.bluewater.vodokanaldataservice.mapper.DistrictMapper;
import ru.bluewater.vodokanaldataservice.repository.DistrictRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistrictService {
    private final DistrictRepository districtRepository;
    private final DistrictMapper districtMapper;

    public DistrictEntity getOrCreateDistrict(String districtName) {
        if (districtName == null || districtName.isBlank()) {
            log.warn("District name is null or empty");
            throw new BusinessException("District name cannot be null or empty");
        }
        return districtRepository.findByName(districtName)
                .orElseGet(() -> {
                    DistrictEntity newDistrict = DistrictEntity.builder()
                            .name(districtName)
                            .build();
                    return districtRepository.save(newDistrict);
                });
    }

    public List<DistrictResponse> getAllDistricts() {
        List<DistrictEntity> districtEntities = districtRepository.findAll();

        return districtMapper.toResponseList(districtEntities);
    }
}
