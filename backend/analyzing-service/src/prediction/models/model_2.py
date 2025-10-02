from typing import Any, Dict, List
from models.messages import ITPDataMessage
from prediction.prediction_model import PredictionModel
from utils.logger import get_logger

logger = get_logger(__name__)

class Model2(PredictionModel):
    def __init__(self):
        self.model_name = "Model_2"
    
    async def process(self, messages: List[ITPDataMessage]) -> Dict[str, Any]:
        logger.info(f"Processing {len(messages)} messages with {self.model_name}")
        
        # Делаем предикт тут

        logger.info(f"{self.model_name} processing completed")
        return {}