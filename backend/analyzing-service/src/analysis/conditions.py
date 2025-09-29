from typing import Dict, Any, Optional
from utils.logger import get_logger

logger = get_logger(__name__)

class ConditionChecker:
    def __init__(self):
        pass

    def check_pre_analysis_condition(self, analysis_result: Dict[str, Any]) -> bool:
        """
        Проверяет условие после pre-analysis

        Returns:
            True если нужно отправить сообщение в Kafka
        """
        # МЕСТО ДЛЯ ТВОЕГО УСЛОВИЯ
        condition_met = analysis_result.get('message_count', 0) > 0

        if condition_met:
            logger.info(f"Pre-analysis condition met for ITP: {analysis_result.get('itp_id')}")

        return condition_met

    def check_post_analysis_condition(self, analysis_result: Dict[str, Any]) -> bool:
        """
        Проверяет условие после post-analysis

        Returns:
            True если нужно отправить сообщение в Kafka
        """
        # МЕСТО ДЛЯ ТВОЕГО УСЛОВИЯ
        condition_met = analysis_result.get('predictions_processed', False)

        if condition_met:
            logger.info(f"Post-analysis condition met for ITP: {analysis_result.get('itp_id')}")

        return condition_met

    def prepare_pre_analysis_message(self, analysis_result: Dict[str, Any]) -> Dict[str, Any]:
        """
        МЕСТО ДЛЯ ФОРМИРОВАНИЯ СООБЩЕНИЯ после pre-analysis
        """
        return {
            'type': 'pre_analysis_result',
            'itp_id': analysis_result.get('itp_id'),
            'data': analysis_result,
            # Добавь свои поля
        }

    def prepare_post_analysis_message(self, analysis_result: Dict[str, Any]) -> Dict[str, Any]:
        """
        МЕСТО ДЛЯ ФОРМИРОВАНИЯ СООБЩЕНИЯ после post-analysis
        """
        return {
            'type': 'post_analysis_result',
            'itp_id': analysis_result.get('itp_id'),
            'data': analysis_result,
            # Добавь свои поля
        }