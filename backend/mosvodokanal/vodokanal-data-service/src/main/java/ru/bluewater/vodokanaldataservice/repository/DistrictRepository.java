package ru.bluewater.vodokanaldataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bluewater.vodokanaldataservice.entity.DistrictEntity;

import java.util.Optional;
import java.util.UUID;

public interface DistrictRepository extends JpaRepository<DistrictEntity, UUID> {
    Optional<DistrictEntity> findByName(String name);
}
