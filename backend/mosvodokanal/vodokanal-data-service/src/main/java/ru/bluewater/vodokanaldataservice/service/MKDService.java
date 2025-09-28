package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bluewater.integration.response.NominatimResponse;
import ru.bluewater.vodokanaldataservice.client.ItpDataProcessingClient;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.MKDUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.MKDResponse;
import ru.bluewater.vodokanaldataservice.api.exception.CoordinatesNotFoundException;
import ru.bluewater.vodokanaldataservice.entity.DistrictEntity;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;
import ru.bluewater.vodokanaldataservice.api.exception.BusinessException;
import ru.bluewater.vodokanaldataservice.api.exception.ResourceNotFoundException;
import ru.bluewater.vodokanaldataservice.mapper.MKDMapper;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;
import ru.bluewater.vodokanaldataservice.repository.MKDRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MKDService {
    private final MKDRepository mkdRepository;
    private final ITPRepository itpRepository;
    private final MKDMapper mkdMapper;
    private final ItpDataProcessingClient itpDataProcessingClient;
    private final DistrictService districtService;

    public List<MKDResponse> findAll() {
        log.debug("Getting all MKDs without pagination");
        return mkdRepository.findAll().stream()
                .map(mkdMapper::toResponse)
                .toList();
    }

    public Page<MKDResponse> findAll(Pageable pageable) {
        log.debug("Getting all MKDs with pagination: {}", pageable);
        return mkdRepository.findAll(pageable)
                .map(mkdMapper::toResponse);
    }

    public MKDResponse findById(UUID id) {
        log.debug("Getting MKD by id: {}", id);
        MKDEntity entity = mkdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MKD not found with id: " + id));
        return mkdMapper.toResponse(entity);
    }

    public MKDResponse findByItpId(UUID itpId) {
        log.debug("Getting MKD by ITP id: {}", itpId);
        MKDEntity entity = mkdRepository.findByItpId(itpId)
                .orElseThrow(() -> new ResourceNotFoundException("MKD not found for ITP id: " + itpId));
        return mkdMapper.toResponse(entity);
    }

    public Page<MKDResponse> findByAddressContaining(String address, Pageable pageable) {
        log.debug("Searching MKDs by address containing: {}", address);
        return mkdRepository.findByAddressContainingIgnoreCase(address, pageable)
                .map(mkdMapper::toResponse);
    }

    public List<MKDResponse> findByLocationNear(BigDecimal latitude, BigDecimal longitude, BigDecimal radius) {
        log.debug("Finding MKDs near location: {}, {} with radius: {}", latitude, longitude, radius);
        List<MKDEntity> entities = mkdRepository.findByLocationNear(latitude, longitude, radius);
        return mkdMapper.toResponseList(entities);
    }

    @Transactional
    public MKDResponse create(UUID itpId, MKDCreateRequest request) throws CoordinatesNotFoundException {
        log.debug("Creating MKD for ITP id: {}", itpId);

        ITPEntity itp = itpRepository.findById(itpId)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + itpId));

        if (mkdRepository.existsByFias(request.getFias())) {
            throw new BusinessException("MKD already exists with FIAS: " + request.getFias());
        }

        if (mkdRepository.existsByUnom(request.getUnom())) {
            throw new BusinessException("MKD already exists with UNOM: " + request.getUnom());
        }

        MKDEntity entity = mkdMapper.toEntity(request);

        NominatimResponse response = itpDataProcessingClient.getCoordinatesByAddress(request.getAddress());

        entity.setLongitude(BigDecimal.valueOf(Double.parseDouble(response.getLon())));
        entity.setLatitude(BigDecimal.valueOf(Double.parseDouble(response.getLat())));

        entity.setItp(itp);

        DistrictEntity district = districtService.getOrCreateDistrict(response.getAddress().getSuburb());
        entity.setDistrict(district);

        entity = mkdRepository.save(entity);
        log.debug("Created MKD with id: {}", entity.getId());

        return mkdMapper.toResponse(entity);
    }

    @Transactional
    public MKDResponse update(UUID id, MKDUpdateRequest request) throws CoordinatesNotFoundException {
        log.debug("Updating MKD with id: {}", id);

        MKDEntity entity = mkdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MKD not found with id: " + id));

        // Проверка на дубликат FIAS
        if (request.getFias() != null &&
                !entity.getFias().equals(request.getFias()) &&
                mkdRepository.existsByFias(request.getFias())) {
            throw new BusinessException("MKD already exists with FIAS: " + request.getFias());
        }

        // Проверка на дубликат UNOM
        if (request.getUnom() != null &&
                !entity.getUnom().equals(request.getUnom()) &&
                mkdRepository.existsByUnom(request.getUnom())) {
            throw new BusinessException("MKD already exists with UNOM: " + request.getUnom());
        }

        String oldAddress = entity.getAddress();

        mkdMapper.updateEntity(entity, request);

        if (!oldAddress.equals(entity.getAddress())) {
            NominatimResponse response = itpDataProcessingClient.getCoordinatesByAddress(entity.getAddress());

            entity.setLongitude(BigDecimal.valueOf(Double.parseDouble(response.getLon())));
            entity.setLatitude(BigDecimal.valueOf(Double.parseDouble(response.getLat())));
            DistrictEntity district = districtService.getOrCreateDistrict(response.getAddress().getSuburb());
            entity.setDistrict(district);
        }
        entity = mkdRepository.save(entity);

        log.debug("Updated MKD with id: {}", id);
        return mkdMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting MKD with id: {}", id);

        if (!mkdRepository.existsById(id)) {
            throw new ResourceNotFoundException("MKD not found with id: " + id);
        }

        mkdRepository.deleteById(id);
        log.debug("Deleted MKD with id: {}", id);
    }
}