package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPCreateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.request.ITPUpdateRequest;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPDetailResponse;
import ru.bluewater.vodokanaldataservice.api.dto.response.ITPResponse;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.api.exception.BusinessException;
import ru.bluewater.vodokanaldataservice.api.exception.ResourceNotFoundException;
import ru.bluewater.vodokanaldataservice.mapper.ITPMapper;
import ru.bluewater.vodokanaldataservice.mapper.MKDMapper;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ITPService {
    private final ITPRepository itpRepository;
    private final ITPMapper itpMapper;
    private final MKDMapper mkdMapper;

    public Page<ITPResponse> findAll(Pageable pageable) {
        log.debug("Getting all ITPs with pagination: {}", pageable);
        return itpRepository.findAll(pageable)
                .map(itpMapper::toResponse);
    }

    public ITPDetailResponse findById(UUID id) {
        log.debug("Getting ITP by id: {}", id);
        ITPEntity entity = itpRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + id));
        return itpMapper.toDetailResponse(entity);
    }

    public ITPResponse findByIdSimple(UUID id) {
        log.debug("Getting ITP simple by id: {}", id);
        ITPEntity entity = itpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + id));
        return itpMapper.toResponse(entity);
    }

    public Page<ITPResponse> findByNumberContaining(String number, Pageable pageable) {
        log.debug("Searching ITPs by number containing: {}", number);
        return itpRepository.findByNumberContainingIgnoreCase(number, pageable)
                .map(itpMapper::toResponse);
    }

    @Transactional
    public ITPResponse create(ITPCreateRequest request) {
        log.debug("Creating ITP with id: {}", request.getId());

        if (itpRepository.existsById(request.getId())) {
            throw new BusinessException("ITP already exists with id: " + request.getId());
        }

        if (itpRepository.existsByNumber(request.getNumber())) {
            throw new BusinessException("ITP already exists with number: " + request.getNumber());
        }

        ITPEntity entity = itpMapper.toEntity(request);

        // Если есть данные МКД, создаем их
        if (request.getMkd() != null) {
            entity.setMkd(mkdMapper.toEntity(request.getMkd()));
            entity.getMkd().setItp(entity);
        }

        entity = itpRepository.save(entity);
        log.debug("Created ITP with id: {}", entity.getId());

        return itpMapper.toResponse(entity);
    }

    @Transactional
    public ITPResponse update(UUID id, ITPUpdateRequest request) {
        log.debug("Updating ITP with id: {}", id);

        ITPEntity entity = itpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ITP not found with id: " + id));

        // Проверка на дубликат номера
        if (request.getNumber() != null &&
                !entity.getNumber().equals(request.getNumber()) &&
                itpRepository.existsByNumber(request.getNumber())) {
            throw new BusinessException("ITP already exists with number: " + request.getNumber());
        }

        itpMapper.updateEntity(entity, request);
        entity = itpRepository.save(entity);

        log.debug("Updated ITP with id: {}", id);
        return itpMapper.toResponse(entity);
    }

    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting ITP with id: {}", id);

        if (!itpRepository.existsById(id)) {
            throw new ResourceNotFoundException("ITP not found with id: " + id);
        }

        itpRepository.deleteById(id);
        log.debug("Deleted ITP with id: {}", id);
    }
}