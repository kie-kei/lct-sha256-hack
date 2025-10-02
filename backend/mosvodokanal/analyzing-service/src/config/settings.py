import os
from dataclasses import dataclass, field

@dataclass
class KafkaSettings:
    bootstrap_servers: str = os.getenv('KAFKA_BOOTSTRAP_SERVERS', 'localhost:9092')
    input_topic: str = os.getenv('KAFKA_INPUT_TOPIC', 'external-itp-data')
    output_topic: str = os.getenv('KAFKA_OUTPUT_TOPIC', 'analyzing-itp-data')
    group_id: str = os.getenv('KAFKA_GROUP_ID', 'itp-data-analyzing-group')

@dataclass
class BatchSettings:
    size: int = int(os.getenv('BATCH_SIZE', '5'))
    timeout_seconds: int = int(os.getenv('BATCH_TIMEOUT', '30'))

@dataclass
class RestSettings:
    vodokanal_base_url: str = os.getenv('VODOKANAL_BASE_URL', 'http://localhost:8081')
    timeout_seconds: int = int(os.getenv('REST_TIMEOUT', '10'))
    retries: int = int(os.getenv('REST_RETRIES', '3'))

@dataclass
class LoggingSettings:
    level: str = os.getenv('LOG_LEVEL', 'INFO')
    file_path: str = os.getenv('LOG_FILE_PATH', '/app/logs/analyzing.log')

@dataclass
class Settings:
    # Используем default_factory вместо прямых объектов
    kafka: KafkaSettings = field(default_factory=KafkaSettings)
    batch: BatchSettings = field(default_factory=BatchSettings)
    rest: RestSettings = field(default_factory=RestSettings)
    logging: LoggingSettings = field(default_factory=LoggingSettings)

settings = Settings()