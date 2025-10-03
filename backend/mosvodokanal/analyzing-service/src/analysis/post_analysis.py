from typing import List, Dict, Any
from datetime import datetime
from models.forecast_data import ForecastPoint
from models.accident_message import AccidentMessage, ProbabilityType
from utils.logger import get_logger
import uuid

logger = get_logger(__name__)

class PostAnalysis:
    def __init__(self):
        self.deviation_threshold = 10.0  # 10% порог для аномалий
        self.ratio_threshold = 4.0       # 4x порог для соотношения каналов

    async def analyze(self, itp_id: str, prediction_results: Dict[str, Any]) -> Dict[str, Any]:
        """
        Математический анализ ПОСЛЕ предсказания
        
        Args:
            itp_id: Идентификатор ИТП
            prediction_results: Результаты от моделей предсказания (содержат 'forecasts')
            
        Returns:
            Результат анализа предсказаний
        """
        logger.info(f"Starting post-analysis for ITP: {itp_id}")
        
        forecasts = prediction_results.get(itp_id, {}).get('forecasts', {})
        
        if not forecasts:
            logger.warning(f"No forecasts available for post-analysis for ITP: {itp_id}")
            return {
                'itp_id': itp_id,
                'predictions_analyzed': 0,
                'anomalies_found': 0,
                'predictions_processed': False
            }
        
        # Конвертируем forecasts в временные точки
        forecast_points = self._convert_forecasts_to_points(forecasts)
        
        # Анализируем каждую временную точку
        analyzed_predictions = []
        total_anomalies = 0
        anomaly_details = []
        
        for point_index, forecast_point in enumerate(forecast_points):
            logger.debug(f"Analyzing forecast point {point_index + 1}/{len(forecast_points)} for ITP {itp_id}")
            
            point_analysis = await self._analyze_single_forecast_point(
                forecast_point, prediction_results
            )
            
            analyzed_predictions.append(point_analysis)
            
            # Считаем аномалии
            if point_analysis.get('has_anomalies', False):
                total_anomalies += 1
                anomaly_details.append({
                    'point_index': point_index,
                    'timestamp': forecast_point.timestamp,
                    'anomalies': point_analysis.get('detected_anomalies', [])
                })
        
        analysis_result = {
            'itp_id': itp_id,
            'predictions_analyzed': len(forecast_points),
            'total_anomalies': total_anomalies,
            'anomaly_rate': (total_anomalies / len(forecast_points) * 100) if forecast_points else 0,
            'analyzed_predictions': analyzed_predictions,
            'anomaly_details': anomaly_details,
            'predictions_processed': True,
            'forecast_models_used': list(forecasts.keys())
        }
        
        logger.info(f"Post-analysis completed for ITP: {itp_id}")
        logger.info(f"Found {total_anomalies} forecast points with anomalies out of {len(forecast_points)}")
        
        return analysis_result

    def _convert_forecasts_to_points(self, forecasts: Dict[str, List[Dict]]) -> List[ForecastPoint]:
        """Конвертирует forecasts в список ForecastPoint, сгруппированных по времени"""
        points_by_timestamp = {}
        
        # Собираем все данные по временным меткам
        for forecast_type, forecast_list in forecasts.items():
            for forecast_item in forecast_list:
                timestamp = forecast_item['timestamp']
                value = forecast_item['value']
                
                if timestamp not in points_by_timestamp:
                    points_by_timestamp[timestamp] = ForecastPoint(timestamp=timestamp)
                
                # Присваиваем значения по типам
                if forecast_type == 'supply_gvs':
                    points_by_timestamp[timestamp].supply_gvs_value = value
                elif forecast_type == 'return_gvs':
                    points_by_timestamp[timestamp].return_gvs_value = value
                elif forecast_type == 'consumption_xvs':
                    points_by_timestamp[timestamp].consumption_xvs_value = value
        
        # Сортируем по времени
        return sorted(points_by_timestamp.values(), key=lambda p: p.timestamp)

    async def _analyze_single_forecast_point(self, forecast_point: ForecastPoint, 
                                           prediction_results: Dict[str, Any]) -> Dict[str, Any]:
        """Анализ одной точки предсказания на предмет аномалий"""
        
        detected_anomalies = []
        
        # Получаем исторические средние (если были переданы в prediction_results)
        # Или используем средние значения из самих forecasts как базовые
        historical_supply_gvs = self._get_historical_average(prediction_results, 'supply_gvs')
        historical_return_gvs = self._get_historical_average(prediction_results, 'return_gvs')  
        historical_consumption_xvs = self._get_historical_average(prediction_results, 'consumption_xvs')
        
        current_supply = forecast_point.supply_gvs_value or 0.0
        current_return = forecast_point.return_gvs_value or 0.0
        current_consumption = forecast_point.consumption_xvs_value or 0.0
        
        # 1. Проверяем отклонения больше 10%
        supply_deviation_percent = self._calculate_deviation_percent(current_supply, historical_supply_gvs)
        return_deviation_percent = self._calculate_deviation_percent(current_return, historical_return_gvs)
        consumption_deviation_percent = self._calculate_deviation_percent(current_consumption, historical_consumption_xvs)
        
        is_supply_anomaly = supply_deviation_percent > self.deviation_threshold
        is_return_anomaly = return_deviation_percent > self.deviation_threshold
        is_consumption_anomaly = consumption_deviation_percent > self.deviation_threshold
        
        if is_supply_anomaly:
            detected_anomalies.append('gvs_supply_forecast_anomaly')
        if is_return_anomaly:
            detected_anomalies.append('gvs_return_forecast_anomaly')
        if is_consumption_anomaly:
            detected_anomalies.append('hvs_consumption_forecast_anomaly')
        
        # 2. Несовпадение потоков: ХВС != (ГВС приход - ГВС обратка)
        gvs_consumption_calculated = current_supply - current_return
        consumption_delta = abs(current_consumption - gvs_consumption_calculated)
        consumption_delta_percent = (consumption_delta / current_consumption * 100) if current_consumption > 0 else 0.0
        is_consumption_flows_anomaly = consumption_delta_percent > self.deviation_threshold and forecast_point.has_complete_gvs_data() and forecast_point.has_consumption_data()
        
        if is_consumption_flows_anomaly:
            detected_anomalies.append('hvs_gvs_consumption_flows_forecast_anomaly')
        
        # 3. Соотношение ГВС каналов больше 4x
        gvs_ratio = (current_supply / current_return) if current_return > 0 else 0.0
        is_gvs_ratio_anomaly = gvs_ratio > self.ratio_threshold and forecast_point.has_complete_gvs_data()
        
        if is_gvs_ratio_anomaly:
            detected_anomalies.append('gvs_channels_flows_ratio_forecast_anomaly')
        
        # 4. Обратка больше прихода (отрицательное соотношение)
        is_negative_ratio_anomaly = current_return > current_supply and forecast_point.has_complete_gvs_data()
        negative_ratio = (current_return / current_supply) if current_supply > 0 else 0.0
        
        if is_negative_ratio_anomaly:
            detected_anomalies.append('gvs_channels_flows_negative_ratio_forecast_anomaly')
        
        return {
            'timestamp': forecast_point.timestamp,
            'forecast_point_index': forecast_point.timestamp.hour,  # Можно использовать как индекс
            
            # Предсказанные значения
            'predicted_supply_gvs': current_supply,
            'predicted_return_gvs': current_return,
            'predicted_consumption_xvs': current_consumption,
            
            # Исторические средние для сравнения
            'historical_supply_gvs': historical_supply_gvs,
            'historical_return_gvs': historical_return_gvs,
            'historical_consumption_xvs': historical_consumption_xvs,
            
            # Отклонения
            'supply_deviation_percent': supply_deviation_percent,
            'return_deviation_percent': return_deviation_percent,
            'consumption_deviation_percent': consumption_deviation_percent,
            
            # Аномалии отклонений
            'is_gvs_supply_forecast_anomaly': is_supply_anomaly,
            'is_gvs_return_forecast_anomaly': is_return_anomaly,
            'is_hvs_consumption_forecast_anomaly': is_consumption_anomaly,
            
            # Потоки и соотношения
            'gvs_consumption_calculated': gvs_consumption_calculated,
            'consumption_flows_delta': consumption_delta,
            'consumption_flows_delta_percent': consumption_delta_percent,
            'is_hvs_gvs_consumption_flows_forecast_anomaly': is_consumption_flows_anomaly,
            
            'gvs_channels_flows_ratio': gvs_ratio,
            'is_gvs_channels_flows_ratio_forecast_anomaly': is_gvs_ratio_anomaly,
            
            'gvs_channels_flows_negative_ratio': negative_ratio,
            'is_gvs_channels_flows_negative_ratio_forecast_anomaly': is_negative_ratio_anomaly,
            
            # Общая информация об аномалиях
            'detected_anomalies': detected_anomalies,
            'has_anomalies': len(detected_anomalies) > 0,
            'anomaly_count': len(detected_anomalies)
        }

    def _get_historical_average(self, prediction_results: Dict[str, Any], forecast_type: str) -> float:
        """Получает историческое среднее значение для типа прогноза"""
        forecasts = prediction_results.get(list(prediction_results.keys())[0], {}).get('forecasts', {})
        forecast_data = forecasts.get(forecast_type, [])
        
        if not forecast_data:
            return 0.0
        
        # Берем среднее из всех предсказанных значений как baseline
        values = [item['value'] for item in forecast_data if item['value'] is not None]
        return sum(values) / len(values) if values else 0.0

    def _calculate_deviation_percent(self, current_value: float, historical_value: float) -> float:
        """Вычисляет процентное отклонение"""
        if historical_value <= 0:
            return 0.0
        return abs(current_value - historical_value) / historical_value * 100