package ru.bluewater.vodokanaldataservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mkd")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MKDEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "fias", nullable = false)
    private String fias;
    @Column(name = "unom", nullable = false)
    private String unom;
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    @OneToOne
    @JoinColumn(name = "itp_id", nullable = false)
    private ITPEntity itp;
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}