#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода упражнений с английского на русский язык
Используйте словарь для замены общих терминов
"""

import json
import sys

# Словарь переводов общих терминов
TRANSLATIONS = {
    # Force types
    "pull": "тянуть",
    "push": "толкать",
    "static": "статическое",

    # Levels
    "beginner": "начинающий",
    "intermediate": "средний",
    "expert": "эксперт",

    # Mechanics
    "compound": "базовое",
    "isolation": "изолирующее",

    # Equipment
    "body only": "собственный вес",
    "machine": "тренажер",
    "other": "другое",
    "dumbbell": "гантели",
    "barbell": "штанга",
    "kettlebells": "гиря",
    "cable": "кроссовер",
    "bands": "резиновые петли",
    "medicine ball": "медицинский мяч",
    "exercise ball": "фитбол",
    "foam roll": "пенный ролик",

    # Categories
    "strength": "сила",
    "stretching": "растяжка",
    "plyometrics": "плиометрика",
    "strongman": "стронгмен",
    "powerlifting": "пауэрлифтинг",

    # Muscles
    "abdominals": "пресс",
    "biceps": "бицепс",
    "triceps": "трицепс",
    "chest": "грудь",
    "shoulders": "плечи",
    "back": "спина",
    "lats": "широчайшие",
    "middle back": "средняя часть спины",
    "lower back": "поясница",
    "traps": "трапеция",
    "forearms": "предплечья",
    "quadriceps": "квадрицепс",
    "hamstrings": "бицепс бедра",
    "calves": "икры",
    "glutes": "ягодицы",
    "adductors": "приводящие мышцы",
    "abductors": "отводящие мышцы",
}

# Перевод названий упражнений (примеры для демонстрации)
EXERCISE_NAME_TRANSLATIONS = {
    "3/4 Sit-Up": "3/4 Скручивание",
    "90/90 Hamstring": "Растяжка бицепса бедра 90/90",
    "Ab Crunch Machine": "Скручивание на тренажере",
    "Ab Roller": "Гимнастический ролик",
    "Adductor": "Пресс бедра на ролике",
    "Adductor/Groin": "Растяжка приводящих мышц",
    "Advanced Kettlebell Windmill": "Мельница с гирей (продвинутый)",
    "Air Bike": "Велосипед",
    "All Fours Quad Stretch": "Растяжка квадрицепса на четвереньках",
    "Alternate Hammer Curl": "Чередующиеся молотковые сгибания",
    "Alternate Heel Touchers": "Касания пяток",
    "Alternate Incline Dumbbell Curl": "Чередующиеся сгибания на наклонной скамье",
    "Alternate Leg Diagonal Bound": "Диагональные прыжки",
    "Alternating Cable Shoulder Press": "Чередующиеся жимы плеч на блоке",
    "Alternating Deltoid Raise": "Чередующиеся подъемы дельт",
    "Alternating Floor Press": "Чередующиеся жимы с пола",
    "Alternating Hang Clean": "Чередующиеся рывки с виса",
    "Alternating Kettlebell Press": "Чередующиеся жимы гири",
    "Alternating Kettlebell Row": "Чередующиеся тяги гири",
    "Alternating Renegade Row": "Тяга в планке",
    "Ankle Circles": "Круговые движения стопами",
    "Ankle On The Knee": "Растяжка ягодиц",
    "Anterior Tibialis-SMR": "Ролик для передней поверхности голени",
    "Anti-Gravity Press": "Жим на наклонной скамье лицом вниз",
    "Arm Circles": "Круговые движения руками",
    "Arnold Dumbbell Press": "Жим Арнольда",
    "Around The Worlds": "Мир вокруг",
    "Atlas Stone Trainer": "Тренажер для камня Атласа",
    "Atlas Stones": "Камень Атласа",
    "Axle Deadlift": "Тяга с толстой штангой",
    "Back Flyes - With Bands": "Разведение рук с эспандером",
    "Backward Drag": "Тяга санок назад",
    "Backward Medicine Ball Throw": "Бросок мяча назад",
    "Balance Board": "Балансборд",
    "Ball Leg Curl": "Сгибание ног на фитболе",
    "Band Assisted Pull-Up": "Подтягивания с резиной",
    "Band Good Morning": "Доброе утро с резиной",
    "Band Good Morning (Pull Through)": "Протяжка с резиной",
    "Band Hip Adductions": "Приведение бедра с резиной",
    "Band Pull Apart": "Разведение рук с резиной",
    "Band Skull Crusher": "Французский жим с резиной",
    "Barbell Ab Rollout": "Ролик со штангой",
    "Barbell Ab Rollout - On Knees": "Ролик со штангой с колен",
    "Barbell Bench Press - Medium Grip": "Жим штанги средним хватом",
    "Barbell Curl": "Сгибание штанги",
    "Barbell Curls Lying Against An Incline": "Сгибание на наклонной скамье",
    "Barbell Deadlift": "Становая тяга",
    "Barbell Full Squat": "Глубокий присед",
    "Barbell Glute Bridge": "Ягодичный мостик со штангой",
    "Barbell Guillotine Bench Press": "Гильотинский жим",
    "Barbell Hack Squat": "Хак-присед",
    "Barbell Hip Thrust": "Тазовый мост со штангой",
    "Barbell Incline Bench Press - Medium Grip": "Жим на наклонной скамье",
    "Barbell Incline Shoulder Raise": "Подъем плеч на наклонной скамье",
    "Barbell Lunge": "Выпады со штангой",
    "Barbell Rear Delt Row": "Тяга к подбородку в наклоне",
    "Barbell Rollout from Bench": "Ролик со скамьи",
    "Barbell Seated Calf Raise": "Подъем на носки сидя со штангой",
    "Barbell Shoulder Press": "Жим штанги стоя",
    "Barbell Shrug": "Шраги со штангой",
    "Barbell Shrug Behind The Back": "Шраги за спиной",
    "Barbell Side Bend": "Наклоны в стороны со штангой",
    "Barbell Side Split Squat": "Болгарский выпад",
    "Barbell Squat": "Присед со штангой",
    "Barbell Squat To A Bench": "Присед к скамье",
    "Barbell Step Ups": "Заход на платформу",
    "Barbell Walking Lunge": "Ходьба выпадами",
    "Battling Ropes": "Боевые канаты",
    "Bear Crawl Sled Drags": "Медвежья ходьба с санками",
    "Behind Head Chest Stretch": "Растяжка груди за головой",
    "Bench Dips": "Обратные отжимания",
    "Bench Jump": "Прыжок через скамью",
    "Bench Press - Powerlifting": "Жим лежа по пауэрлифтингу",
    "Bench Press - With Bands": "Жим лежа с резиной",
    "Bench Press with Chains": "Жим лежа с цепями",
    "Bench Sprint": "Спринт на скамье",
    "Bent-Arm Barbell Pullover": "Пуловер со штангой",
}

def translate_text(text):
    """Переводит текст с использованием словаря"""
    return TRANSLATIONS.get(text, text)

def translate_exercise(exercise):
    """Переводит одно упражнение"""
    translated = exercise.copy()

    # Перевод названия
    if exercise["name"] in EXERCISE_NAME_TRANSLATIONS:
        translated["name"] = EXERCISE_NAME_TRANSLATIONS[exercise["name"]]

    # Перевод простых полей
    for field in ["force", "level", "mechanic", "equipment", "category"]:
        if translated.get(field):
            translated[field] = translate_text(translated[field])

    # Перевод мышц
    if "primaryMuscles" in translated:
        translated["primaryMuscles"] = [translate_text(m) for m in translated["primaryMuscles"]]

    if "secondaryMuscles" in translated:
        translated["secondaryMuscles"] = [translate_text(m) for m in translated["secondaryMuscles"]]

    # Инструкции остаются на английском (требуют полного перевода)
    # Для перевода инструкций используйте онлайн-переводчик

    return translated

def main():
    if len(sys.argv) < 3:
        print("Использование: python translate_exercises.py <входной_файл> <выходной_файл>")
        print("Пример: python translate_exercises.py exercises.json exercises_ru.json")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    # Чтение исходного файла
    print(f"Чтение файла: {input_file}")
    with open(input_file, 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    # Перевод упражнений
    translated = [translate_exercise(ex) for ex in exercises]

    # Сохранение переведенного файла
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(translated, f, ensure_ascii=False, indent=2)

    print(f"Переведенный файл сохранен: {output_file}")
    print(f"\nВНИМАНИЕ: Инструкции остались на английском языке.")
    print("Для полного перевода инструкций используйте онлайн-переводчик.")

if __name__ == "__main__":
    main()
