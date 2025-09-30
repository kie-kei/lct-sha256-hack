package ru.bluewater.vodokanaldataservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bluewater.integration.type.ProbabilityType;
import ru.bluewater.vodokanaldataservice.entity.AccidentEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AccidentRepository extends JpaRepository<AccidentEntity, UUID> {
    List<AccidentEntity> findByItpIdOrderByMeasurementTimestampDesc(UUID itpId);

    Page<AccidentEntity> findByItpIdOrderByMeasurementTimestampDesc(UUID itpId, Pageable pageable);

    List<AccidentEntity> findByProbabilityTypeOrderByMeasurementTimestampDesc(ProbabilityType probabilityType);

    Page<AccidentEntity> findByProbabilityTypeOrderByMeasurementTimestampDesc(ProbabilityType probabilityType, Pageable pageable);

    List<AccidentEntity> findByMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(
            Date startDate, Date endDate);

    Page<AccidentEntity> findByMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(
            Date startDate, Date endDate, Pageable pageable);

    List<AccidentEntity> findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(
            UUID itpId, Date startDate, Date endDate);

    Page<AccidentEntity> findByItpIdAndMeasurementTimestampBetweenOrderByMeasurementTimestampDesc(
            UUID itpId, Date startDate, Date endDate, Pageable pageable);

    // Поиск по аномалиям
    @Query("SELECT a FROM AccidentEntity a WHERE " +
            "a.isGvsFirstChannelFlowAnomaly = :isGvsFirstChannelFlowAnomaly OR " +
            "a.isGvsSecondChannelFlowAnomaly = :isGvsSecondChannelFlowAnomaly OR " +
            "a.isHvsConsumptionFlowAnomaly = :isHvsConsumptionFlowAnomaly OR " +
            "a.isHvsGvsConsumptionFlowsAnomaly = :isHvsGvsConsumptionFlowsAnomaly OR " +
            "a.isGvsChannelsFlowsRatioAnomaly = :isGvsChannelsFlowsRatioAnomaly OR " +
            "a.isGvsChannelsFlowsNegativeRatioAnomaly = :isGvsChannelsFlowsNegativeRatioAnomaly " +
            "ORDER BY a.measurementTimestamp DESC")
    List<AccidentEntity> findAllByAnomalies(
            @Param("isGvsFirstChannelFlowAnomaly") Boolean isGvsFirstChannelFlowAnomaly,
            @Param("isGvsSecondChannelFlowAnomaly") Boolean isGvsSecondChannelFlowAnomaly,
            @Param("isHvsConsumptionFlowAnomaly") Boolean isHvsConsumptionFlowAnomaly,
            @Param("isHvsGvsConsumptionFlowsAnomaly") Boolean isHvsGvsConsumptionFlowsAnomaly,
            @Param("isGvsChannelsFlowsRatioAnomaly") Boolean isGvsChannelsFlowsRatioAnomaly,
            @Param("isGvsChannelsFlowsNegativeRatioAnomaly") Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    );

    @Query("SELECT a FROM AccidentEntity a WHERE a.itp.id = :itpId AND (" +
            "a.isGvsFirstChannelFlowAnomaly = :isGvsFirstChannelFlowAnomaly OR " +
            "a.isGvsSecondChannelFlowAnomaly = :isGvsSecondChannelFlowAnomaly OR " +
            "a.isHvsConsumptionFlowAnomaly = :isHvsConsumptionFlowAnomaly OR " +
            "a.isHvsGvsConsumptionFlowsAnomaly = :isHvsGvsConsumptionFlowsAnomaly OR " +
            "a.isGvsChannelsFlowsRatioAnomaly = :isGvsChannelsFlowsRatioAnomaly OR " +
            "a.isGvsChannelsFlowsNegativeRatioAnomaly = :isGvsChannelsFlowsNegativeRatioAnomaly)" +
            "ORDER BY a.measurementTimestamp DESC")
    List<AccidentEntity> findAllByItpIdAndAnomalies(
            @Param("itpId") UUID itpId,
            @Param("isGvsFirstChannelFlowAnomaly") Boolean isGvsFirstChannelFlowAnomaly,
            @Param("isGvsSecondChannelFlowAnomaly") Boolean isGvsSecondChannelFlowAnomaly,
            @Param("isHvsConsumptionFlowAnomaly") Boolean isHvsConsumptionFlowAnomaly,
            @Param("isHvsGvsConsumptionFlowsAnomaly") Boolean isHvsGvsConsumptionFlowsAnomaly,
            @Param("isGvsChannelsFlowsRatioAnomaly") Boolean isGvsChannelsFlowsRatioAnomaly,
            @Param("isGvsChannelsFlowsNegativeRatioAnomaly") Boolean isGvsChannelsFlowsNegativeRatioAnomaly
    );

    @Query("SELECT COUNT(a) FROM AccidentEntity a WHERE a.itp.id = :itpId")
    Long countByItpId(@Param("itpId") UUID itpId);

    @Query("SELECT COUNT(a) FROM AccidentEntity a WHERE a.itp.id = :itpId AND a.probabilityType = :probabilityType")
    Long countByItpIdAndProbabilityType(@Param("itpId") UUID itpId, @Param("probabilityType") ProbabilityType probabilityType);

    @Query("SELECT a FROM AccidentEntity a WHERE a.itp.id = :itpId " +
            "ORDER BY a.measurementTimestamp DESC LIMIT :limit")
    List<AccidentEntity> findLatestByItpId(@Param("itpId") UUID itpId, @Param("limit") int limit);
}