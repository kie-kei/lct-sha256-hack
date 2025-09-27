package ru.bluewater.vodokanaldataservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
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
    @ManyToOne
    @JoinColumn(name = "itp_id", nullable = false)
    private ITPEntity itp;
    @Column(name = "measurement_timestamp", nullable = false)
    private Date measurementTimestamp;

    // ГВС данные
    @Column(name = "heat_meter_identifier")
    private UUID heatMeterIdentifier;
    @Column(name = "first_channel_flowmeter_identifier")
    private UUID firstChannelFlowmeterIdentifier;
    @Column(name = "second_channel_flowmeter_identifier")
    private UUID secondChannelFlowmeterIdentifier;
    @Column(name = "gvs_first_channel_flow_value")
    private Float gvsFirstChannelFlowValue; // Подача
    @Column(name = "gvs_second_channel_flow_value")
    private Float gvsSecondChannelFlowValue; // Обратка
    @Column(name = "gvs_consumption_flow_value")
    private Float gvsConsumptionFlowValue; // Потребление

    // ХВС данные
    @Column(name = "water_meter_identifier")
    private UUID waterMeterIdentifier;
    @Column(name = "hvs_flow_value")
    private Float hvsFlowValue;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}