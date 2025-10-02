from dataclasses import dataclass
from typing import Optional, Dict, Any
from datetime import datetime
from enum import Enum
import uuid

class ProbabilityType(Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"

@dataclass
class AccidentMessage:
    itp_id: uuid.UUID
    measurement_timestamp: datetime
    probability_type: ProbabilityType
    
    # ГВС Приход
    is_gvs_first_channel_flow_anomaly: Optional[bool] = None
    gvs_standard_first_channel_flow_value: Optional[float] = None
    gvs_actual_first_channel_flow_value: Optional[float] = None
    
    # ГВС Обратка  
    is_gvs_second_channel_flow_anomaly: Optional[bool] = None
    gvs_standard_second_channel_flow_value: Optional[float] = None
    gvs_actual_second_channel_flow_value: Optional[float] = None
    
    # ХВС Потребление
    is_hvs_consumption_flow_anomaly: Optional[bool] = None
    hvs_standard_consumption_flow_value: Optional[float] = None
    hvs_actual_consumption_flow_value: Optional[float] = None
    
    # Несовпадение потоков
    is_hvs_gvs_consumption_flows_anomaly: Optional[bool] = None
    hvs_gvs_consumption_flows_delta: Optional[float] = None
    
    # Соотношения ГВС
    is_gvs_channels_flows_ratio_anomaly: Optional[bool] = None
    gvs_channels_flows_ratio: Optional[float] = None
    
    is_gvs_channels_flows_negative_ratio_anomaly: Optional[bool] = None
    gvs_channels_flows_negative_ratio: Optional[float] = None
    
    def to_dict(self) -> Dict[str, Any]:
        """Конвертация в словарь для отправки в Kafka"""
        return {
            'itpId': str(self.itp_id),
            'measurementTimestamp': int(self.measurement_timestamp.timestamp() * 1000),  # Java Date формат
            'probabilityType': self.probability_type.value,
            
            # ГВС Приход
            'isGvsFirstChannelFlowAnomaly': self.is_gvs_first_channel_flow_anomaly,
            'gvsStandardFirstChannelFlowValue': self.gvs_standard_first_channel_flow_value,
            'gvsActualFirstChannelFlowValue': self.gvs_actual_first_channel_flow_value,
            
            # ГВС Обратка
            'isGvsSecondChannelFlowAnomaly': self.is_gvs_second_channel_flow_anomaly,
            'gvsStandardSecondChannelFlowValue': self.gvs_standard_second_channel_flow_value,
            'gvsActualSecondChannelFlowValue': self.gvs_actual_second_channel_flow_value,
            
            # ХВС Потребление
            'isHvsConsumptionFlowAnomaly': self.is_hvs_consumption_flow_anomaly,
            'hvsStandardConsumptionFlowValue': self.hvs_standard_consumption_flow_value,
            'hvsActualConsumptionFlowValue': self.hvs_actual_consumption_flow_value,
            
            # Несовпадение потоков
            'isHvsGvsConsumptionFlowsAnomaly': self.is_hvs_gvs_consumption_flows_anomaly,
            'hvsGvsConsumptionFlowsDelta': self.hvs_gvs_consumption_flows_delta,
            
            # Соотношения ГВС
            'isGvsChannelsFlowsRatioAnomaly': self.is_gvs_channels_flows_ratio_anomaly,
            'gvsChannelsFlowsRatio': self.gvs_channels_flows_ratio,
            
            'isGvsChannelsFlowsNegativeRatioAnomaly': self.is_gvs_channels_flows_negative_ratio_anomaly,
            'gvsChannelsFlowsNegativeRatio': self.gvs_channels_flows_negative_ratio
        }