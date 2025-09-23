package ru.bluewater.vodokanaldataservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "itp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITPEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "number", nullable = false)
    private String number;
    @OneToOne(mappedBy = "itp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MKDEntity mkd;
    @OneToMany(mappedBy = "itp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WaterMeterDataEntity> waterMeterData;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}