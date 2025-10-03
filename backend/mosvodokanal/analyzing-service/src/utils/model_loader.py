import os
import aiohttp
import asyncio
from utils.logger import get_logger

logger = get_logger(__name__)

async def download_models():
    """Скачивает модели из S3 при запуске"""
    models_dir = "prediction/models"
    os.makedirs(models_dir, exist_ok=True)
    
    # URL'ы моделей
    model_urls = {
        'sarimax_model_gvs_podacha.pkl': 'https://storage.yandexcloud.net/wedrochers/sarimax_model_gvs_podacha.pkl',
        'sarimax_model_gvs_obratka.pkl': 'https://storage.yandexcloud.net/wedrochers/sarimax_model_gvs_obratka.pkl',
        'sarimax_model_xvs.pkl': 'https://storage.yandexcloud.net/wedrochers/sarimax_model_xvs.pkl'
    }
    
    logger.info("📥 Downloading models from S3...")
    
    timeout = aiohttp.ClientTimeout(total=300)  # 5 минут
    async with aiohttp.ClientSession(timeout=timeout) as session:
        
        for filename, url in model_urls.items():
            file_path = os.path.join(models_dir, filename)
            
            # Проверяем существование файла
            if os.path.exists(file_path):
                logger.info(f"✅ Model {filename} already exists, skipping")
                continue
            
            try:
                logger.info(f"⬇️ Downloading {filename}...")
                
                async with session.get(url) as response:
                    if response.status == 200:
                        with open(file_path, 'wb') as f:
                            async for chunk in response.content.iter_chunked(8192):
                                f.write(chunk)
                        
                        file_size = os.path.getsize(file_path)
                        logger.info(f"✅ Downloaded {filename}: {file_size} bytes")
                    else:
                        logger.error(f"❌ Failed to download {filename}: HTTP {response.status}")
                        
            except Exception as e:
                logger.error(f"❌ Error downloading {filename}: {e}")
                if os.path.exists(file_path):
                    os.remove(file_path)  # Удаляем частично скачанный файл
    
    # Проверяем что все модели скачались
    downloaded_models = []
    for filename in model_urls.keys():
        file_path = os.path.join(models_dir, filename)
        if os.path.exists(file_path):
            downloaded_models.append(filename)
    
    logger.info(f"📊 Downloaded models: {len(downloaded_models)}/{len(model_urls)}")
    
    if len(downloaded_models) == len(model_urls):
        logger.info("🎉 All models downloaded successfully!")
    else:
        logger.warning(f"⚠️ Only {len(downloaded_models)} models downloaded")
    
    return {filename: os.path.join(models_dir, filename) 
            for filename in downloaded_models}