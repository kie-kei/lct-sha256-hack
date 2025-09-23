package ru.bluewater.vodokanaldataservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bluewater.vodokanaldataservice.entity.MKDEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MKDRepository extends JpaRepository<MKDEntity, UUID> {
    Optional<MKDEntity> findByFias(String fias);

    Optional<MKDEntity> findByUnom(String unom);

    Optional<MKDEntity> findByItpId(UUID itpId);

    Page<MKDEntity> findByAddressContainingIgnoreCase(String address, Pageable pageable);

    @Query("SELECT m FROM MKDEntity m WHERE " +
            "(:lat1 - :radius) <= m.latitude AND m.latitude <= (:lat1 + :radius) AND " +
            "(:lon1 - :radius) <= m.longitude AND m.longitude <= (:lon1 + :radius)")
    List<MKDEntity> findByLocationNear(@Param("lat1") BigDecimal latitude,
                                       @Param("lon1") BigDecimal longitude,
                                       @Param("radius") BigDecimal radius);

    boolean existsByFias(String fias);

    boolean existsByUnom(String unom);
}