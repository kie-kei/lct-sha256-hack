import asyncio
import json
from typing import AsyncGenerator, Tuple
from aiokafka import AIOKafkaConsumer
from models.messages import ITPDataMessage
from utils.logger import get_logger

logger = get_logger(__name__)

class ITPDataKafkaConsumer:
    def __init__(self, settings):
        self.settings = settings
        self.consumer = None

    async def start(self):
        self.consumer = AIOKafkaConsumer(
            self.settings.kafka.input_topic,  # ← ИСПРАВЛЕНО
            bootstrap_servers=self.settings.kafka.bootstrap_servers,  # ← ИСПРАВЛЕНО
            group_id=self.settings.kafka.group_id,  # ← ИСПРАВЛЕНО
            auto_offset_reset='latest',
            key_deserializer=lambda x: x.decode('utf-8') if x else None
        )
        await self.consumer.start()
        logger.info(f"Kafka consumer started for topic: {self.settings.kafka.input_topic}")

    async def stop(self):
        if self.consumer:
            await self.consumer.stop()
            logger.info("Kafka consumer stopped")

    def _deserialize_message(self, data: bytes) -> ITPDataMessage:
        """Десериализация сообщения в типизированный объект"""
        try:
            # Логируем сырые данные
            raw_text = data.decode('utf-8')
            logger.debug(f"Raw JSON text preview: {raw_text[:200]}...")

            json_data = json.loads(raw_text)
            logger.debug(f"JSON keys: {list(json_data.keys())}")

            # Исправляем названия полей согласно реальному JSON
            required_fields = ['itp', 'mkd', 'timestamp']  # ← исправлено!
            missing_fields = [field for field in required_fields if field not in json_data]
            if missing_fields:
                raise ValueError(f"Missing required fields: {missing_fields}")

            message = ITPDataMessage.from_dict(json_data)

            if not message.validate():
                raise ValueError("Message validation failed")

            logger.debug(f"Successfully created ITPDataMessage with {len(message.odpu_gvs_devices)} GVS devices")
            return message

        except Exception as e:
            logger.error(f"Deserialization error: {e}")
            logger.error(f"Raw data preview: {data[:300]}...")
            raise

    async def consume_messages(self) -> AsyncGenerator[Tuple[str, ITPDataMessage], None]:
        """Generator для получения типизированных сообщений"""
        try:
            async for kafka_message in self.consumer:
                itp_id = kafka_message.key

                if not itp_id:
                    logger.warning("Received message without ITP ID key, skipping")
                    continue

                try:
                    # Десериализуем вручную
                    itp_data_message = self._deserialize_message(kafka_message.value)

                    logger.debug(f"Successfully deserialized message for ITP: {itp_id}")
                    yield itp_id, itp_data_message

                except Exception as e:
                    logger.error(f"Failed to deserialize message for key {itp_id}: {e}")
                    continue
                
        except Exception as e:
            logger.error(f"Error consuming messages: {e}")
            raise