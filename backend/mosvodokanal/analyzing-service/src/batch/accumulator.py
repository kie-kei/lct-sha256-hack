import asyncio
from collections import defaultdict
from typing import Dict, List, Tuple, AsyncGenerator
from models.messages import ITPDataMessage
from utils.logger import get_logger

logger = get_logger(__name__)

class BatchAccumulator:
    def __init__(self, batch_size: int, timeout_seconds: int):
        self.batch_size = batch_size
        self.timeout_seconds = timeout_seconds
        self.batches: Dict[str, List[ITPDataMessage]] = defaultdict(list)
        self.last_activity: Dict[str, float] = {}
        
    async def add_message(self, itp_id: str, message: ITPDataMessage) -> bool:
        """Добавляет типизированное сообщение в батч. Возвращает True если батч готов"""
        self.batches[itp_id].append(message)
        self.last_activity[itp_id] = asyncio.get_event_loop().time()
        
        if len(self.batches[itp_id]) >= self.batch_size:
            logger.info(f"Batch ready for ITP {itp_id}: {len(self.batches[itp_id])} messages")
            return True
        return False
    
    def get_batch(self, itp_id: str) -> List[ITPDataMessage]:
        """Получает и очищает типизированный батч для ITP"""
        batch = self.batches.pop(itp_id, [])
        self.last_activity.pop(itp_id, None)
        return batch
    
    async def check_timeouts(self) -> AsyncGenerator[Tuple[str, List[ITPDataMessage]], None]:
        """Проверяет таймауты и возвращает готовые батчи"""
        current_time = asyncio.get_event_loop().time()
        
        timeout_itp_ids = []
        for itp_id, last_time in self.last_activity.items():
            if current_time - last_time >= self.timeout_seconds:
                if self.batches[itp_id]:  # Есть сообщения
                    timeout_itp_ids.append(itp_id)
        
        for itp_id in timeout_itp_ids:
            batch = self.get_batch(itp_id)
            logger.info(f"Batch timeout for ITP {itp_id}: {len(batch)} messages")
            yield itp_id, batch