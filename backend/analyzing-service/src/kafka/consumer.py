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
            self.settings.kafka.input_topic,
            bootstrap_servers=self.settings.kafka.bootstrap_servers,
            group_id=self.settings.kafka.group_id,
            auto_offset_reset='latest',
            value_deserializer=self._deserialize_message,
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
            json_data = json.loads(data.decode('utf-8'))
            message = ITPDataMessage.from_dict(json_data)
            
            if not message.validate():
                raise ValueError("Message validation failed")
            
            return message
        except Exception as e:
            logger.error(f"Error deserializing message: {e}")
            raise
    
    async def consume_messages(self) -> AsyncGenerator[Tuple[str, ITPDataMessage], None]:
        """Generator для получения типизированных сообщений"""
        try:
            async for kafka_message in self.consumer:
                itp_id = kafka_message.key
                itp_data_message = kafka_message.value
                
                if not itp_id:
                    logger.warning("Received message without ITP ID key, skipping")
                    continue
                
                # Дополнительная проверка соответствия ключа и ID в сообщении
                if itp_id != itp_data_message.itp_id:
                    logger.warning(f"Key mismatch: {itp_id} != {itp_data_message.itp_id}")
                
                logger.debug(f"Received message for ITP: {itp_id}")
                yield itp_id, itp_data_message
                
        except Exception as e:
            logger.error(f"Error consuming messages: {e}")
            raise