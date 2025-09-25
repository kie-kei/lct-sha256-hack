package ru.bluewater.vodokanaldataservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bluewater.integration.message.ITPDataMessage;
import ru.bluewater.integration.message.ODPUGVSDeviceMessage;
import ru.bluewater.integration.message.WaterMeterXVSITPMessage;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;
import ru.bluewater.vodokanaldataservice.api.exception.BusinessException;
import ru.bluewater.vodokanaldataservice.mapper.ITPMessageMapper;
import ru.bluewater.vodokanaldataservice.repository.ITPRepository;
import ru.bluewater.vodokanaldataservice.repository.MKDRepository;
import ru.bluewater.vodokanaldataservice.repository.WaterMeterDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ITPDataMessageProcessorService {

    private final ITPRepository itpRepository;
    private final MKDRepository mkdRepository;
    private final WaterMeterDataRepository waterMeterDataRepository;
    private final ITPMessageMapper itpMessageMapper;

    @Transactional
    public void processITPDataMessage(ITPDataMessage message) {
        UUID itpId = message.getItp().getId();
        log.debug("Processing enriched ITP data message for ITP ID: {}", itpId);

        try {
            // Обрабатываем или создаём ITP
            ITPEntity itpEntity = processITP(message);

            // Обрабатываем или создаём MKD
            MKDEntity mkdEntity = processMKD(message, itpEntity);

            // Обрабатываем данные счётчиков (объединяем ГВС и ХВС)
            processWaterMeterData(message, itpEntity);

            log.debug("Successfully processed ITP data message for ITP ID: {}", itpId);
        } catch (Exception e) {
            log.error("Error processing ITP data message for ITP ID: {}", itpId, e);
            throw new BusinessException("Failed to process ITP data message: " + e.getMessage());
        }
    }

    private ITPEntity processITP(ITPDataMessage message) {
        UUID itpId = message.getItp().getId();

        return itpRepository.findById(itpId)
                .map(existingITP -> {
                    log.debug("ITP already exists, updating: {}", itpId);
                    // Обновляем существующий ITP если нужно
                    if (!existingITP.getNumber().equals(message.getItp().getNumber())) {
                        existingITP.setNumber(message.getItp().getNumber());
                        return itpRepository.save(existingITP);
                    }
                    return existingITP;
                })
                .orElseGet(() -> {
                    log.debug("Creating new ITP: {}", itpId);
                    ITPEntity newITP = itpMessageMapper.toITPEntity(message.getItp());
                    return itpRepository.save(newITP);
                });
    }

    private MKDEntity processMKD(ITPDataMessage message, ITPEntity itpEntity) {
        if (message.getMkd() == null) {
            log.debug("No MKD data in message for ITP: {}", itpEntity.getId());
            return null;
        }

        return mkdRepository.findByItpId(itpEntity.getId())
                .map(existingMKD -> {
                    log.debug("MKD already exists for ITP: {}, updating", itpEntity.getId());
                    // Обновляем существующий MKD
                    itpMessageMapper.updateMKDEntity(existingMKD, message.getMkd());
                    return mkdRepository.save(existingMKD);
                })
                .orElseGet(() -> {
                    log.debug("Creating new MKD for ITP: {}", itpEntity.getId());
                    MKDEntity newMKD = itpMessageMapper.toMKDEntity(message.getMkd());
                    newMKD.setItp(itpEntity);
                    return mkdRepository.save(newMKD);
                });
    }

    private void processWaterMeterData(ITPDataMessage message, ITPEntity itpEntity) {
        List<WaterMeterDataEntity> waterMeterDataList = new ArrayList<>();

        // Объединяем данные ГВС и ХВС в одну запись
        List<ODPUGVSDeviceMessage> gvsDevices = message.getOdpuGvsDevice();
        List<WaterMeterXVSITPMessage> hvsDevices = message.getWaterMeters();

        if (gvsDevices == null || hvsDevices == null) {
            log.warn("Unprocessable water meter data: gvsDevices {}, hvsDevices {}", gvsDevices, hvsDevices);
            return;
        }

        if (gvsDevices.size() != hvsDevices.size()) {
            log.warn("Unprocessable water meter data: gvsDevices size {}, hvsDevices size {}", gvsDevices.size(), hvsDevices.size());
            return;
        }

        for (int i = 0; i < gvsDevices.size(); i++) {
            WaterMeterDataEntity waterMeterData = new WaterMeterDataEntity();
            waterMeterData.setItp(itpEntity);
            waterMeterData.setMeasurementTimestamp(message.getTimestamp());

            // ГВС данные
            ODPUGVSDeviceMessage gvsDevice = gvsDevices.get(i);
            waterMeterData.setHeatMeterIdentifier(gvsDevice.getHeatMeterIdentifier());
            waterMeterData.setFirstChannelFlowmeterIdentifier(gvsDevice.getFirstChannelFlowmeterIdentifier());
            waterMeterData.setSecondChannelFlowmeterIdentifier(gvsDevice.getSecondChannelFlowmeterIdentifier());
            waterMeterData.setGvsFlowValue(gvsDevice.getFlowValue());

            // ХВС данные
            WaterMeterXVSITPMessage hvsDevice = hvsDevices.get(i);
            waterMeterData.setWaterMeterIdentifier(hvsDevice.getIdentifier());
            waterMeterData.setHvsFlowValue(hvsDevice.getFlowValue());

            waterMeterDataList.add(waterMeterData);
        }

        if (!waterMeterDataList.isEmpty()) {
            log.debug("Saving {} water meter data records for ITP: {}",
                    waterMeterDataList.size(), itpEntity.getId());
            waterMeterDataRepository.saveAll(waterMeterDataList);
        }
    }
}