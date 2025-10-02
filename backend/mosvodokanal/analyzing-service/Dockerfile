FROM python:3.11-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Устанавливаем системные зависимости
RUN apt-get update && apt-get install -y \
    gcc \
    g++ \
    gfortran \
    libopenblas-dev \
    liblapack-dev \
    pkg-config \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Копируем requirements и устанавливаем зависимости
COPY requirements.txt .
RUN pip install --no-cache-dir --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

# Копируем исходный код
COPY . .

# Создаем директории
RUN mkdir -p /app/prediction/models /app/logs

# Устанавливаем переменные окружения
ENV PYTHONPATH=/app
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

# Настройки моделей
ENV FORCE_DOWNLOAD_MODELS=true
ENV MODEL_MAX_AGE_HOURS=24
ENV MODEL_UPDATE_INTERVAL_HOURS=12

# Создаем пользователя для безопасности
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Порт для health check
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD python -c "import os; exit(0 if os.path.exists('/app/prediction/models/sarimax_model_gvs_podacha.pkl') else 1)" || exit 1

# Запуск приложения
CMD ["python", "main.py"]