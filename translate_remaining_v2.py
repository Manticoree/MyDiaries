#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода оставшихся английских фраз в exercises.json на русский язык
Использует LibreTranslate API (бесплатный)
"""

import json
import re
import time
import requests
from typing import List, Dict, Set

# Кэш переводов для оптимизации
translation_cache: Dict[str, str] = {}

def get_cache_key(text: str) -> str:
    """Создает ключ для кэша на основе текста"""
    # Удаляем лишние пробелы и приводим к нижнему регистру
    return ' '.join(text.lower().split())

def translate_phrase(text: str, api_url: str = "https://libretranslate.de/translate") -> str:
    """Переводит фразу с английского на русский используя LibreTranslate API"""
    if not text or not isinstance(text, str):
        return text

    cache_key = get_cache_key(text)
    if cache_key in translation_cache:
        return translation_cache[cache_key]

    # Проверяем, есть ли в тексте английские слова (длиной > 2 буквы)
    if not re.search(r'\b[a-zA-Z]{3,}\b', text):
        translation_cache[cache_key] = text
        return text

    try:
        payload = {
            "q": text,
            "source": "en",
            "target": "ru",
            "format": "text"
        }
        response = requests.post(api_url, json=payload, timeout=10)
        response.raise_for_status()

        result = response.json().get('translatedText', text)
        translation_cache[cache_key] = result
        time.sleep(0.1)  # Пауза между запросами
        return result
    except Exception as e:
        print(f"Ошибка перевода '{text[:50]}...': {e}")
        return text

def translate_instruction(instruction: str) -> str:
    """Переводит отдельную инструкцию"""
    return translate_phrase(instruction)

def translate_instructions(instructions: List[str]) -> List[str]:
    """Переводит список инструкций"""
    return [translate_instruction(inst) for inst in instructions]

def needs_translation(text: str) -> bool:
    """Проверяет, нужен ли перевод (есть ли английские слова > 2 букв)"""
    return bool(re.search(r'\b[a-zA-Z]{3,}\b', text))

def main():
    print("Загрузка exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    translated_count = 0
    for idx, exercise in enumerate(exercises):
        # Проверяем, нужно ли переводить инструкции
        instructions = exercise.get('instructions', [])
        needs_trans = any(needs_translation(inst) for inst in instructions)

        if needs_trans:
            print(f"\nПеревод упражнения {idx + 1}/{len(exercises)}: {exercise.get('name', 'Unknown')}")

            # Переводим инструкции
            translated_instructions = translate_instructions(instructions)
            exercise['instructions'] = translated_instructions

            translated_count += 1

            # Сохраняем каждые 50 упражнений
            if translated_count % 50 == 0:
                print(f"Сохранение промежуточного результата... (переведено {translated_count})")
                with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
                    json.dump(exercises, f, ensure_ascii=False, indent=2)

    # Финальное сохранение
    print(f"\nСохранение финального результата...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print(f"\nГотово! Переведено {translated_count} упражнений")
    print(f"Размер кэша переводов: {len(translation_cache)}")

if __name__ == '__main__':
    main()
