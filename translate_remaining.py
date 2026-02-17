#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода оставшихся английских фраз в exercises.json на русский язык
"""

import json
import re
from typing import List, Dict
from googletrans import Translator

# Функция для перевода текста
def translate_text(text: str, translator: Translator, max_retries: int = 3) -> str:
    """Переводит текст с английского на русский"""
    if not text or not isinstance(text, str):
        return text

    # Если текст уже содержит только русские буквы и цифры/знаки, пропускаем
    if re.match(r'^[\s\W\d\u0400-\u04FF]+$', text):
        return text

    # Пробуем перевести
    for attempt in range(max_retries):
        try:
            result = translator.translate(text, src='en', dest='ru')
            if result and result.text:
                return result.text
        except Exception as e:
            if attempt == max_retries - 1:
                print(f"Ошибка перевода для '{text[:50]}...': {e}")
                return text
            continue

    return text

# Функция для перевода инструкции
def translate_instruction(instruction: str, translator: Translator) -> str:
    """Переводит отдельную инструкцию"""
    # Если инструкция уже на русском (содержит только кириллицу), пропускаем
    if re.search(r'[a-zA-Z]{3,}', instruction):
        # Пробуем перевести
        return translate_text(instruction, translator)
    return instruction

# Функция для перевода списка инструкций
def translate_instructions(instructions: List[str], translator: Translator) -> List[str]:
    """Переводит список инструкций"""
    return [translate_instruction(inst, translator) for inst in instructions]

# Основная функция
def main():
    print("Загрузка exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    translator = Translator()

    translated_count = 0
    for idx, exercise in enumerate(exercises):
        # Проверяем, нужно ли переводить инструкции
        needs_translation = False
        for inst in exercise.get('instructions', []):
            # Если есть английские слова длиной более 2 букв
            if re.search(r'\b[a-zA-Z]{3,}\b', inst):
                needs_translation = True
                break

        if needs_translation:
            print(f"\nПеревод упражнения {idx + 1}/{len(exercises)}: {exercise.get('name', 'Unknown')}")

            # Переводим инструкции
            instructions = exercise.get('instructions', [])
            translated_instructions = translate_instructions(instructions, translator)
            exercise['instructions'] = translated_instructions

            translated_count += 1
            if translated_count % 10 == 0:
                print(f"Сохранение промежуточного результата... (переведено {translated_count})")
                with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
                    json.dump(exercises, f, ensure_ascii=False, indent=2)

    # Финальное сохранение
    print(f"\nСохранение финального результата...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print(f"\nГотово! Переведено {translated_count} упражнений")

if __name__ == '__main__':
    main()
