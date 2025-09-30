package ru.bluewater.vodokanaldataservice.repository;

import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bluewater.vodokanaldataservice.entity.ITPEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITPRepository extends JpaRepository<ITPEntity, UUID> {
    Optional<ITPEntity> findByNumber(String number);

    @Query("SELECT i FROM ITPEntity i LEFT JOIN FETCH i.mkd WHERE i.id = :id")
    Optional<ITPEntity> findByIdWithMKD(@Param("id") UUID id);

    @Query("SELECT i FROM ITPEntity i LEFT JOIN FETCH i.mkd LEFT JOIN FETCH i.waterMeterData WHERE i.id = :id")
    Optional<ITPEntity> findByIdWithDetails(@Param("id") UUID id);

    Page<ITPEntity> findByNumberContainingIgnoreCase(String number, Pageable pageable);

    boolean existsByNumber(String number);

    Page<ITPEntity> findAllByMkd_District_Name(String name, Pageable pageable);
}