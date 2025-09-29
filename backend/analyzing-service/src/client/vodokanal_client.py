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

    async def get_itp_data(self, itp_id: str) -> Dict[str, Any]:
        """Получает данные по ITP из главного сервиса"""
        url = f"{self.base_url}/api/v1/itp/{itp_id}"

        for attempt in range(self.retries + 1):
            try:
                async with self.session.get(url) as response:
                    if response.status == 200:
                        data = await response.json()
                        logger.debug(f"Successfully fetched data for ITP: {itp_id}")
                        return data
                    elif response.status == 404:
                        logger.warning(f"ITP not found: {itp_id}")
                        return {}
                    else:
                        logger.warning(f"HTTP {response.status} for ITP: {itp_id}")

            except Exception as e:
                logger.error(f"Attempt {attempt + 1} failed for ITP {itp_id}: {e}")
                if attempt == self.retries:
                    raise
                await asyncio.sleep(2 ** attempt)

        return {}

    async def get_water_meter_data(self, itp_id: str) -> Dict[str, Any]:
        """Получает данные счетчиков по ITP"""
        url = f"{self.base_url}/api/v1/water-meter-data/itp/{itp_id}/latest"

        for attempt in range(self.retries + 1):
            try:
                async with self.session.get(url) as response:
                    if response.status == 200:
                        data = await response.json()
                        logger.debug(f"Successfully fetched water meter data for ITP: {itp_id}")
                        return data
                    else:
                        logger.warning(f"HTTP {response.status} for water meter data ITP: {itp_id}")

            except Exception as e:
                logger.error(f"Attempt {attempt + 1} failed for water meter data ITP {itp_id}: {e}")
                if attempt == self.retries:
                    raise
                await asyncio.sleep(2 ** attempt)

        return {}