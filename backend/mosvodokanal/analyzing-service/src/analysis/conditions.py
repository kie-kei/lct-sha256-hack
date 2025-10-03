from typing import Dict, Any, Optional, List
from models.accident_message import AccidentMessage, ProbabilityType
from utils.logger import get_logger
import uuid

logger = get_logger(__name__)

class ConditionChecker:
    def __init__(self):
        pass

    def check_pre_analysis_condition(self, analysis_result: Dict[str, Any]) -> bool:
        """
        Проверяет условие после pre-analysis
        Возвращает True если есть аномалии (отклонения больше 10%)
        """
        total_anomalies = analysis_result.get('total_anomalies', 0)
        condition_met = total_anomalies > 0
        
        if condition_met:
            logger.info(f"Pre-analysis condition met for ITP: {analysis_result.get('itp_id')} - found {total_anomalies} anomalies")
        
        return condition_met

    def check_post_analysis_condition(self, analysis_result: Dict[str, Any]) -> bool:
        """
        Проверяет условие после post-analysis
        Возвращает True если есть аномалии в прогнозах
        """
        total_anomalies = analysis_result.get('total_anomalies', 0)
        predictions_processed = analysis_result.get('predictions_processed', False)
        
        condition_met = predictions_processed and total_anomalies > 0
        
        if condition_met:
            logger.info(f"Post-analysis condition met for ITP: {analysis_result.get('itp_id')} - found {total_anomalies} forecast anomalies")
        
        return condition_met

    def prepare_pre_analysis_message(self, analysis_result: Dict[str, Any]) -> List[Dict[str, Any]]:
        """
        Формирует AccidentMessage для каждого сообщения с аномалиями
        """
        accident_messages = []
        
        analyzed_messages = analysis_result.get('analyzed_messages', [])
        itp_id = analysis_result.get('itp_id')
        
        # Исторические значения для сравнения
        historical_avg_hvs = analysis_result.get('historical_avg_hvs_flow', 0.0)
        historical_avg_gvs_first = analysis_result.get('historical_avg_gvs_first_channel_flow', 0.0)
        historical_avg_gvs_second = analysis_result.get('historical_avg_gvs_second_channel_flow', 0.0)
        
        for msg_analysis in analyzed_messages:
            if not msg_analysis.get('has_anomalies', False):
                continue  # Пропускаем сообщения без аномалий
            
            # Определяем уровень критичности
            probability_type = self._determine_probability_type(msg_analysis)
            
            # Создаем AccidentMessage
            accident = AccidentMessage(
                itp_id=uuid.UUID(itp_id),
                measurement_timestamp=msg_analysis['timestamp'],
                probability_type=probability_type,
                
                # ГВС Приход
                is_gvs_first_channel_flow_anomaly=msg_analysis.get('is_gvs_first_channel_flow_anomaly'),
                gvs_standard_first_channel_flow_value=historical_avg_gvs_first,
                gvs_actual_first_channel_flow_value=msg_analysis.get('current_gvs_first_channel_flow'),
                
                # ГВС Обратка
                is_gvs_second_channel_flow_anomaly=msg_analysis.get('is_gvs_second_channel_flow_anomaly'),
                gvs_standard_second_channel_flow_value=historical_avg_gvs_second,
                gvs_actual_second_channel_flow_value=msg_analysis.get('current_gvs_second_channel_flow'),
                
                # ХВС Потребление
                is_hvs_consumption_flow_anomaly=msg_analysis.get('is_hvs_consumption_flow_anomaly'),
                hvs_standard_consumption_flow_value=historical_avg_hvs,
                hvs_actual_consumption_flow_value=msg_analysis.get('current_hvs_flow'),
                
                # Несовпадение потоков
                is_hvs_gvs_consumption_flows_anomaly=msg_analysis.get('is_hvs_gvs_consumption_flows_anomaly'),
                hvs_gvs_consumption_flows_delta=msg_analysis.get('consumption_flows_delta'),
                
                # Соотношения ГВС
                is_gvs_channels_flows_ratio_anomaly=msg_analysis.get('is_gvs_channels_flows_ratio_anomaly'),
                gvs_channels_flows_ratio=msg_analysis.get('gvs_channels_flows_ratio'),
                
                is_gvs_channels_flows_negative_ratio_anomaly=msg_analysis.get('is_gvs_channels_flows_negative_ratio_anomaly'),
                gvs_channels_flows_negative_ratio=msg_analysis.get('gvs_channels_flows_negative_ratio')
            )
            
            accident_messages.append(accident.to_dict())
            
        logger.info(f"Prepared {len(accident_messages)} accident messages for ITP: {itp_id}")
        return accident_messages

    def prepare_post_analysis_message(self, analysis_result: Dict[str, Any]) -> List[Dict[str, Any]]:
        """
        Формирует AccidentMessage для каждой точки прогноза с аномалиями
        """
        accident_messages = []
        
        analyzed_predictions = analysis_result.get('analyzed_predictions', [])
        itp_id = analysis_result.get('itp_id')
        
        for pred_analysis in analyzed_predictions:
            if not pred_analysis.get('has_anomalies', False):
                continue  # Пропускаем прогнозы без аномалий
            
            # Определяем уровень критичности
            probability_type = self._determine_probability_type(pred_analysis)
            
            # Создаем AccidentMessage для прогноза
            accident = AccidentMessage(
                itp_id=uuid.UUID(itp_id),
                measurement_timestamp=pred_analysis['timestamp'],
                probability_type=probability_type,
                
                # ГВС Приход (из прогноза)
                is_gvs_first_channel_flow_anomaly=pred_analysis.get('is_gvs_supply_forecast_anomaly'),
                gvs_standard_first_channel_flow_value=pred_analysis.get('historical_supply_gvs'),
                gvs_actual_first_channel_flow_value=pred_analysis.get('predicted_supply_gvs'),
                
                # ГВС Обратка (из прогноза)  
                is_gvs_second_channel_flow_anomaly=pred_analysis.get('is_gvs_return_forecast_anomaly'),
                gvs_standard_second_channel_flow_value=pred_analysis.get('historical_return_gvs'),
                gvs_actual_second_channel_flow_value=pred_analysis.get('predicted_return_gvs'),
                
                # ХВС Потребление (из прогноза)
                is_hvs_consumption_flow_anomaly=pred_analysis.get('is_hvs_consumption_forecast_anomaly'),
                hvs_standard_consumption_flow_value=pred_analysis.get('historical_consumption_xvs'),
                hvs_actual_consumption_flow_value=pred_analysis.get('predicted_consumption_xvs'),
                
                # Несовпадение потоков (из прогноза)
                is_hvs_gvs_consumption_flows_anomaly=pred_analysis.get('is_hvs_gvs_consumption_flows_forecast_anomaly'),
                hvs_gvs_consumption_flows_delta=pred_analysis.get('consumption_flows_delta'),
                
                # Соотношения ГВС (из прогноза)
                is_gvs_channels_flows_ratio_anomaly=pred_analysis.get('is_gvs_channels_flows_ratio_forecast_anomaly'),
                gvs_channels_flows_ratio=pred_analysis.get('gvs_channels_flows_ratio'),
                
                is_gvs_channels_flows_negative_ratio_anomaly=pred_analysis.get('is_gvs_channels_flows_negative_ratio_forecast_anomaly'),
                gvs_channels_flows_negative_ratio=pred_analysis.get('gvs_channels_flows_negative_ratio')
            )
            
            accident_messages.append(accident.to_dict())
            
        logger.info(f"Prepared {len(accident_messages)} forecast accident messages for ITP: {itp_id}")
        return accident_messages

    def _determine_probability_type(self, analysis: Dict[str, Any]) -> ProbabilityType:
        """Определяет уровень критичности аномалии"""
        anomaly_count = analysis.get('anomaly_count', 0)
        
        # Находим максимальное отклонение
        max_deviation = 0.0
        for key in analysis.keys():
            if '_deviation_percent' in key:
                max_deviation = max(max_deviation, analysis.get(key, 0))
        
        # Логика определения критичности
        if anomaly_count >= 3 or max_deviation > 50:
            return ProbabilityType.CRITICAL
        elif anomaly_count >= 2 or max_deviation > 30:
            return ProbabilityType.HIGH
        elif anomaly_count >= 1 or max_deviation > 20:
            return ProbabilityType.MEDIUM
        else:
            return ProbabilityType.LOW

    def _calculate_deviation_percent(self, current_value: float, historical_value: float) -> float:
        """Вычисляет процентное отклонение"""
        if historical_value <= 0:
            return 0.0
        return abs(current_value - historical_value) / historical_value * 100