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
        """
        logger.info(f"Starting pre-analysis for ITP: {itp_id}")
        
        # Извлекаем средние значения из REST API
        average_flows = external_data.get('average_flows', {})
        historical_avg_hvs = average_flows.get('avg_hvs_flow', 0.0)
        historical_avg_gvs_first = average_flows.get('avg_gvs_first_channel_flow', 0.0)
        historical_avg_gvs_second = average_flows.get('avg_gvs_second_channel_flow', 0.0)
        
        # Собираем текущие значения из батча
        current_gvs_first_values = []
        current_gvs_second_values = []
        current_hvs_values = []
        
        for msg in batch_messages:
            for device in msg.odpu_gvs_devices:
                if device.first_channel_flow_value is not None:
                    current_gvs_first_values.append(device.first_channel_flow_value)
                if device.second_channel_flow_value is not None:
                    current_gvs_second_values.append(device.second_channel_flow_value)
            
            for meter in msg.water_meters:
                if meter.flow_value is not None:
                    current_hvs_values.append(meter.flow_value)
        
        # Вычисляем средние текущие значения
        current_avg_hvs = sum(current_hvs_values) / len(current_hvs_values) if current_hvs_values else 0.0
        current_avg_gvs_first = sum(current_gvs_first_values) / len(current_gvs_first_values) if current_gvs_first_values else 0.0
        current_avg_gvs_second = sum(current_gvs_second_values) / len(current_gvs_second_values) if current_gvs_second_values else 0.0

        # Сравниваем текущие значения со средними историческими
        hvs_deviation = abs(current_avg_hvs - historical_avg_hvs) if historical_avg_hvs > 0 else 0.0
        gvs_first_deviation = abs(current_avg_gvs_first - historical_avg_gvs_first) if historical_avg_gvs_first > 0 else 0.0
        gvs_second_deviation = abs(current_avg_gvs_second - historical_avg_gvs_second) if historical_avg_gvs_second > 0 else 0.0
        
        # Вычисляем процентные отклонения
        hvs_deviation_percent = (hvs_deviation / historical_avg_hvs * 100) if historical_avg_hvs > 0 else 0.0
        gvs_first_deviation_percent = (gvs_first_deviation / historical_avg_gvs_first * 100) if historical_avg_gvs_first > 0 else 0.0
        gvs_second_deviation_percent = (gvs_second_deviation / historical_avg_gvs_second * 100) if historical_avg_gvs_second > 0 else 0.0
        
        analysis_result = {
            'itp_id': itp_id,
            'message_count': len(batch_messages),
            'period': external_data.get('period'),
            'hour': external_data.get('hour'),
            
            # Исторические средние значения (из REST API)
            'historical_avg_hvs_flow': historical_avg_hvs,
            'historical_avg_gvs_first_channel_flow': historical_avg_gvs_first,
            'historical_avg_gvs_second_channel_flow': historical_avg_gvs_second,
            
            # Текущие средние значения (из батча сообщений)
            'current_avg_hvs_flow': current_avg_hvs,
            'current_avg_gvs_first_channel_flow': current_avg_gvs_first,
            'current_avg_gvs_second_channel_flow': current_avg_gvs_second,
            
            # Абсолютные отклонения
            'hvs_deviation': hvs_deviation,
            'gvs_first_deviation': gvs_first_deviation,
            'gvs_second_deviation': gvs_second_deviation,
            
            # Процентные отклонения
            'hvs_deviation_percent': hvs_deviation_percent,
            'gvs_first_deviation_percent': gvs_first_deviation_percent,
            'gvs_second_deviation_percent': gvs_second_deviation_percent,
            
            # Количество значений для анализа
            'hvs_values_count': len(current_hvs_values),
            'gvs_first_values_count': len(current_gvs_first_values),
            'gvs_second_values_count': len(current_gvs_second_values),
            
            # Статус получения данных
            'average_flows_available': bool(average_flows)
        }
        
        logger.info(f"Pre-analysis completed for ITP: {itp_id}")
        logger.debug(f"Analysis result: historical HVS={historical_avg_hvs}, current HVS={current_avg_hvs}, deviation={hvs_deviation_percent:.2f}%")
        
        return analysis_result