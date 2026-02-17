#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода инструкций с использованием словаря переводов
"""

import json
import os

# Основные переводы
TRANSLATIONS = {
    # Основные фразы
    "Lie down on the floor": "Лягте на пол",
    "Lie on your back": "Лягте на спину",
    "Lie on your stomach": "Лягте на живот",
    "Lie on your side": "Лягте на бок",
    "Stand with your feet shoulder-width apart": "Встаньте, ноги на ширине плеч",
    "Stand with your feet hip-width apart": "Встаньте, ноги на ширине бедер",
    "Stand straight": "Встаньте прямо",
    "Stand upright": "Встаньте прямо",
    "Keep your back straight": "Держите спину прямо",
    "Keep your core engaged": "Напрягите мышцы пресса",
    "Keep your feet flat on the floor": "Держите стопы плотно прижатыми к полу",
    "Secure your feet": "Закрепите ноги",
    "Bend your knees": "Согните колени",
    "Bend your legs at the knees": "Согните ноги в коленях",
    "Extend your legs": "Выпрямите ноги",
    "Extend your arms": "Выпрямите руки",
    "Bend your arms": "Согните руки",
    "Bend at the waist": "Наклонитесь в талии",
    "Bend at the hips": "Наклонитесь в бедрах",
    "Return to starting position": "Вернитесь в исходное положение",
    "Return to the starting position": "Вернитесь в исходное положение",
    "Reverse the motion": "Выполните движение в обратном порядке",
    "This will be your starting position": "Это будет исходное положение",

    # Повторения
    "Repeat for the recommended amount of repetitions": "Повторите рекомендуемое количество раз",
    "Repeat for": "Повторите",
    "repetitions": "раз",
    "reps": "раз",
    "Hold for": "Удерживайте в течение",
    "seconds": "секунд",
    "Pause at the top": "Задержитесь в верхней точке",
    "Pause briefly": "Задержитесь ненадолго",

    # Движения
    "Slowly lower": "Медленно опустите",
    "Slowly raise": "Медленно поднимите",
    "Slowly lift": "Медленно поднимите",
    "Raise": "Поднимите",
    "Lift": "Поднимите",
    "Lower": "Опустите",
    "Pull": "Тяните",
    "Push": "Толкайте",
    "Press": "Жим",
    "Squeeze": "Сожмите",
    "Contract": "Сократите",
    "Relax": "Расслабьте",
    "Extend": "Выпрямите",
    "Flex": "Согните",
    "Bend": "Согните",
    "Straighten": "Выпрямите",

    # Дыхание
    "Exhale": "Выдохните",
    "Inhale": "Вдохните",
    "Breathe out": "Выдыхайте",
    "Breathe in": "Вдыхайте",
    "while breathing out": "выдыхая",
    "while breathing in": "вдыхая",
    "as you breathe out": "когда вы выдыхаете",
    "as you breathe in": "когда вы вдыхаете",

    # Части тела
    "Your hands": "Ваши руки",
    "Your arms": "Ваши руки",
    "Your legs": "Ваши ноги",
    "Your feet": "Ваши стопы",
    "Your back": "Ваша спина",
    "Your head": "Ваша голова",
    "Your torso": "Ваш торс",
    "Your hips": "Ваши бедра",
    "Your knees": "Ваши колени",
    "Your abs": "Ваш пресс",
    "Your core": "Ваш корпус",
    "Your body": "Ваше тело",
    "Your shoulders": "Ваши плечи",
    "Your chest": "Ваша грудь",
    "Your glutes": "Ваши ягодицы",

    # Направления и положения
    "in front of you": "перед собой",
    "behind you": "за спиной",
    "to your sides": "в стороны",
    "by your sides": "по бокам",
    "at your sides": "по бокам",
    "overhead": "над головой",
    "across your body": "через тело",
    "across your chest": "через грудь",
    "perpendicular to the ground": "перпендикулярно полу",
    "perpendicular to the floor": "перпендикулярно полу",
    "parallel to the floor": "параллельно полу",
    "at chest level": "на уровне груди",
    "at shoulder level": "на уровне плеч",
    "at waist level": "на уровне талии",
    "straight out": "прямо",
    "straight into the air": "прямо вверх",

    # Инвентарь
    "barbell": "штанга",
    "dumbbells": "гантели",
    "dumbbell": "гантели",
    "cable": "трос",
    "machine": "тренажер",
    "bench": "скамья",
    "mat": "коврик",
    "ball": "мяч",
    "band": "эспандер",
    "rope": "канат",
    "bar": "гриф",
    "pads": "подушечки",
    "handles": "рукоятки",
    "grip": "хват",

    # Прочее
    "and": "и",
    "or": "или",
    "with": "с",
    "without": "без",
    "while": "пока",
    "when": "когда",
    "then": "затем",
    "next": "далее",
    "finally": "наконец",
    "during": "во время",
    "before": "до",
    "after": "после",
    "if necessary": "при необходимости",
    "at the same time": "в то же время",
    "all the time": "все время",
    "at all times": "всегда",
    "the floor": "пол",
    "the ground": "пол",
    "position": "положение",
    "movement": "движение",
    "exercise": "упражнение",
    "weight": "вес",
    "resistance": "сопротивление",
    "contraction": "сокращение",
    "pause": "пауза",
    "form": "техника",
    "technique": "техника",
    "angle": "угол",
    "degree": "градус",
    "degrees": "градусов",
}

def translate_text(text):
    """Переводит текст по словарю"""
    if text in TRANSLATIONS:
        return TRANSLATIONS[text]

    result = text
    # Пробуем частичные замены (от длинных фраз к коротким)
    for eng, rus in sorted(TRANSLATIONS.items(), key=lambda x: len(x[0]), reverse=True):
        if eng.lower() in result.lower():
            result = result.replace(eng, rus)

    return result

def main():
    # Читаем JSON файл
    print("Чтение файла exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    # Создаем словарь переводов из файла, если существует
    dict_file = 'instructions_translations.json'
    if os.path.exists(dict_file):
        print("Загрузка существующего словаря переводов...")
        with open(dict_file, 'r', encoding='utf-8') as f:
            custom_translations = json.load(f)
            TRANSLATIONS.update(custom_translations)
        print(f"Загружено {len(custom_translations)} пользовательских переводов")

    # Переводим инструкции
    translated_count = 0
    for i, exercise in enumerate(exercises):
        if 'instructions' in exercise and exercise['instructions']:
            exercise['instructions'] = [translate_text(inst) for inst in exercise['instructions']]
            translated_count += 1

        if (i + 1) % 100 == 0:
            print(f"Обработано {i + 1} упражнений...")

    print(f"Переведено инструкций для {translated_count} упражнений")

    # Сохраняем результат
    print("Сохранение переведенного файла...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print("Готово!")
    print(f"\nДля добавления новых переводов создайте файл '{dict_file}'")
    print("в формате JSON: {\"английская фраза\": \"русский перевод\"}")

if __name__ == '__main__':
    main()
