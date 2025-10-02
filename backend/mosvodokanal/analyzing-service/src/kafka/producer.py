import json
from typing import List, Dict, Any
from aiokafka import AIOKafkaProducer
from utils.logger import get_logger

logger = get_logger(__name__)

class ITPDataKafkaProducer:
    def __init__(self, settings):
        self.settings = settings
        self.producer = None
    
    async def start(self):
        self.producer = AIOKafkaProducer(
            bootstrap_servers=self.settings.kafka.bootstrap_servers,
            value_serializer=lambda x: json.dumps(x).encode('utf-8'),
            key_serializer=lambda x: x.encode('utf-8') if x else None
        )
        await self.producer.start()
        logger.info("Kafka producer started")
    
    async def stop(self):
        if self.producer:
            await self.producer.stop()
            logger.info("Kafka producer stopped")
    
    async def send_message(self, key: str, message: Dict[str, Any]):
        """Отправляет одно сообщение в Kafka"""
        try:
            await self.producer.send_and_wait(
                self.settings.kafka.output_topic, 
                value=message, 
                key=key
            )
            logger.debug(f"Message sent to Kafka for key: {key}")
        except Exception as e:
            logger.error(f"Failed to send message for key {key}: {e}")
            raise
    
    async def send_accident_messages(self, itp_id: str, accident_messages: List[Dict[str, Any]]):
        """Отправляет список AccidentMessage в Kafka"""
        try:
            for i, accident_msg in enumerate(accident_messages):
                # Используем ITP ID + индекс как ключ для уникальности
                message_key = f"{itp_id}_{i}_{accident_msg.get('measurementTimestamp', '')}"
                
                await self.producer.send_and_wait(
                    "accident-data",  # Специальный топик для инцидентов
                    value=accident_msg,
                    key=message_key
                )
                
            logger.info(f"Sent {len(accident_messages)} accident messages for ITP: {itp_id}")
            
        except Exception as e:
            logger.error(f"Failed to send accident messages for ITP {itp_id}: {e}")
            raise