import asyncio
import signal
from config.settings import settings
from kafka.consumer import ITPDataKafkaConsumer
from kafka.producer import ITPDataKafkaProducer
from client.vodokanal_client import VodokanalDataServiceClient
from batch.accumulator import BatchAccumulator
from prediction.prediction_pipeline import PredictionPipeline
from processing.batch_processor import BatchProcessor
from utils.model_loader import download_models
from utils.logger import get_logger

logger = get_logger(__name__)

class ITPDataAnalyzingService:
    def __init__(self):
        self.consumer = ITPDataKafkaConsumer(settings)
        self.producer = ITPDataKafkaProducer(settings)
        self.rest_client = VodokanalDataServiceClient(settings)
        self.accumulator = BatchAccumulator(
            settings.batch.size, 
            settings.batch.timeout_seconds
        )
        
        # Инициализация пайплайна предсказаний
        self.prediction_pipeline = PredictionPipeline()
        
        self.batch_processor = BatchProcessor(
            self.rest_client,
            self.prediction_pipeline,
            self.producer
        )
        
        self.running = True
    
    async def start(self):
        """Запуск всех компонентов"""
        logger.info("🚀 Starting ITP Data Analyzing Service...")
        
        try:
            # 1. Скачиваем модели
            model_paths = await download_models()
            
            # 2. Загружаем модели в пайплайн
            if model_paths:
                self.prediction_pipeline.load_sarimax_models(model_paths)
                models_info = self.prediction_pipeline.get_loaded_models_info()
                logger.info(f"📊 Loaded models: {list(models_info.keys())}")
            else:
                logger.warning("⚠️ No models loaded, predictions will be skipped")
            
            # 3. Запускаем Kafka компоненты
            await self.consumer.start()
            await self.producer.start()
            await self.rest_client.start()
            
            logger.info("✅ All components started successfully")
            
            # Запускаем основные задачи
            tasks = [
                asyncio.create_task(self.consume_messages()),
                asyncio.create_task(self.check_timeouts())
            ]
            
            await asyncio.gather(*tasks)
            
        except Exception as e:
            logger.error(f"Service startup error: {e}")
            raise
        finally:
            await self.stop()
    
    async def stop(self):
        """Остановка всех компонентов"""
        logger.info("🛑 Stopping ITP Data Analyzing Service...")
        self.running = False
        
        await self.consumer.stop()
        await self.producer.stop() 
        await self.rest_client.stop()
        
        logger.info("✅ Service stopped")
    
    async def consume_messages(self):
        """Основной цикл получения сообщений"""
        async for itp_id, message in self.consumer.consume_messages():
            if not self.running:
                break
                
            # Добавляем сообщение в батч
            batch_ready = await self.accumulator.add_message(itp_id, message)
            
            if batch_ready:
                batch = self.accumulator.get_batch(itp_id)
                # Запускаем обработку в фоне
                asyncio.create_task(self.batch_processor.process_batch(itp_id, batch))
    
    async def check_timeouts(self):
        """Проверка таймаутов батчей"""
        while self.running:
            async for itp_id, batch in self.accumulator.check_timeouts():
                # Запускаем обработку в фоне
                asyncio.create_task(self.batch_processor.process_batch(itp_id, batch))
            
            await asyncio.sleep(5)  # Проверяем каждые 5 секунд


async def main():
    """Главная функция"""
    service = ITPDataAnalyzingService()
    
    # Graceful shutdown
    def signal_handler():
        logger.info("Received shutdown signal")
        asyncio.create_task(service.stop())
    
    for sig in (signal.SIGTERM, signal.SIGINT):
        asyncio.get_event_loop().add_signal_handler(sig, signal_handler)
    
    try:
        await service.start()
    except KeyboardInterrupt:
        logger.info("Service interrupted by user")
    except Exception as e:
        logger.error(f"Service error: {e}")

if __name__ == "__main__":
    asyncio.run(main())