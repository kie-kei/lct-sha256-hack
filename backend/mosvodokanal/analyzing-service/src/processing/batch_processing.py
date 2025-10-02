import asyncio
from typing import List, Dict, Any
from models.messages import ITPDataMessage
from client.vodokanal_client import VodokanalDataServiceClient
from analysis.pre_analysis import PreAnalysis
from analysis.post_analysis import PostAnalysis
from analysis.conditions import ConditionChecker
from prediction.prediction_pipeline import PredictionPipeline
from kafka.producer import ITPDataKafkaProducer
from utils.logger import get_logger

logger = get_logger(__name__)

class BatchProcessor:
    def __init__(self, 
                 rest_client: VodokanalDataServiceClient,
                 prediction_pipeline: PredictionPipeline,
                 kafka_producer: ITPDataKafkaProducer):
        self.rest_client = rest_client
        self.prediction_pipeline = prediction_pipeline
        self.kafka_producer = kafka_producer
        
        self.pre_analysis = PreAnalysis()
        self.post_analysis = PostAnalysis()
        self.condition_checker = ConditionChecker()
    
    async def process_batch(self, itp_id: str, messages: List[ITPDataMessage]):
        """Основная логика обработки типизированного батча"""
        logger.info(f"Starting batch processing for ITP: {itp_id}, messages: {len(messages)}")
        
        try:
            # Валидация всех сообщений
            valid_messages = [msg for msg in messages if msg.validate()]
            
            if not valid_messages:
                logger.warning(f"No valid messages in batch for ITP {itp_id}")
                return
            
            # 1. Получаем средние значения расходов из REST API
            average_flows_data = await self._fetch_average_flows(itp_id, valid_messages)
            
            # 2. Pre-Analysis (математический анализ каждого сообщения)
            pre_analysis_result = await self.pre_analysis.analyze(
                itp_id, valid_messages, average_flows_data
            )
            
            # 3. Проверяем условие и отправляем AccidentMessage в Kafka если есть аномалии
            if self.condition_checker.check_pre_analysis_condition(pre_analysis_result):
                accident_messages = self.condition_checker.prepare_pre_analysis_message(pre_analysis_result)
                
                if accident_messages:  # Если есть сообщения об авариях
                    await self.kafka_producer.send_accident_messages(itp_id, accident_messages)
            
            # 4. Параллельные предсказания через модели
            prediction_results = await self.prediction_pipeline.process_batch(itp_id, valid_messages)
            
            # 5. Post-Analysis (математический анализ каждой точки прогноза)
            post_analysis_result = await self.post_analysis.analyze(itp_id, prediction_results)
            
            # 6. Проверяем условие и отправляем AccidentMessage в Kafka если есть аномалии в прогнозах
            if self.condition_checker.check_post_analysis_condition(post_analysis_result):
                forecast_accident_messages = self.condition_checker.prepare_post_analysis_message(post_analysis_result)
                
                if forecast_accident_messages:  # Если есть сообщения об авариях в прогнозах
                    await self.kafka_producer.send_accident_messages(f"{itp_id}_forecast", forecast_accident_messages)
            
            logger.info(f"Batch processing completed for ITP: {itp_id}")
            
        except Exception as e:
            logger.error(f"Error processing batch for ITP {itp_id}: {e}")
            raise

    async def _fetch_average_flows(self, itp_id: str, messages: List[ITPDataMessage]) -> Dict[str, Any]:
        """Получает средние значения расходов"""
        try:
            # Конвертируем ITP ID в int
            itp_id_int = int(itp_id) if itp_id.isdigit() else int(messages[0].itp.id)
            
            # Определяем период и час из первого сообщения батча
            first_message = messages[0]
            period = int(first_message.timestamp.timestamp())  # Unix timestamp
            hour = first_message.timestamp.hour
            
            logger.debug(f"Fetching average flows for ITP {itp_id_int}, period {period}, hour {hour}")
            
            # Получаем данные
            average_flows = await self.rest_client.get_average_flows(itp_id_int, period, hour)
            
            return {
                'average_flows': average_flows,
                'period': period,
                'hour': hour,
                'itp_id_int': itp_id_int
            }
            
        except Exception as e:
            logger.error(f"Error fetching average flows for ITP {itp_id}: {e}")
            return {
                'average_flows': {
                    'avg_hvs_flow': 0.0,
                    'avg_gvs_first_channel_flow': 0.0,
                    'avg_gvs_second_channel_flow': 0.0
                },
                'period': None,
                'hour': None,
                'itp_id_int': None
            }