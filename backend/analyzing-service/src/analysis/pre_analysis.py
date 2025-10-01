from typing import List, Dict, Any
from models.messages import ITPDataMessage
from utils.logger import get_logger

logger = get_logger(__name__)

class PreAnalysis:
    def __init__(self):
        pass
    
    async def analyze(self, itp_id: str, batch_messages: List[ITPDataMessage], external_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Математический анализ ПЕРЕД предсказанием
        
        Args:
            itp_id: Идентификатор ИТП
            batch_messages: Типизированный батч сообщений из Kafka
            external_data: Данные из главного сервиса
            
        Returns:
            Результат анализа
        """
        logger.info(f"Starting pre-analysis for ITP: {itp_id}")
        
        total_gvs_meters = sum(len(msg.odpu_gvs_devices) for msg in batch_messages)
        total_water_meters = sum(len(msg.water_meters) for msg in batch_messages)
        
        gvs_first_channel_flow_values = []
        gvs_second_channel_flow_values = []
        hvs_flow_values = []
        
        for msg in batch_messages:
            for device in msg.odpu_gvs_devices:
                if device.first_channel_flow_value is not None:
                    gvs_first_channel_flow_values.append(device.first_channel_flow_value)
                if device.second_channel_flow_value is not None:
                    gvs_second_channel_flow_values.append(device.second_channel_flow_value)
            
            for meter in msg.water_meters:
                if meter.flow_value is not None:
                    hvs_flow_values.append(meter.flow_value)
        
        analysis_result = {
            'itp_id': itp_id,
            'message_count': len(batch_messages),
            'total_gvs_meters': total_gvs_meters,
            'total_water_meters': total_water_meters,
        }
        
        logger.info(f"Pre-analysis completed for ITP: {itp_id}")
        return analysis_result