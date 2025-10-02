from dataclasses import dataclass
from datetime import datetime
from typing import Optional

@dataclass
class ForecastPoint:
    timestamp: datetime
    supply_gvs_value: Optional[float] = None
    return_gvs_value: Optional[float] = None
    consumption_xvs_value: Optional[float] = None
    
    def has_complete_gvs_data(self) -> bool:
        """Проверяет, есть ли полные данные ГВС для анализа соотношений"""
        return self.supply_gvs_value is not None and self.return_gvs_value is not None
    
    def has_consumption_data(self) -> bool:
        """Проверяет, есть ли данные потребления ХВС"""
        return self.consumption_xvs_value is not None