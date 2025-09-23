package ru.bluewater.vodokanaldataservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "water_meter_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterMeterDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    // ГВС данные
    @Column(name = "heat_meter_identifier")
    private UUID heatMeterIdentifier;
    @Column(name = "first_channel_flowmeter_identifier")
    private UUID firstChannelFlowmeterIdentifier;
    @Column(name = "second_channel_flowmeter_identifier")
    private UUID secondChannelFlowmeterIdentifier;
    @Column(name = "gvs_flow_value")
    private Integer gvsFlowValue;

    // ХВС данные
    @Column(name = "water_meter_identifier")
    private UUID waterMeterIdentifier;
    @Column(name = "hvs_flow_value")
    private Integer hvsFlowValue;
    @Column(name = "measurement_timestamp", nullable = false)
    private LocalDateTime measurementTimestamp;
    @ManyToOne
    @JoinColumn(name = "itp_id", nullable = false)
    private ITPEntity itp;

    // Задается на сервисе обогащения потока
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}