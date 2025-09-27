package ru.bluewater.vodokanaldataservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bluewater.vodokanaldataservice.entity.WaterMeterDataEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WaterMeterDataRepository extends JpaRepository<WaterMeterDataEntity, UUID> {
    List<WaterMeterDataEntity> findByItpIdOrderByMeasurementTimestampDesc(UUID itpId);

    Page<WaterMeterDataEntity> findByItpIdOrderByMeasurementTimestampDesc(UUID itpId, Pageable pageable);

    List<WaterMeterDataEntity> findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(
            UUID itpId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT w FROM WaterMeterDataEntity w WHERE w.itp.id = :itpId " +
            "AND w.measurementTimestamp = (SELECT MAX(w2.measurementTimestamp) " +
            "FROM WaterMeterDataEntity w2 WHERE w2.itp.id = :itpId)")
    List<WaterMeterDataEntity> findLatestByItpId(@Param("itpId") UUID itpId);

    @Query("SELECT AVG(w.gvsFlowValue) FROM WaterMeterDataEntity w " +
            "WHERE w.itp.id = :itpId AND w.measurementTimestamp BETWEEN :startDate AND :endDate")
    Double getAverageGvsFlowByItpIdAndPeriod(@Param("itpId") UUID itpId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(w.hvsFlowValue) FROM WaterMeterDataEntity w " +
            "WHERE w.itp.id = :itpId AND w.measurementTimestamp BETWEEN :startDate AND :endDate")
    Double getAverageHvsFlowByItpIdAndPeriod(@Param("itpId") UUID itpId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
}