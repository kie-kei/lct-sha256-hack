import asyncio
import aiohttp
from typing import Dict, Any, Optional
from utils.logger import get_logger

logger = get_logger(__name__)

class VodokanalDataServiceClient:
    def __init__(self, settings):
        self.base_url = settings.rest.vodokanal_base_url
        self.timeout = aiohttp.ClientTimeout(total=settings.rest.timeout_seconds)
        self.retries = settings.rest.retries
        self.session: Optional[aiohttp.ClientSession] = None

    async def start(self):
        self.session = aiohttp.ClientSession(timeout=self.timeout)
        logger.info(f"REST client started for: {self.base_url}")

    async def stop(self):
        if self.session:
            await self.session.close()
            logger.info("REST client stopped")

    async def get_average_flows(self, itp_id: int, period: int, hour: int) -> Dict[str, float]:
        """
        Получает средние значения расходов для ITP за определенный период и час
        
        Args:
            itp_id: Идентификатор ITP (int)
            period: Период времени в unix формате
            hour: Час дня (0-23)
            
        Returns:
            Dict с полями: avg_hvs_flow, avg_gvs_first_channel_flow, avg_gvs_second_channel_flow
        """
        url = f"{self.base_url}/api/v1/water-meter-data/itp/{itp_id}/period-for-hour-averages"
        
        # Валидация входных данных
        if not (0 <= hour <= 23):
            raise ValueError(f"Hour must be between 0 and 23, got: {hour}")
        
        payload = {
            "itpId": itp_id,
            "days": 365,
            "hour": hour
        }
        
        logger.debug(f"Requesting average flows for ITP {itp_id}, period {period}, hour {hour}")

        for attempt in range(self.retries + 1):
            try:
                async with self.session.post(url, json=payload) as response:
                    if response.status == 200:
                        data = await response.json()
                        logger.debug(f"Successfully fetched average flows for ITP: {itp_id}")
                        
                        # Валидация ответа
                        expected_fields = ['avg_hvs_flow', 'avg_gvs_first_channel_flow', 'avg_gvs_second_channel_flow']
                        if all(field in data for field in expected_fields):
                            return {
                                'avg_hvs_flow': float(data['avg_hvs_flow']) if data['avg_hvs_flow'] is not None else 0.0,
                                'avg_gvs_first_channel_flow': float(data['avg_gvs_first_channel_flow']) if data['avg_gvs_first_channel_flow'] is not None else 0.0,
                                'avg_gvs_second_channel_flow': float(data['avg_gvs_second_channel_flow']) if data['avg_gvs_second_channel_flow'] is not None else 0.0
                            }
                        else:
                            logger.warning(f"Invalid response format for ITP {itp_id}: missing required fields")
                            return self._empty_flows_response()
                    
                    elif response.status == 404:
                        logger.warning(f"No data found for ITP {itp_id}, period {period}, hour {hour}")
                        return self._empty_flows_response()
                    
                    elif response.status == 400:
                        error_text = await response.text()
                        logger.error(f"Bad request for ITP {itp_id}: {error_text}")
                        raise ValueError(f"Bad request: {error_text}")
                    
                    else:
                        response_text = await response.text()
                        logger.warning(f"HTTP {response.status} for average flows ITP {itp_id}: {response_text}")

            except ValueError:
                # Не повторяем запросы при ошибках валидации
                raise
            except Exception as e:
                logger.error(f"Attempt {attempt + 1} failed for average flows ITP {itp_id}: {e}")
                if attempt == self.retries:
                    raise
                await asyncio.sleep(2 ** attempt)

        # Если все попытки неудачны, возвращаем пустой ответ
        logger.error(f"All attempts failed for average flows ITP {itp_id}")
        return self._empty_flows_response()

    def _empty_flows_response(self) -> Dict[str, float]:
        """Возвращает пустой ответ со средними значениями"""
        return {
            'avg_hvs_flow': 0.0,
            'avg_gvs_first_channel_flow': 0.0,
            'avg_gvs_second_channel_flow': 0.0
        }