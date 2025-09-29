import asyncio
import signal
from config.settings import settings
from kafka.consumer import ITPDataKafkaConsumer
from kafka.producer import ITPDataKafkaProducer
from client.vodokanal_client import VodokanalDataServiceClient
from batch.accumulator import BatchAccumulator
from prediction.prediction_pipeline import PredictionPipeline
from prediction.models.model_1 import Model1
from prediction.models.model_2 import Model2
from processing.batch_processing import BatchProcessor
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

        # Инициализация моделей и pipeline
        models = [Model1(), Model2()]
        self.prediction_pipeline = PredictionPipeline(models)

        self.batch_processor = BatchProcessor(
            self.rest_client,
            self.prediction_pipeline,
            self.producer
        )

        self.running = True

    async def start(self):
        """Запуск всех компонентов"""
        logger.info("Starting ITP Data Analyzing Service...")

        await self.consumer.start()
        await self.producer.start()
        await self.rest_client.start()

        # Запускаем основные задачи
        tasks = [
            asyncio.create_task(self.consume_messages()),
            asyncio.create_task(self.check_timeouts())
        ]

        try:
            await asyncio.gather(*tasks)
        except Exception as e:
            logger.error(f"Service error: {e}")
        finally:
            await self.stop()

    async def stop(self):
        """Остановка всех компонентов"""
        logger.info("Stopping ITP Data Analyzing Service...")
        self.running = False

        await self.consumer.stop()
        await self.producer.stop()
        await self.rest_client.stop()

    async def consume_messages(self):
        """Основной цикл получения сообщений"""
        async for itp_id, message in self.consumer.consume_messages():
            if not self.running:
                break

            # Добавляем сообщение в батч
            batch_ready = await self.accumulator.add_message(itp_id, message)

            if batch_ready:
                batch = self.accumulator.get_batch(itp_id)
                self._schedule_batch_processing(itp_id, batch)

    async def check_timeouts(self):
        """Проверка таймаутов батчей"""
        while self.running:
            async for itp_id, batch in self.accumulator.check_timeouts():
                self._schedule_batch_processing(itp_id, batch)

            await asyncio.sleep(5)  # Проверяем каждые 5 секунд

    def _schedule_batch_processing(self, itp_id, batch):
        async def runner():
            try:
                await self.batch_processor.process_batch(itp_id, batch)
            except Exception:
                logger.exception(f"Batch processing failed for ITP: {itp_id}")

        asyncio.create_task(runner())

async def main():
    service = ITPDataAnalyzingService()

    # Graceful shutdown
    def signal_handler():
        logger.info("Received shutdown signal")
        asyncio.create_task(service.stop())

    for sig in (signal.SIGTERM, signal.SIGINT):
        asyncio.get_event_loop().add_signal_handler(sig, signal_handler)

    await service.start()

if __name__ == "__main__":
    asyncio.run(main())