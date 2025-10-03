from dataclasses import dataclass, asdict
from typing import List, Optional, Dict, Any
from datetime import datetime
from decimal import Decimal
import uuid

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

    def to_dict(self) -> Dict[str, Any]:
        return {
            'id': str(self.id),
            'number': self.number
        }

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

    def to_dict(self) -> Dict[str, Any]:
        return {
            'address': self.address,
            'fias': self.fias,
            'unom': self.unom,
            'latitude': float(self.latitude) if self.latitude else None,
            'longitude': float(self.longitude) if self.longitude else None
        }

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
            heat_meter_identifier=uuid.UUID(data['heatMeterIdentifier']) if isinstance(data['heatMeterIdentifier'], str) else data['heatMeterIdentifier'],
            first_channel_flowmeter_identifier=uuid.UUID(data['firstChannelFlowmeterIdentifier']) if isinstance(data['firstChannelFlowmeterIdentifier'], str) else data['firstChannelFlowmeterIdentifier'],
            second_channel_flowmeter_identifier=uuid.UUID(data['secondChannelFlowmeterIdentifier']) if isinstance(data['secondChannelFlowmeterIdentifier'], str) else data['secondChannelFlowmeterIdentifier'],
            first_channel_flow_value=data.get('firstChannelFlowValue'),
            second_channel_flow_value=data.get('secondChannelFlowValue')
        )

    def to_dict(self) -> Dict[str, Any]:
        return {
            'heatMeterIdentifier': str(self.heat_meter_identifier),
            'firstChannelFlowmeterIdentifier': str(self.first_channel_flowmeter_identifier),
            'secondChannelFlowmeterIdentifier': str(self.second_channel_flowmeter_identifier),
            'firstChannelFlowValue': self.first_channel_flow_value,
            'secondChannelFlowValue': self.second_channel_flow_value
        }

@dataclass
class WaterMeterXVSITPMessage:
    identifier: uuid.UUID
    flow_value: Optional[float] = None

    @classmethod
    def from_dict(cls, data: Dict[str, Any]) -> 'WaterMeterXVSITPMessage':
        return cls(
            identifier=uuid.UUID(data['identifier']) if isinstance(data['identifier'], str) else data['identifier'],
            flow_value=data.get('flowValue')
        )

    def to_dict(self) -> Dict[str, Any]:
        return {
            'identifier': str(self.identifier),
            'flowValue': self.flow_value
        }

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
            return cls(
                itp=ITPMessage.from_dict(data['itp']),
                mkd=MKDMessage.from_dict(data['mkd']),
                odpu_gvs_devices=[ODPUGVSDeviceMessage.from_dict(d) for d in data.get('odpuGvsDevices', [])],
                water_meters=[WaterMeterXVSITPMessage.from_dict(d) for d in data.get('waterMeters', [])],
                timestamp=datetime.fromtimestamp(data['timestamp'] / 1000)
            )
        except Exception as e:
            raise ValueError(f"Invalid ITPDataMessage format: {e}")

    def to_dict(self) -> Dict[str, Any]:
        return {
            'itp': self.itp.to_dict(),
            'mkd': self.mkd.to_dict(),
            'odpuGvsDevices': [device.to_dict() for device in self.odpu_gvs_devices],
            'waterMeters': [meter.to_dict() for meter in self.water_meters],
            'timestamp': self.timestamp.isoformat()
        }

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
            return False