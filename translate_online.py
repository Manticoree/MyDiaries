#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода оставшихся английских фраз в exercises.json на русский язык
Использует онлайн API перевода MyMemory
"""

import json
import re
import time
from typing import List, Dict

# Кэш переводов
TRANSLATION_CACHE = {}

def translate_phrase(text: str) -> str:
    """Переводит фразу используя MyMemory API"""
    if not text or not isinstance(text, str):
        return text

    # Проверяем кэш
    cache_key = text.strip().lower()
    if cache_key in TRANSLATION_CACHE:
        return TRANSLATION_CACHE[cache_key]

    # Проверяем, есть ли английские слова
    if not re.search(r'[a-zA-Z]{3,}', text):
        return text

    try:
        # Используем бесплатный MyMemory API
        import requests
        url = "https://api.mymemory.translated.net/get"
        params = {
            'q': text,
            'langpair': 'en|ru'
        }

        response = requests.get(url, params=params, timeout=5)
        response.raise_for_status()

        data = response.json()

        if data['responseStatus'] == 200:
            translated = data['responseData']['translatedText']
            TRANSLATION_CACHE[cache_key] = translated
            time.sleep(0.1)  # Пауза между запросами
            return translated
        else:
            print(f"Ошибка API для '{text[:30]}...': {data.get('responseDetails', 'Unknown')}")
            return text
    except Exception as e:
        print(f"Ошибка перевода '{text[:30]}...': {e}")
        return text

def translate_instruction(instruction: str) -> str:
    """Переводит инструкцию, сохраняя уже переведенные части"""
    if not instruction:
        return instruction

    # Разбиваем на части, сохраняя структуру
    result = []

    # Ищем английские части и переводим их
    while instruction:
        # Находим первый английский фрагмент
        match = re.search(r'[a-zA-Z]{3,}[^а-яА-Я\s]{0,20}', instruction)

        if not match:
            # Остался только русский текст
            result.append(instruction)
            break

        # Добавляем русский текст до английского фрагмента
        russian_part = instruction[:match.start()]
        if russian_part:
            result.append(russian_part)

        # Переводим английский фрагмент
        en_part = match.group(0)
        translated_part = translate_phrase(en_part)
        result.append(translated_part)

        # Продолжаем с конца переведенного фрагмента
        instruction = instruction[match.end():]

    return ''.join(result)

def translate_instructions(instructions: List[str]) -> List[str]:
    """Переводит список инструкций"""
    return [translate_instruction(inst) for inst in instructions]

def main():
    print("Загрузка exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    translated_count = 0
    for idx, exercise in enumerate(exercises):
        # Проверяем, нужно ли переводить инструкции
        instructions = exercise.get('instructions', [])
        needs_trans = any(re.search(r'[a-zA-Z]{3,}', inst) for inst in instructions)

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

    print(f"\nГотово! Обработано {translated_count} упражнений")

if __name__ == '__main__':
    main()
