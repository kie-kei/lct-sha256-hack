from typing import List, Dict, Any
from datetime import datetime, timedelta
import pickle
import json
from models.messages import ITPDataMessage
import statsmodels

#####

json_input_str = '''
[
    {
        "itp": {
            "id": "4fd787ff-8d98-4fee-bfe2-42749f425817",
            "number": "ИТП-7090-33"
        },
        "mkd": {
            "address": "Беговая ул., 32",
            "fias": "b2f1a5bd-e41e-44df-8d8f-275dbffed5da",
            "unom": "47571371",
            "latitude": null,
            "longitude": null,
            "district": null
        },
        "odpuGvsDevices": [
            {
                "heatMeterIdentifier": "f462f8c1-76d1-42e8-9d28-8109e059405f",
                "firstChannelFlowmeterIdentifier": "4c346e4b-68fe-45fb-848e-bd93db2f5eb4",
                "secondChannelFlowmeterIdentifier": "f5650ddb-3905-494d-bc58-086664cb314c",
                "firstChannelFlowValue": 7.084,
                "secondChannelFlowValue": 9.685
            }
        ],
        "waterMeters": [
            {
                "identifier": "3bfd83f5-92fc-4efc-b02f-3df143a8960f",
                "flowValue": 8.267
            }
        ],
        "timestamp": 1759356889132,
        "itpid": "4fd787ff-8d98-4fee-bfe2-42749f425817"
    },
    {
        "itp": {
            "id": "4fd787ff-8d98-4fee-bfe2-42749f425817",
            "number": "ИТП-7090-33"
        },
        "mkd": {
            "address": "Беговая ул., 32",
            "fias": "b2f1a5bd-e41e-44df-8d8f-275dbffed5da",
            "unom": "47571371",
            "latitude": null,
            "longitude": null,
            "district": null
        },
        "odpuGvsDevices": [
            {
                "heatMeterIdentifier": "f462f8c1-76d1-42e8-9d28-8109e059405f",
                "firstChannelFlowmeterIdentifier": "4c346e4b-68fe-45fb-848e-bd93db2f5eb4",
                "secondChannelFlowmeterIdentifier": "f5650ddb-3905-494d-bc58-086664cb314c",
                "firstChannelFlowValue": 7.084,
                "secondChannelFlowValue": 9.685
            }
        ],
        "waterMeters": [
            {
                "identifier": "3bfd83f5-92fc-4efc-b02f-3df143a8960f",
                "flowValue": 8.267
            }
        ],
        "timestamp": 1759366899132,
        "itpid": "4fd787ff-8d98-4fee-bfe2-42749f425817"
    },
    {
        "itp": {
            "id": "4fd787ff-8d98-4fee-bfe2-42749f425817",
            "number": "ИТП-7090-33"
        },
        "mkd": {
            "address": "Беговая ул., 32",
            "fias": "b2f1a5bd-e41e-44df-8d8f-275dbffed5da",
            "unom": "47571371",
            "latitude": null,
            "longitude": null,
            "district": null
        },
        "odpuGvsDevices": [
            {
                "heatMeterIdentifier": "f462f8c1-76d1-42e8-9d28-8109e059405f",
                "firstChannelFlowmeterIdentifier": "4c346e4b-68fe-45fb-848e-bd93db2f5eb4",
                "secondChannelFlowmeterIdentifier": "f5650ddb-3905-494d-bc58-086664cb314c",
                "firstChannelFlowValue": 7.084,
                "secondChannelFlowValue": 9.685
            }
        ],
        "waterMeters": [
            {
                "identifier": "3bfd83f5-92fc-4efc-b02f-3df143a8960f",
                "flowValue": 8.267
            }
        ],
        "timestamp": 1759376899132,
        "itpid": "4fd787ff-8d98-4fee-bfe2-42749f425817"
    },
    {
        "itp": {
            "id": "4fd787ff-8d98-4fee-bfe2-42749f425817",
            "number": "ИТП-7090-33"
        },
        "mkd": {
            "address": "Беговая ул., 32",
            "fias": "b2f1a5bd-e41e-44df-8d8f-275dbffed5da",
            "unom": "47571371",
            "latitude": null,
            "longitude": null,
            "district": null
        },
        "odpuGvsDevices": [
            {
                "heatMeterIdentifier": "f462f8c1-76d1-42e8-9d28-8109e059405f",
                "firstChannelFlowmeterIdentifier": "4c346e4b-68fe-45fb-848e-bd93db2f5eb4",
                "secondChannelFlowmeterIdentifier": "f5650ddb-3905-494d-bc58-086664cb314c",
                "firstChannelFlowValue": 7.084,
                "secondChannelFlowValue": 9.685
            }
        ],
        "waterMeters": [
            {
                "identifier": "3bfd83f5-92fc-4efc-b02f-3df143a8960f",
                "flowValue": 8.267
            }
        ],
        "timestamp": 1759386899132,
        "itpid": "4fd787ff-8d98-4fee-bfe2-42749f425817"
    }
]
'''

#####

class PredictionPipeline:
    """Пайплайн для прогнозирования показаний датчиков"""
    
    def __init__(self):
        self.sarimax_models = {}  # ключ: тип данных, значение: модель и метаданные
    
    def _determine_model_type(self, series_name: str) -> str:
        """
        Определяет тип модели по названию серии из training_data_info
        
        Args:
            series_name: название серии данных (из training_data_info)
            
        Returns:
            Тип модели: 'supply_gvs', 'return_gvs', 'consumption_xvs'
        """
        series_name_lower = series_name.lower()
        
        if 'подача' in series_name_lower or 'supply' in series_name_lower:
            return 'supply_gvs'
        elif 'обратка' in series_name_lower or 'return' in series_name_lower:
            return 'return_gvs'
        elif 'потребление' in series_name_lower or 'consumption' in series_name_lower or 'хвс' in series_name_lower:
            return 'consumption_xvs'
        else:
            raise ValueError(f"Не удалось определить тип модели для series_name: {series_name}")
    
    def load_sarimax_models(self, model_paths: Dict[str, str]):
        """
        Загрузка SARIMAX моделей из pickle файлов
        
        Args:
            model_paths: словарь с путями к моделям
                ключи: произвольные имена файлов
                значения: пути к файлам .pkl
        """
        for file_name, path in model_paths.items():
            try:
                with open(path, 'rb') as f:
                    loaded_package = pickle.load(f)
                
                # Определяем тип модели по series_name из training_data_info
                series_name = loaded_package['training_data_info']['series_name']
                model_type = self._determine_model_type(series_name)
                
                # Сохраняем модель с правильным типом
                self.sarimax_models[model_type] = {
                    'model': loaded_package['model'],
                    'order': loaded_package['order'],
                    'seasonal_order': loaded_package['seasonal_order'],
                    'training_info': loaded_package['training_data_info'],
                    'fit_date': loaded_package['fit_date'],
                    'source_file': file_name
                }
                
                print(f"✅ Модель {model_type} загружена из {file_name}")
                print(f"   Series: {series_name}")
                print(f"   Order: {loaded_package['order']}")
                print(f"   Seasonal Order: {loaded_package['seasonal_order']}")
                
            except Exception as e:
                print(f"❌ Ошибка загрузки модели из {file_name}: {e}")
    
    def get_loaded_models_info(self) -> Dict[str, Any]:
        """Возвращает информацию о загруженных моделях"""
        info = {}
        for model_type, model_data in self.sarimax_models.items():
            info[model_type] = {
                'series_name': model_data['training_info']['series_name'],
                'order': model_data['order'],
                'seasonal_order': model_data['seasonal_order'],
                'fit_date': model_data['fit_date'],
                'data_length': model_data['training_info'].get('data_length', 'unknown')
            }
        return info
    
    def extract_data_from_messages(self, messages: List[ITPDataMessage]) -> List[Dict[str, Any]]:
        """
        Извлечение данных из ITPDataMessage в простой формат
        
        Args:            messages: список сообщений от датчиков
            
        Returns:
            Список словарей с извлеченными данными
        """
        extracted_data = []
        
        for message in messages:
            # Извлекаем данные ГВС (берем первый прибор если есть)
            supply_gvs = None
            return_gvs = None
            temp1_gvs = None
            temp2_gvs = None
            
            if message.odpu_gvs_devices:
                device = message.odpu_gvs_devices[0]
                supply_gvs = device.first_channel_flow_value
                return_gvs = device.second_channel_flow_value
            
            # Извлекаем данные ХВС (берем первый счетчик если есть)
            consumption_xvs = None
            if message.water_meters:
                meter = message.water_meters[0]
                consumption_xvs = meter.flow_value
            
            # Создаем запись данных
            data_record = {
                'timestamp': message.timestamp,
                'supply_gvs': supply_gvs,
                'return_gvs': return_gvs,
                'consumption_xvs': consumption_xvs,
                'temp1_gvs': temp1_gvs,
                'temp2_gvs': temp2_gvs,
                'itp_id': str(message.itp.id),
                'itp_number': message.itp.number
            }
            
            # Добавляем только если есть хотя бы одно значение
            if any(v is not None for v in [supply_gvs, return_gvs, consumption_xvs]):
                extracted_data.append(data_record)
        
        return extracted_data
    
    def _retrain_model_with_new_data(self, model_type: str, new_valid_data: List[Dict[str, Any]]) -> bool:
        """
        Дообучение модели на новых валидных данных
        
        Args:
            model_type: тип модели для дообучения
            new_valid_data: новые валидные данные
            
        Returns:
            True если дообучение прошло успешно, False в противном случае
        """
        try:
            if model_type not in self.sarimax_models:
                print(f"❌ Модель {model_type} не найдена для дообучения")
                return False
            
            model_info = self.sarimax_models[model_type]
            old_model = model_info['model']
            
            # Извлекаем временные ряды для данного типа данных
            historical_values = []
            timestamps = []
            
            # Собираем все исторические данные для этого типа
            print(new_valid_data)
            for data_point in new_valid_data:
                value = data_point.get(model_type)
                if value is not None:
                    historical_values.append(value)
                    timestamps.append(data_point['timestamp'])
            
            if not historical_values:
                print(f"❌ Нет исторических данных для дообучения {model_type}")
                return False
            
            # Сортируем по времени
            sorted_data = sorted(zip(timestamps, historical_values), key=lambda x: x[0])
            sorted_values = [value for _, value in sorted_data]
            
            print(f"🔄 Дообучение модели {model_type} на {len(sorted_values)} точках данных...")
            
            # Создаем новую модель с теми же параметрами
            from statsmodels.tsa.statespace.sarimax import SARIMAX
            
            new_model = SARIMAX(
                sorted_values,
                order=model_info['order'],
                seasonal_order=model_info['seasonal_order'],
                enforce_stationarity=False,
                enforce_invertibility=False
            )
            
            # Используем параметры старой модели как начальные для ускорения обучения
            start_params = old_model.params
            
            # Обучаем новую модель
            new_result = new_model.fit(
                start_params=start_params,
                disp=False,
                method='lbfgs',
                maxiter=100
            )
            
            # Обновляем модель в хранилище
            model_info['model'] = new_result
            model_info['fit_date'] = datetime.now()
            model_info['training_info']['data_length'] = len(sorted_values)
            model_info['training_info']['last_date'] = timestamps[-1] if timestamps else None
            
            print(f"✅ Модель {model_type} успешно дообучена")
            print(f"   Новый размер данных: {len(sorted_values)}")
            print(f"   Дата дообучения: {datetime.now()}")
            
            return True
            
        except Exception as e:
            print(f"❌ Ошибка дообучения модели {model_type}: {e}")
            return False
    
    def process_batch(self, messages: List[ITPDataMessage], forecast_hours: int = 24, retrain_models: bool = True) -> Dict[str, Any]:
        """
        Основной метод обработки сообщений и прогнозирования
        
        Args:
            messages: список входящих сообщений
            forecast_hours: количество часов для прогноза
            retrain_models: нужно ли дообучать модели на новых данных
            
        Returns:
            Словарь с результатами обработки и прогнозами
        """
        results = {
            'processed_messages': len(messages),
            'anomalies_detected': 0,
            'valid_data_count': 0,
            'models_retrained': [],
            'forecasts': {},
            'anomaly_details': [],
            'processing_time': datetime.now()
        }
        
        # 1. Извлекаем данные из сообщений
        current_data_list = self.extract_data_from_messages(messages)
        
        if not current_data_list:
            print("⚠️ Нет данных для обработки")
            return results
        
        print(f"📊 Извлечено {len(current_data_list)} записей данных")
        
        if retrain_models and self.sarimax_models:
            print("🔄 Дообучение моделей на новых данных...")
            for model_type in self.sarimax_models.keys():
                if self._retrain_model_with_new_data(model_type, current_data_list):
                    results['models_retrained'].append(model_type)
        
        # 5. Выполняем прогнозирование если есть модели
        if self.sarimax_models:
            forecasts = self._generate_forecasts(forecast_hours)
            results['forecasts'] = forecasts
            print(f"📈 Сгенерировано прогнозов: {len(forecasts)}")
        else:
            print("⚠️ Модели не загружены, прогнозирование невозможно")
        
        return results
    
    def _generate_forecasts(self, hours: int, from_date: datetime = None) -> Dict[str, List[Dict[str, Any]]]:
        """
        Генерация прогнозов с использованием SARIMAX моделей
        
        Args:
            hours: количество часов для прогноза
            
        Returns:
            Словарь с прогнозами для каждого типа данных
        """
        forecasts = {}
        
        for model_type, model_info in self.sarimax_models.items():
            try:
                model = model_info['model']
                
                # Получаем прогноз
                forecast_values = model.forecast(steps=hours)
                
                # Создаем временные метки для прогноза
                last_timestamp = from_date if from_date else datetime.now()
                forecast_dates = [last_timestamp + timedelta(hours=i+1) for i in range(hours)]
                
                # Преобразуем в список точек
                forecast_points = []
                for i, value in enumerate(forecast_values):
                    point = {
                        'timestamp': forecast_dates[i],
                        'value': float(value),
                        'type': model_type,
                        'series_name': model_info['training_info']['series_name']
                    }
                    forecast_points.append(point)
                
                forecasts[model_type] = forecast_points
                
                print(f"✅ Прогноз для {model_type}: {len(forecast_points)} точек")
                
            except Exception as e:
                print(f"❌ Ошибка прогнозирования для {model_type}: {e}")
                forecasts[model_type] = []
        
        return forecasts
    
    def save_models(self, output_dir: str = "./models"):
        """
        Сохранение дообученных моделей
        
        Args:
            output_dir: директория для сохранения моделей
        """
        import os
        os.makedirs(output_dir, exist_ok=True)
        
        for model_type, model_info in self.sarimax_models.items():
            try:
                model_package = {
                    'model': model_info['model'],
                    'model_type': 'SARIMAX',
                    'order': model_info['order'],
                    'seasonal_order': model_info['seasonal_order'],
                    'training_data_info': model_info['training_info'],
                    'fit_date': model_info['fit_date']
                }
                
                filename = f"sarimax_model_{model_type}_{datetime.now().strftime('%Y%m%d_%H%M')}.pkl"
                filepath = os.path.join(output_dir, filename)
                
                with open(filepath, 'wb') as f:
                    pickle.dump(model_package, f)
                
                print(f"💾 Модель {model_type} сохранена в {filepath}")
                
            except Exception as e:
                print(f"❌ Ошибка сохранения модели {model_type}: {e}")

# Пример использования
def main():
    """Пример использования пайплайна"""
    
    # 1. Инициализация пайплайна
    pipeline = PredictionPipeline()
    
    # 2. Загрузка исторических данных
    historical_data = [
        {
            'timestamp': datetime(2025, 4, 26, 10, 0, 0),
            'supply_gvs': 0.15,
            'return_gvs': 0.10,
            'consumption_xvs': 0.07,
            'temp1_gvs': 52.0,
            'temp2_gvs': 31.0
        },
        # ... больше исторических данных
    ]
    
    # 3. Загрузка SARIMAX моделей
    model_paths = {
        'model_gvs_podacha': 'models/sarimax_model_gvs_podacha.pkl',
        'model_gvs_obratka': 'models/sarimax_model_gvs_obratka.pkl', 
        'model_xvs': 'models/sarimax_model_xvs.pkl'
    }
    
    pipeline.load_sarimax_models(model_paths)
    
    # 4. Показываем информацию о загруженных моделях
    models_info = pipeline.get_loaded_models_info()
    print("\n📋 Загруженные модели:")
    for model_type, info in models_info.items():
        print(f"   {model_type}: {info['series_name']}")
    
    # 5. Обработка входящих сообщений с дообучением
    sample_messages = [

    ]  # List[ITPDataMessage]

    data_list = json.loads(json_input_str)

    # Преобразуем каждый элемент словаря в объект ITPDataMessage
    sample_messages = [ITPDataMessage.from_dict(item) for item in data_list]
    
    results = pipeline.process_batch(
        sample_messages, 
        forecast_hours=24, 
        retrain_models=True  # Включено дообучение
    )
    # 6. Вывод результатов
    print(f"\n📊 Результаты обработки:")
    print(f"   Обработано сообщений: {results['processed_messages']}")
    print(f"   Обнаружено аномалий: {results['anomalies_detected']}")
    print(f"   Валидных данных: {results['valid_data_count']}")
    print(f"   Дообучено моделей: {len(results['models_retrained'])}")
    print(f"   Сгенерировано прогнозов: {len(results['forecasts'])}")
    
    # 7. Сохранение дообученных моделей (опционально)
    if results['models_retrained']:
        pipeline.save_models()
        print(f"💾 Дообученные модели сохранены")


if __name__ == "__main__":
    main()