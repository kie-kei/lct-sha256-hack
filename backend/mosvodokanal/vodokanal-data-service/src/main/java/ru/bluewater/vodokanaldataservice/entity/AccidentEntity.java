package ru.bluewater.vodokanaldataservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.bluewater.integration.type.ProbabilityType;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "accident")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccidentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "itp_id", nullable = false)
    private ITPEntity itp;
    @Column(name = "measurement_timestamp", nullable = false)
    private Date measurementTimestamp;

    @Column(name = "probability_type")
    @Enumerated(EnumType.STRING)
    private ProbabilityType probabilityType;

    // Приход ГВС
    @Column(name = "is_gvs_first_channel_flow_anomaly")
    private Boolean isGvsFirstChannelFlowAnomaly;
    @Column(name = "gvs_standard_first_channel_flow_value")
    private Float gvsStandardFirstChannelFlowValue;
    @Column(name = "gvs_actual_first_channel_flow_value")
    private Float gvsActualFirstChannelFlowValue;

    // Обратка ГВС
    @Column(name = "is_gvs_second_channel_flow_anomaly")
    private Boolean isGvsSecondChannelFlowAnomaly;
    @Column(name = "gvs_standard_second_channel_flow_value")
    private Float gvsStandardSecondChannelFlowValue;
    @Column(name = "gvs_actual_second_channel_flow_value")
    private Float gvsActualSecondChannelFlowValue;

    // Потребление ХВС
    @Column(name = "is_hsv_consumption_flow_anomaly")
    private Boolean isHvsConsumptionFlowAnomaly;
    @Column(name = "hsv_standard_consumption_flow_value")
    private Float hvsStandardConsumptionFlowValue;
    @Column(name = "hvs_actual_consumption_flow_value")
    private Float hvsActualConsumptionFlowValue;

    // Несовпадение (Приход ГВС - Обратка ГВС) и Потребления ХВС
    @Column(name = "is_hvs_gvs_consumption_flows_anomaly")
    private Boolean isHvsGvsConsumptionFlowsAnomaly;
    @Column(name = "hvs_gvs_consumption_flows_delta")
    private Float hvsGvsConsumptionFlowsDelta;

    // Соотношение Прихода ГВС к Обратке ГВС превышает стандартное
    @Column(name = "is_gvs_channels_flows_ratio_anomaly")
    private Boolean isGvsChannelsFlowsRatioAnomaly;
    @Column(name = "gvs_channels_flows_ratio")
    private Float gvsChannelsFlowsRatio;

    // Приход ГВС превышает Обратку ГВС (отрицательное соотношение)
    @Column(name = "is_gvs_channels_flows_negative_ratio_anomaly")
    private Boolean isGvsChannelsFlowsNegativeRatioAnomaly;
    @Column(name = "gvs_channels_flows_negative_ratio")
    private Float gvsChannelsFlowsNegativeRatio;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}