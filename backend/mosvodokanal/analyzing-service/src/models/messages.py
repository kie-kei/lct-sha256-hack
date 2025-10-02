from dataclasses import dataclass
from typing import List, Optional, Dict, Any
from datetime import datetime
from decimal import Decimal
import uuid
from utils.logger import get_logger

logger = get_logger(__name__)

@dataclass
class ITPMessage:
    id: uuid.UUID
    number: str

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'ITPMessage':
        return cls(
            id=uuid.UUID(data['id']) if isinstance(data['id'], str) else data['id'],
            number=data['number']
        )

@dataclass
class MKDMessage:
    address: str
    fias: str
    unom: str
    latitude: Optional[Decimal] = None
    longitude: Optional[Decimal] = None

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'MKDMessage':
        return cls(
            address=data['address'],
            fias=data['fias'],
            unom=data['unom'],
            latitude=Decimal(str(data['latitude'])) if data.get('latitude') is not None else None,
            longitude=Decimal(str(data['longitude'])) if data.get('longitude') is not None else None
        )

@dataclass
class ODPUGVSDeviceMessage:
    heat_meter_identifier: uuid.UUID
    first_channel_flowmeter_identifier: uuid.UUID
    second_channel_flowmeter_identifier: uuid.UUID
    first_channel_flow_value: Optional[float] = None
    second_channel_flow_value: Optional[float] = None

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'ODPUGVSDeviceMessage':
        return cls(
            heat_meter_identifier=uuid.UUID(data['heatMeterIdentifier']),
            first_channel_flowmeter_identifier=uuid.UUID(data['firstChannelFlowmeterIdentifier']),
            second_channel_flowmeter_identifier=uuid.UUID(data['secondChannelFlowmeterIdentifier']),
            first_channel_flow_value=data.get('firstChannelFlowValue'),
            second_channel_flow_value=data.get('secondChannelFlowValue')
        )

@dataclass
class WaterMeterXVSITPMessage:
    identifier: uuid.UUID
    flow_value: Optional[float] = None

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'WaterMeterXVSITPMessage':
        return cls(
            identifier=uuid.UUID(data['identifier']),
            flow_value=data.get('flowValue')
        )

@dataclass
class ITPDataMessage:
    itp: ITPMessage
    mkd: MKDMessage
    odpu_gvs_devices: List[ODPUGVSDeviceMessage]
    water_meters: List[WaterMeterXVSITPMessage]
    timestamp: datetime

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'ITPDataMessage':
        """Создает объект из JSON словаря"""
        try:
            logger.debug(f"JSON keys: {list(data.keys())}")

            return cls(
                itp=ITPMessage.from_dict(data['itp']),  # ← исправлено
                mkd=MKDMessage.from_dict(data['mkd']),  # ← исправлено
                odpu_gvs_devices=[ODPUGVSDeviceMessage.from_dict(d) for d in data.get('odpuGvsDevices', [])],
                water_meters=[WaterMeterXVSITPMessage.from_dict(d) for d in data.get('waterMeters', [])],
                # Timestamp в миллисекундах
                timestamp=datetime.fromtimestamp(data['timestamp'] / 1000)
            )
        except Exception as e:
            logger.error(f"Error deserializing ITPDataMessage: {e}")
            logger.error(f"Data preview: {str(data)[:500]}...")
            raise ValueError(f"Invalid ITPDataMessage format: {e}")

    @property
    def itp_id(self) -> str:
        """Удобный getter для ITP ID"""
        return str(self.itp.id)

    def validate(self) -> bool:
        """Валидация сообщения"""
        try:
            assert self.itp is not None, "ITP message is required"
            assert self.itp.id is not None, "ITP ID is required"
            assert self.itp.number is not None and self.itp.number.strip(), "ITP number is required"
            assert self.mkd is not None, "MKD message is required"
            assert self.timestamp is not None, "Timestamp is required"
            return True
        except AssertionError as e:
            logger.error(f"Validation error: {e}")
            return False