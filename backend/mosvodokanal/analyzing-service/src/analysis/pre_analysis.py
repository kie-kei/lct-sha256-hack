from typing import List, Dict, Any
from models.messages import ITPDataMessage
from models.accident_message import AccidentMessage, ProbabilityType
from utils.logger import get_logger

logger = get_logger(__name__)

class PreAnalysis:
    def __init__(self):
        self.deviation_threshold = 10.0  # 10% порог для аномалий
        self.ratio_threshold = 4.0       # 4x порог для соотношения каналов

    async def analyze(self, itp_id: str, batch_messages: List[ITPDataMessage], external_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Математический анализ ПЕРЕД предсказанием - анализируем каждое сообщение
        """
        logger.info(f"Starting pre-analysis for ITP: {itp_id}")

        # Извлекаем средние значения из REST API
        average_flows = external_data.get('average_flows', {})
        historical_avg_hvs = average_flows.get('avg_hvs_flow', 0.0)
        historical_avg_gvs_first = average_flows.get('avg_gvs_first_channel_flow', 0.0)
        historical_avg_gvs_second = average_flows.get('avg_gvs_second_channel_flow', 0.0)

        # Анализируем каждое сообщение отдельно
        analyzed_messages = []
        total_anomalies = 0
        anomaly_details = []

        for msg_index, msg in enumerate(batch_messages):
            logger.debug(f"Analyzing message {msg_index + 1}/{len(batch_messages)} for ITP {itp_id}")
            
            message_analysis = await self._analyze_single_message(
                msg, historical_avg_hvs, historical_avg_gvs_first, historical_avg_gvs_second
            )
            
            analyzed_messages.append(message_analysis)
            
            # Считаем аномалии
            if message_analysis.get('has_anomalies', False):
                total_anomalies += 1
                anomaly_details.append({
                    'message_index': msg_index,
                    'timestamp': msg.timestamp,
                    'anomalies': message_analysis.get('detected_anomalies', [])
                })

        analysis_result = {
            'itp_id': itp_id,
            'message_count': len(batch_messages),
            'period': external_data.get('period'),
            'hour': external_data.get('hour'),
            
            # Исторические средние значения
            'historical_avg_hvs_flow': historical_avg_hvs,
            'historical_avg_gvs_first_channel_flow': historical_avg_gvs_first,
            'historical_avg_gvs_second_channel_flow': historical_avg_gvs_second,
            
            # Результаты анализа по сообщениям
            'analyzed_messages': analyzed_messages,
            'total_anomalies': total_anomalies,
            'anomaly_details': anomaly_details,
            'anomaly_rate': (total_anomalies / len(batch_messages) * 100) if batch_messages else 0,
            
            # Статус получения данных
            'average_flows_available': bool(average_flows)
        }

        logger.info(f"Pre-analysis completed for ITP: {itp_id}")
        logger.info(f"Found {total_anomalies} messages with anomalies out of {len(batch_messages)}")

        return analysis_result

    async def _analyze_single_message(self, msg: ITPDataMessage, 
                                    historical_avg_hvs: float, 
                                    historical_avg_gvs_first: float, 
                                    historical_avg_gvs_second: float) -> Dict[str, Any]:
        """Анализ одного сообщения на предмет аномалий"""
        
        # Собираем значения из сообщения
        gvs_first_values = []
        gvs_second_values = []
        hvs_values = []
        
        # ГВС данные
        for device in msg.odpu_gvs_devices:
            if device.first_channel_flow_value is not None:
                gvs_first_values.append(device.first_channel_flow_value)
            if device.second_channel_flow_value is not None:
                gvs_second_values.append(device.second_channel_flow_value)
        
        # ХВС данные
        for meter in msg.water_meters:
            if meter.flow_value is not None:
                hvs_values.append(meter.flow_value)
        
        # Средние значения для этого сообщения
        avg_gvs_first = sum(gvs_first_values) / len(gvs_first_values) if gvs_first_values else 0.0
        avg_gvs_second = sum(gvs_second_values) / len(gvs_second_values) if gvs_second_values else 0.0
        avg_hvs = sum(hvs_values) / len(hvs_values) if hvs_values else 0.0
        
        # Вычисляем отклонения
        detected_anomalies = []
        
        # 1. Проверяем отклонения больше 10%
        hvs_deviation_percent = self._calculate_deviation_percent(avg_hvs, historical_avg_hvs)
        gvs_first_deviation_percent = self._calculate_deviation_percent(avg_gvs_first, historical_avg_gvs_first)
        gvs_second_deviation_percent = self._calculate_deviation_percent(avg_gvs_second, historical_avg_gvs_second)
        
        is_hvs_anomaly = hvs_deviation_percent > self.deviation_threshold
        is_gvs_first_anomaly = gvs_first_deviation_percent > self.deviation_threshold  
        is_gvs_second_anomaly = gvs_second_deviation_percent > self.deviation_threshold
        
        if is_hvs_anomaly:
            detected_anomalies.append('hvs_consumption_anomaly')
        if is_gvs_first_anomaly:
            detected_anomalies.append('gvs_first_channel_anomaly')
        if is_gvs_second_anomaly:
            detected_anomalies.append('gvs_second_channel_anomaly')
        
        # 2. Несовпадение потоков: ХВС != (ГВС приход - ГВС обратка)
        gvs_consumption = avg_gvs_first - avg_gvs_second if (avg_gvs_first > 0 and avg_gvs_second > 0) else 0.0
        consumption_delta = abs(avg_hvs - gvs_consumption) if (avg_hvs > 0 and gvs_consumption > 0) else 0.0
        consumption_delta_percent = (consumption_delta / avg_hvs * 100) if avg_hvs > 0 else 0.0
        is_consumption_flows_anomaly = consumption_delta_percent > self.deviation_threshold
        
        if is_consumption_flows_anomaly:
            detected_anomalies.append('hvs_gvs_consumption_flows_anomaly')
        
        # 3. Соотношение ГВС каналов больше 4x
        gvs_ratio = (avg_gvs_first / avg_gvs_second) if avg_gvs_second > 0 else 0.0
        is_gvs_ratio_anomaly = gvs_ratio > self.ratio_threshold
        
        if is_gvs_ratio_anomaly:
            detected_anomalies.append('gvs_channels_flows_ratio_anomaly')
        
        # 4. Обратка больше прихода (отрицательное соотношение)
        is_negative_ratio_anomaly = avg_gvs_second > avg_gvs_first if (avg_gvs_first > 0 and avg_gvs_second > 0) else False
        negative_ratio = (avg_gvs_second / avg_gvs_first) if avg_gvs_first > 0 else 0.0
        
        if is_negative_ratio_anomaly:
            detected_anomalies.append('gvs_channels_flows_negative_ratio_anomaly')
        
        return {
            'timestamp': msg.timestamp,
            'itp_id': str(msg.itp.id),
            'itp_number': msg.itp.number,
            
            # Текущие значения
            'current_hvs_flow': avg_hvs,
            'current_gvs_first_channel_flow': avg_gvs_first,
            'current_gvs_second_channel_flow': avg_gvs_second,
            
            # Отклонения
            'hvs_deviation_percent': hvs_deviation_percent,
            'gvs_first_deviation_percent': gvs_first_deviation_percent,
            'gvs_second_deviation_percent': gvs_second_deviation_percent,
            
            # Аномалии отклонений
            'is_hvs_consumption_flow_anomaly': is_hvs_anomaly,
            'is_gvs_first_channel_flow_anomaly': is_gvs_first_anomaly,
            'is_gvs_second_channel_flow_anomaly': is_gvs_second_anomaly,
            
            # Потоки и соотношения
            'gvs_consumption_calculated': gvs_consumption,
            'consumption_flows_delta': consumption_delta,
            'consumption_flows_delta_percent': consumption_delta_percent,
            'is_hvs_gvs_consumption_flows_anomaly': is_consumption_flows_anomaly,
            
            'gvs_channels_flows_ratio': gvs_ratio,
            'is_gvs_channels_flows_ratio_anomaly': is_gvs_ratio_anomaly,
            
            'gvs_channels_flows_negative_ratio': negative_ratio,
            'is_gvs_channels_flows_negative_ratio_anomaly': is_negative_ratio_anomaly,
            
            # Общая информация об аномалиях
            'detected_anomalies': detected_anomalies,
            'has_anomalies': len(detected_anomalies) > 0,
            'anomaly_count': len(detected_anomalies)
        }

    def _calculate_deviation_percent(self, current_value: float, historical_value: float) -> float:
        """Вычисляет процентное отклонение"""
        if historical_value <= 0:
            return 0.0
        return abs(current_value - historical_value) / historical_value * 100