import logging
import sys
import os
from config.settings import settings

def get_logger(name: str) -> logging.Logger:
    logger = logging.getLogger(name)
    
    if not logger.handlers:
        # Уровень логирования
        log_level = getattr(logging, settings.logging.level.upper(), logging.INFO)
        logger.setLevel(log_level)
        
        # Формат логов
        formatter = logging.Formatter(
            '%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        )
        
        # Console handler
        console_handler = logging.StreamHandler(sys.stdout)
        console_handler.setFormatter(formatter)
        logger.addHandler(console_handler)
        
        # File handler (если директория существует)
        if os.path.exists(os.path.dirname(settings.logging.file_path)):
            file_handler = logging.FileHandler(settings.logging.file_path)
            file_handler.setFormatter(formatter)
            logger.addHandler(file_handler)
    
    return logger