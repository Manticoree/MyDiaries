#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода инструкций упражнений на русский язык
"""

import json
import re

# Словарь переводов для общих фраз в инструкциях
INSTRUCTION_TRANSLATIONS = {
    # Основные команды и действия
    "Stand up": "Встаньте",
    "Lie down": "Лягте",
    "Sit down": "Сядьте",
    "Lie on your back": "Лягте на спину",
    "Lie back": "Откиньтесь назад",
    "Lie face down": "Лягте лицом вниз",
    "Begin by": "Начните с",
    "Start": "Начните",
    "Now": "Теперь",
    "Hold": "Удерживайте",
    "Keep": "Держите",
    "Maintain": "Поддерживайте",
    "Return": "Вернитесь",
    "Go back": "Вернитесь",
    "Reverse": "Верните",
    "Continue": "Продолжайте",
    "Repeat": "Повторите",
    "Switch": "Переключитесь",
    "Place": "Положите",
    "Position": "Установите",
    "Put": "Поставьте",
    "Take": "Возьмите",
    "Grab": "Возьмите",
    "Hold on to": "Ухватитесь за",
    "Use": "Используйте",
    "Lift": "Поднимите",
    "Lower": "Опустите",
    "Raise": "Поднимите",
    "Push": "Толкайте",
    "Pull": "Тяните",
    "Squeeze": "Сожмите",
    "Contract": "Сократите",
    "Extend": "Выпрямите",
    "Flex": "Согните",
    "Bend": "Согните",
    "Rotate": "Поверните",
    "Turn": "Поверните",
    "Twist": "Поверните",
    "Breathe in": "Вдыхайте",
    "Inhale": "Вдыхайте",
    "Breathe out": "Выдыхайте",
    "Exhale": "Выдыхайте",
    "Pause": "Сделайте паузу",
    "Hold for": "Удерживайте",
    "Wait": "Подождите",
    "Focus on": "Сосредоточьтесь на",
    "Concentrate on": "Сосредоточьтесь на",
    "Make sure": "Убедитесь, что",
    "Ensure": "Убедитесь, что",
    "Be sure to": "Убедитесь, что",
    "Try to": "Постарайтесь",
    "Attempt to": "Попытайтесь",
    "Avoid": "Избегайте",
    "Do not": "Не",
    "Don't": "Не",
    "Slowly": "Медленно",
    "Quickly": "Быстро",
    "Rapidly": "Быстро",
    "Immediately": "Немедленно",
    "Briefly": "Кратко",
    "Carefully": "Осторожно",
    "Gently": "Аккуратно",
    "Tightly": "Крепко",
    "Securely": "Надежно",
    "Firmly": "Крепко",
    "As possible": "Как можно",
    "As much as possible": "Как можно больше",
    "As far as possible": "Как можно дальше",
    "As you can": "Как можете",
    "As needed": "По мере необходимости",
    "If necessary": "При необходимости",
    "For a second": "На секунду",
    "For a moment": "На мгновение",
    "For 10 seconds": "На 10 секунд",
    "For 10-20 seconds": "На 10-20 секунд",
    "For 10-30 seconds": "На 10-30 секунд",
    "About 10 seconds": "Примерно на 10 секунд",
    "For the recommended amount of repetitions": "Рекомендуемое количество повторений",
    "For the prescribed amount of repetitions": "Предписанное количество повторений",
    "For 10-20 repetitions": "10-20 повторений",
    "For several reps": "Несколько повторений",

    # Части тела
    "your feet": "вашими ногами",
    "your legs": "вашими ногами",
    "your arms": "вашими руками",
    "your hands": "вашими руками",
    "your knees": "вашими коленями",
    "your hips": "вашими бедрами",
    "your torso": "вашим торсом",
    "your shoulders": "вашими плечами",
    "your head": "вашей головой",
    "your neck": "вашей шеей",
    "your back": "вашей спиной",
    "your chest": "вашей грудью",
    "your abs": "вашим прессом",
    "your stomach": "вашим животом",
    "your body": "вашим телом",
    "your foot": "вашей ногой",
    "your leg": "вашей ногой",
    "your arm": "вашей рукой",
    "your hand": "вашей рукой",
    "your elbow": "вашим локтем",
    "your wrist": "вашим запястьем",
    "your ankle": "вашей лодыжкой",
    "your heel": "вашей пяткой",
    "your toe": "вашим пальцем",
    "your thigh": "вашим бедром",
    "your calf": "вашей икрой",
    "your glutes": "вашими ягодицами",
    "your core": "вашим кором",
    "your upper back": "верхней частью спины",
    "your lower back": "поясницей",
    "your side": "в сторону",

    # Направления и положения
    "straight": "прямо",
    "forward": "вперед",
    "backward": "назад",
    "back": "назад",
    "up": "вверх",
    "down": "вниз",
    "to the side": "в сторону",
    "to the left": "влево",
    "to the right": "вправо",
    "to the front": "вперед",
    "to the back": "назад",
    "on the floor": "на пол",
    "on the ground": "на землю",
    "on your side": "на бок",
    "on your back": "на спину",
    "in front of you": "перед собой",
    "behind you": "позади вас",
    "at your side": "по бокам",
    "by your side": "по бокам",
    "over your head": "над головой",
    "above your head": "над головой",
    "behind your head": "за головой",
    "between your feet": "между ногами",
    "between your knees": "между коленями",
    "at shoulder level": "на уровне плеч",
    "at shoulder width": "на ширине плеч",
    "parallel to the floor": "параллельно полу",
    "perpendicular to the floor": "перпендикулярно полу",
    "parallel to the ground": "параллельно земле",
    "perpendicular to the ground": "перпендикулярно земле",

    # Спортинвентарь
    "barbell": "штангу",
    "dumbbell": "гантель",
    "kettlebell": "гирю",
    "weight": "вес",
    "weights": "веса",
    "resistance": "сопротивление",
    "band": "резиновую петлю",
    "rope": "канат",
    "bench": "скамью",
    "platform": "платформу",
    "machine": "тренажер",
    "pads": "подушки",
    "handles": "рукоятки",

    # Упражнения и движения
    "squat": "присед",
    "press": "жим",
    "lift": "подъем",
    "curl": "сгибание",
    "row": "тяга",
    "raise": "подъем",
    "extension": "разгибание",
    "flexion": "сгибание",
    "rotation": "вращение",
    "stretch": "растяжку",
    "pull-up": "подтягивание",
    "dip": "отжимание",
    "lunge": "выпад",
    "step": "шаг",
    "jump": "прыжок",
    "sprint": "спринт",
    "roll": "каток",
    "slide": "скольжение",

    # Другие термины
    "starting position": "исходное положение",
    "original position": "исходное положение",
    "initial position": "исходное положение",
    "the movement": "движение",
    "this movement": "это движение",
    "the exercise": "упражнение",
    "this exercise": "это упражнение",
    "the position": "положение",
    "the contraction": "сокращение",
    "the weight": "вес",
    "the bar": "штангу",
    "the floor": "пол",
    "the ground": "землю",
    "the air": "в воздух",
    "the rack": "стойку",
    "the bench": "скамью",
    "the ball": "мяч",
    "the wall": "стену",
    "the platform": "платформу",
    "the handle": "рукоятку",
    "the grips": "хваты",
    "the grip": "хват",
    "the tension": "натяжение",

    # Описания
    "as you breathe in": "на вдохе",
    "as you breathe out": "на выдохе",
    "as you inhale": "на вдохе",
    "as you exhale": "на выдохе",
    "while breathing in": "при вдохе",
    "while breathing out": "при выдохе",
    "as you perform this movement": "выполняя это движение",
    "during this portion of the movement": "во время этой части движения",
    "at all times": "все время",
    "at the same time": "в то же время",
    "simultaneously": "одновременно",
    "alternately": "поочередно",
    "alternating": "поочередно",
    "in turn": "по очереди",
    "one at a time": "по одному",
    "separately": "отдельно",
    "together": "вместе",
    "apart": "раздельно",

    # Количества и расстояния
    "about": "примерно",
    "approximately": "приблизительно",
    "slightly": "слегка",
    "a little": "немного",
    "a small amount": "немного",
    "a few": "несколько",
    "several": "несколько",
    "multiple": "несколько",
    "1 inch": "2.5 см",
    "2 inches": "5 см",
    "3-4 inches": "7.5-10 см",
    "18-24 inches": "45-60 см",
    "90 degrees": "90 градусов",
    "45 degrees": "45 градусов",
    "shoulder width": "ширина плеч",
    "hip width": "ширина бедер",
    "medium width": "средняя ширина",
    "wide": "широкий",
    "narrow": "узкий",

    # Технические советы
    "Tip:": "Совет:",
    "Note:": "Примечание:",
    "Caution:": "Осторожно:",
    "Warning:": "Предупреждение:",
    "Keep your back straight": "Держите спину прямо",
    "Keep your head up": "Держите голову поднятой",
    "Keep your arms extended": "Держите руки вытянутыми",
    "Keep your legs straight": "Держите ноги прямыми",
    "Keep your core tight": "Держите корпус в напряжении",
    "Maintain a straight back": "Поддерживайте прямую спину",
    "Maintain proper form": "Поддерживайте правильную форму",
    "Keep the weight under control": "Держите вес под контролем",
    "Do not swing": "Не раскачивайтесь",
    "Do not use momentum": "Не используйте инерцию",
    "Use a slow and controlled motion": "Используйте медленное и контролируемое движение",
    "Use your legs": "Используйте ноги",
    "Use your hips": "Используйте бедра",
    "Use your core": "Используйте корпус",
    "Drive through your heels": "Толкайтесь пятками",
    "Drive through your feet": "Толкайтесь стопами",
    "Push through the floor": "Толкайтесь от пола",
    "Engage your core": "Включите корпус",
    "Squeeze your glutes": "Сожмите ягодицы",
    "Squeeze your abs": "Сожмите пресс",
    "Tighten your abs": "Напрягите пресс",
    "Focus on contracting": "Сосредоточьтесь на сокращении",
}

def translate_instruction(text):
    """Переводит инструкцию с использованием словаря"""
    if not text:
        return text

    # Заменяем фразы
    for eng, rus in INSTRUCTION_TRANSLATIONS.items():
        # Замена с учетом регистра
        pattern = re.compile(re.escape(eng), re.IGNORECASE)
        text = pattern.sub(rus, text)

    return text

def main():
    import sys

    if len(sys.argv) < 3:
        print("Использование: python translate_instructions.py <входной_файл> <выходной_файл>")
        print("Пример: python translate_instructions.py exercises_ru.json exercises_full_ru.json")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # Чтение файла
    print(f"Чтение файла: {input_file}")
    with open(input_file, 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    # Перевод инструкций
    for i, exercise in enumerate(exercises, 1):
        if i % 100 == 0:
            print(f"Обработка: {i}/{len(exercises)}")

        if "instructions" in exercise:
            exercise["instructions"] = [translate_instruction(inst) for inst in exercise["instructions"]]

    # Сохранение файла
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print(f"Переведенный файл сохранен: {output_file}")
    print(f"\nВНИМАНИЕ: Это автоматический перевод. Рекомендуется проверить качество перевода.")

if __name__ == "__main__":
    main()
