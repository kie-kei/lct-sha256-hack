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
            for i, msg in enumerate(messages):
                if not msg.validate():
                    logger.error(f"Invalid message {i} in batch for ITP {itp_id}")
                    continue
            
            # 1. Получаем данные из REST API
            external_data = await self.rest_client.get_itp_data(itp_id)
            water_meter_data = await self.rest_client.get_water_meter_data(itp_id)
            
            # 2. Pre-Analysis (математический анализ)
            pre_analysis_result = await self.pre_analysis.analyze(
                itp_id, messages, {**external_data, **water_meter_data}
            )
            
            # 3. Проверяем условие и отправляем в Kafka если нужно
            if self.condition_checker.check_pre_analysis_condition(pre_analysis_result):
                message = self.condition_checker.prepare_pre_analysis_message(pre_analysis_result)
                await self.kafka_producer.send_message(itp_id, message)
            
            # 4. Параллельные предсказания через модели
            prediction_results = await self.prediction_pipeline.process_batch(itp_id, messages)
            
            # 5. Post-Analysis (математический анализ результатов предсказания)
            post_analysis_result = await self.post_analysis.analyze(itp_id, prediction_results)
            
            # 6. Проверяем условие и отправляем в Kafka если нужно
            if self.condition_checker.check_post_analysis_condition(post_analysis_result):
                message = self.condition_checker.prepare_post_analysis_message(post_analysis_result)
                await self.kafka_producer.send_message(itp_id, message)
            
            logger.info(f"Batch processing completed for ITP: {itp_id}")
            
        except Exception as e:
            logger.error(f"Error processing batch for ITP {itp_id}: {e}")
            raise