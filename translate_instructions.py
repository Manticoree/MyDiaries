#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода инструкций упражнений на русский язык
"""

import json
import re

# Словарь для перевода общих терминов
COMMON_TERMS = {
    "Lie down on the floor": "Лягте на пол",
    "Lie on your back": "Лягте на спину",
    "Lie on your stomach": "Лягте на живот",
    "Lie on your side": "Лягте на бок",
    "Stand with your feet shoulder-width apart": "Встаньте, ноги на ширине плеч",
    "Stand straight": "Встаньте прямо",
    "Stand upright": "Встаньте прямо",
    "Keep your back straight": "Держите спину прямо",
    "Keep your core engaged": "Напрягите корпус",
    "Keep your feet flat on the floor": "Держите стопы плотно прижатыми к полу",
    "Place your feet": "Поставьте ноги",
    "Secure your feet": "Закрепите ноги",
    "Bend your knees": "Согните колени",
    "Extend your legs": "Выпрямите ноги",
    "Extend your arms": "Выпрямите руки",
    "Bend your arms": "Согните руки",
    "Bend at the waist": "Наклонитесь в талии",
    "Bend at the hips": "Наклонитесь в бедрах",
    "Return to starting position": "Вернитесь в исходное положение",
    "Reverse the motion": "Выполните движение в обратном порядке",
    "Repeat for": "Повторите",
    "repetitions": "раз",
    "reps": "раз",
    "Hold for": "Удерживайте в течение",
    "seconds": "секунд",
    "Pause at the top": "Задержитесь в верхней точке",
    "Pause briefly": "Задержитесь ненадолго",
    "Slowly lower": "Медленно опустите",
    "Slowly raise": "Медленно поднимите",
    "Exhale": "Выдохните",
    "Inhale": "Вдохните",
    "contracting": "сокращая",
    "squeezing": "напрягая",
    "grasp": "возьмите",
    "grip": "хват",
    "palms facing": "ладони обращены",
    "overhand grip": "прямой хват",
    "underhand grip": "обратный хват",
    "neutral grip": "нейтральный хват",
    "barbell": "штанга",
    "dumbbell": "гантели",
    "dumbbells": "гантели",
    "weight": "вес",
    "shoulder-width": "на ширине плеч",
    "hip-width": "на ширине бедер",
    "hands shoulder-width apart": "руки на ширине плеч",
    "feet shoulder-width apart": "ноги на ширине плеч",
    "to the sides": "в стороны",
    "forward": "вперед",
    "backward": "назад",
    "upward": "вверх",
    "downward": "вниз",
    "across your body": "через тело",
    "across your chest": "через грудь",
    "overhead": "над головой",
    "at shoulder level": "на уровне плеч",
    "at chest level": "на уровне груди",
    "at waist level": "на уровне талии",
    "in front of you": "перед собой",
    "behind you": "за спиной",
    "at your sides": "по бокам",
    "to your sides": "в стороны",
    "starting position": "исходное положение",
    "keep": "держите",
    "maintain": "сохраняйте",
    "engage": "напрягите",
    "squeeze": "сожмите",
    "contract": "сократите",
    "relax": "расслабьте",
    "lower": "опустите",
    "raise": "поднимите",
    "lift": "поднимите",
    "pull": "тяните",
    "push": "толкайте",
    "press": "жим",
    "rotate": "вращайте",
    "twist": "скручивайте",
    "bend": "согните",
    "straighten": "выпрямите",
    "extend": "выпрямите",
    "flex": "согните",
    "lean": "наклонитесь",
    "step": "шагните",
    "lunge": "делайте выпад",
    "jump": "прыгайте",
    "squat": "приседайте",
    "curl": "сгибайте",
    "extend": "разгибайте",
    "abduct": "отводите",
    "adduct": "приводите",
    "elevate": "поднимите",
    "depress": "опустите",
    "protract": "выдвигайте",
    "retract": "втягивайте",
    "floor": "пол",
    "ground": "земля/пол",
    "bench": "скамья",
    "machine": "тренажер",
    "cable": "трос",
    "rack": "стойка",
    "platform": "платформа",
    "mat": "коврик",
    "ball": "мяч",
    "band": "эспандер",
    "rope": "канат",
    "bar": "гриф",
    "rod": "штанга",
    "handle": "рукоятка",
    "strap": "ремень",
    "harness": "упряжь",
    "chain": "цепь",
    "kettlebell": "гиря",
    "kettlebells": "гири",
    "plate": "блин",
    "plates": "блины",
    "dip station": "станция для отжиманий",
    "pull-up bar": "турник",
    "EZ bar": "EZ-гриф",
    "trap bar": "гриф-ловушка",
    "Smith machine": "машина Смита",
    "leg press machine": "тренажер жима ногами",
    "leg curl machine": "тренажер для сгибания ног",
    "leg extension machine": "тренажер для разгибания ног",
    "crossover machine": "кроссовер",
    "cable machine": "блочный тренажер",
    "lat pulldown machine": "тренажер для тяги верхнего блока",
    "seated row machine": "тренажер для тяги гантели сидя",
    "chest press machine": "тренажер для жима груди",
    "shoulder press machine": "тренажер для жима плеч",
    "pec deck machine": "бабочка",
    "tricep extension machine": "тренажер для разгибания трицепса",
    "bicep curl machine": "тренажер для сгибания бицепса",
    "hack squat machine": "тренажер для приседаний Хака",
    "leg press": "жим ногами",
    "leg curl": "сгибание ног",
    "leg extension": "разгибание ног",
    "lat pulldown": "тяга верхнего блока",
    "seated row": "тяга гантели сидя",
    "chest press": "жим груди",
    "shoulder press": "жим плеч",
    "pec deck": "бабочка",
    "tricep extension": "разгибание трицепса",
    "bicep curl": "сгибание бицепса",
    "hack squat": "приседания Хака",
    "dip": "отжимание на брусьях",
    "pull-up": "подтягивание",
    "chin-up": "подтягивание обратным хватом",
    "push-up": "отжимание",
    "sit-up": "скручивание",
    "crunch": "скручивание",
    "plank": "планка",
    "lunge": "выпад",
    "squat": "приседание",
    "deadlift": "становая тяга",
    "bench press": "жим лежа",
    "incline bench press": "жим на наклонной скамье",
    "decline bench press": "жим на обратной наклонной скамье",
    "overhead press": "жим над головой",
    "military press": "армейский жим",
    "clean and jerk": "рывок и толчок",
    "snatch": "рывок",
    "clean": "подрыв",
    "jerk": "толчок",
    "front squat": "фронтальный присед",
    "back squat": "присед со штангой на плечах",
    "sumo deadlift": "становая тяга сумо",
    "romanian deadlift": "румынская становая тяга",
    "good morning": "доброе утро",
    "hyperextension": "гиперэкстензия",
    "back extension": "разгибание спины",
    "leg raise": "поднимание ног",
    "hanging leg raise": "поднимание ног в висе",
    "russian twist": "русский скручивание",
    "side plank": "боковая планка",
    "mountain climber": "альпинист",
    "burpee": "берпи",
    "jumping jack": "прыжок ногами врозь",
    "box jump": "прыжок на тумбу",
    "jump rope": "скалолазание с веревкой",
    "sprint": "спринт",
    "jog": "бег трусцой",
    "run": "бег",
    "walk": "ходьба",
    "swim": "плавание",
    "cycle": "велосипед",
    "row": "гребля",
}

# Словарь для перевода анатомических терминов
ANATOMY_TERMS = {
    "shoulders": "плечи",
    "shoulder": "плечо",
    "chest": "грудь",
    "pecs": "грудные мышцы",
    "pec": "грудная мышца",
    "back": "спина",
    "lats": "широкие мышцы спины",
    "lat": "широкая мышца спины",
    "traps": "трапеции",
    "trap": "трапеция",
    "rhomboids": "ромбовидные мышцы",
    "erector spinae": "разгибатели позвоночника",
    "lower back": "поясница",
    "upper back": "верх спины",
    "arms": "руки",
    "arms": "руки",
    "biceps": "бицепс",
    "bicep": "бицепс",
    "triceps": "трицепс",
    "tricep": "трицепс",
    "forearms": "предплечья",
    "forearm": "предплечье",
    "hands": "руки/кисти",
    "hand": "рука/кисть",
    "grip": "хват",
    "legs": "ноги",
    "leg": "нога",
    "thighs": "бедра",
    "thigh": "бедро",
    "quadriceps": "квадрицепс",
    "quad": "квадрицепс",
    "hamstrings": "бицепс бедра",
    "hamstring": "бицепс бедра",
    "glutes": "ягодицы",
    "glute": "ягодичная мышца",
    "gluteus": "ягодичная мышца",
    "calves": "икры",
    "calf": "икроножная мышца",
    "shins": "голени",
    "shin": "голень",
    "ankles": "лодыжки",
    "ankle": "лодыжка",
    "feet": "стопы",
    "foot": "стопа",
    "toes": "пальцы ног",
    "toe": "палец ноги",
    "core": "корпус",
    "abs": "пресс",
    "abdominals": "пресс",
    "abdominal": "мышца пресса",
    "obliques": "косые мышцы пресса",
    "oblique": "косая мышца пресса",
    "transverse abdominis": "поперечная мышца живота",
    "pelvis": "таз",
    "hips": "бедра/тазобедренные суставы",
    "hip": "бедро/тазобедренный сустав",
    "neck": "шея",
    "head": "голова",
    "spine": "позвоночник",
    "torso": "торс",
    "upper body": "верхняя часть тела",
    "lower body": "нижняя часть тела",
}

def translate_instruction(instruction):
    """Переводит инструкцию с английского на русский"""
    result = instruction

    # Сначала заменяем более длинные фразы
    for eng, rus in COMMON_TERMS.items():
        if len(eng) > 5:  # Заменяем только более длинные фразы
            result = re.sub(r'\b' + re.escape(eng) + r'\b', rus, result, flags=re.IGNORECASE)

    # Затем заменяем анатомические термины
    for eng, rus in ANATOMY_TERMS.items():
        result = re.sub(r'\b' + re.escape(eng) + r'\b', rus, result, flags=re.IGNORECASE)

    # Наконец заменяем короткие термины
    for eng, rus in COMMON_TERMS.items():
        if len(eng) <= 5:
            result = re.sub(r'\b' + re.escape(eng) + r'\b', rus, result, flags=re.IGNORECASE)

    return result

def main():
    # Читаем JSON файл
    print("Чтение файла exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    # Переводим инструкции
    translated_count = 0
    for i, exercise in enumerate(exercises):
        if 'instructions' in exercise and exercise['instructions']:
            original_instructions = exercise['instructions']
            translated_instructions = [translate_instruction(inst) for inst in original_instructions]
            exercise['instructions'] = translated_instructions
            translated_count += 1

        if (i + 1) % 100 == 0:
            print(f"Обработано {i + 1} упражнений...")

    print(f"Переведено инструкций для {translated_count} упражнений")

    # Сохраняем результат
    print("Сохранение переведенного файла...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print("Готово!")

if __name__ == '__main__':
    main()
