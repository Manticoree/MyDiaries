#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода инструкций упражнений на русский язык v2
"""

import json
import re

def translate_instruction(instruction):
    """Переводит инструкцию с английского на русский"""
    text = instruction

    # Полные переводы фраз
    replacements = [
        # Basic movements
        ("Lie down on the floor", "Лягте на пол"),
        ("Lie on your back", "Лягте на спину"),
        ("Lie on your stomach", "Лягте на живот"),
        ("Lie on your side", "Лягте на бок"),
        ("Stand with your feet shoulder-width apart", "Встаньте, ноги на ширине плеч"),
        ("Stand with your feet hip-width apart", "Встаньте, ноги на ширине бедер"),
        ("Stand straight", "Встаньте прямо"),
        ("Stand upright", "Встаньте прямо"),
        ("Keep your back straight", "Держите спину прямо"),
        ("Keep your core engaged", "Напрягите мышцы пресса"),
        ("Keep your feet flat on the floor", "Держите стопы плотно прижатыми к полу"),
        ("Place your feet flat on the floor", "Поставьте стопы плотно на пол"),
        ("Secure your feet", "Закрепите ноги"),
        ("Bend your knees", "Согните колени"),
        ("Bend your legs at the knees", "Согните ноги в коленях"),
        ("Extend your legs", "Выпрямите ноги"),
        ("Straighten your legs", "Выпрямите ноги"),
        ("Extend your arms", "Выпрямите руки"),
        ("Straighten your arms", "Выпрямите руки"),
        ("Bend your arms", "Согните руки"),
        ("Bend at the waist", "Наклонитесь в талии"),
        ("Bend at the hips", "Наклонитесь в бедрах"),
        ("Return to starting position", "Вернитесь в исходное положение"),
        ("Return to the starting position", "Вернитесь в исходное положение"),
        ("Reverse the motion", "Выполните движение в обратном порядке"),
        ("This will be your starting position", "Это будет исходное положение"),
        ("This will be your starting position.", "Это будет исходное положение."),

        # Reps and sets
        ("Repeat for the recommended amount of repetitions", "Повторите рекомендуемое количество раз"),
        ("Repeat for", "Повторите"),
        ("repetitions", "повторов"),
        ("reps", "раз"),
        ("sets", "подходов"),
        ("Hold for", "Удерживайте"),
        ("seconds", "секунд"),
        ("Pause at the top", "Задержитесь в верхней точке"),
        ("Pause briefly at the top", "Задержитесь ненадолго в верхней точке"),
        ("Pause briefly", "Задержитесь ненадолго"),

        # Movement directions
        ("Slowly lower", "Медленно опустите"),
        ("Slowly raise", "Медленно поднимите"),
        ("Slowly lift", "Медленно поднимите"),
        ("Slowly lower your body", "Медленно опустите тело"),
        ("Slowly raise your body", "Медленно поднимите тело"),

        # Breathing
        ("Exhale", "Выдохните"),
        ("Inhale", "Вдохните"),
        ("Breathe out", "Выдыхайте"),
        ("Breathe in", "Вдыхайте"),

        # Equipment
        ("barbell", "штанга"),
        ("dumbbells", "гантели"),
        ("dumbbell", "гантели"),
        ("cable", "трос"),
        ("machine", "тренажер"),
        ("bench", "скамья"),
        ("mat", "коврик"),
        ("ball", "мяч"),
        ("band", "эспандер"),
        ("rope", "канат"),
        ("bar", "гриф"),

        # Body parts
        ("Your feet should be bent at the knees", "Ваши ноги должны быть согнуты в коленях"),
        ("Place your hands behind or to the side of your head", "Положите руки за голову или по бокам от головы"),
        ("Place your hands behind your head", "Положите руки за голову"),
        ("You will begin with your back on the ground", "Начните со спиной на полу"),
        ("Flex your hips and spine to raise your torso toward your knees", "Согните бедра и позвоночник, чтобы поднять торс к коленям"),
        ("At the top of the contraction your torso should be perpendicular to the ground", "В верхней точке сокращения ваш торс должен быть перпендикулярен полу"),
        ("going only ¾ of the way down", "опускаясь только на ¾ пути вниз"),
        ("Lie on your back, with one leg extended straight out", "Лягте на спину, выпрямите одну ногу"),
        ("With the other leg, bend the hip and knee to 90 degrees", "Другую ногу согните в бедре и колене под углом 90 градусов"),
        ("You may brace your leg with your hands if necessary", "При необходимости вы можете поддерживать ногу руками"),
        ("Extend your leg straight into the air", "Выпрямите ногу вертикально вверх"),
        ("Return the leg to the starting position", "Верните ногу в исходное положение"),
        ("and then switch to the other leg", "затем смените ногу"),

        # Machine instructions
        ("Select a light resistance and sit down on the ab machine", "Выберите небольшое сопротивление и сядьте на тренажер для пресса"),
        ("placing your feet under the pads provided", "поместив ноги под подушечки"),
        ("and grabbing the top handles", "и взявшись за верхние рукоятки"),
        ("Your arms should be bent at a 90 degree angle", "Ваши руки должны быть согнуты под углом 90 градусов"),
        ("as you rest the triceps on the pads provided", "когда вы упираетесь трицепсами в подушечки"),
        ("At the same time, begin to lift the legs up", "В то же время начните поднимать ноги"),
        ("as you crunch your upper torso", "скручивая верхнюю часть торса"),
        ("Tip: Be sure to use a slow and controlled motion", "Совет: используйте медленное и контролируемое движение"),
        ("Concentrate on using your abs to move the weight", "Сосредоточьтесь на использовании пресса для перемещения веса"),
        ("while relaxing your legs and feet", "расслабляя при этом ноги и стопы"),
        ("After a second pause", "После паузы в секунду"),
        ("slowly return to the starting position as you breathe in", "медленно вернитесь в исходное положение, вдыхая"),
        ("Repeat the movement for the prescribed amount of reps", "Повторите движение предписанное количество раз"),

        # Ab roller
        ("Hold the Ab Roller with both hands", "Возьмите гимнастический ролик обеими руками"),
        ("and kneel on the floor", "и станьте на колени на полу"),
        ("Now place the ab roller on the floor in front of you", "Теперь поместите гимнастический ролик на пол перед собой"),
        ("so that you are on all your hands and knees", "чтобы вы стояли на руках и коленях"),
        ("as in a kneeling push up position", "как в положении для отжиманий с колен"),
        ("Slowly roll the ab roller straight forward", "Медленно катите гимнастический ролик прямо вперед"),
        ("stretching your body into a straight position", "вытягивая тело в прямую линию"),
        ("Tip: Go down as far as you can", "Совет: опускайтесь настолько низко, насколько можете"),
        ("without touching the floor with your body", "не касаясь полом телом"),
        ("Breathe in during this portion of the movement", "Вдыхайте во время этой части движения"),
        ("After a pause at the stretched position", "После паузы в растянутом положении"),
        ("start pulling yourself back to the starting position", "начните тянуть себя обратно в исходное положение"),
        ("as you breathe out", "выдыхая"),
        ("Tip: Go slowly and keep your abs tight at all times", "Совет: двигайтесь медленно и всегда держите пресс напряженным"),

        # Common phrases
        ("the top of the contraction", "в верхней точке сокращения"),
        ("perpendicular to the ground", "перпендикулярно полу"),
        ("perpendicular to the floor", "перпендикулярно полу"),
        ("straight out", "прямо"),
        ("straight into the air", "прямо вверх"),
        ("if necessary", "при необходимости"),
        ("at chest level", "на уровне груди"),
        ("at shoulder level", "на уровне плеч"),
        ("at waist level", "на уровне талии"),
        ("in front of you", "перед собой"),
        ("behind you", "за спиной"),
        ("to your sides", "в стороны"),
        ("by your sides", "по бокам"),
        ("at your sides", "по бокам"),
        ("overhead", "над головой"),
        ("across your body", "через тело"),
        ("across your chest", "через грудь"),

        # Movement verbs
        ("grasp", "возьмите"),
        ("grab", "возьмите"),
        ("hold", "удерживайте"),
        ("squeeze", "сожмите"),
        ("contract", "сократите"),
        ("relax", "расслабьте"),
        ("maintain", "сохраняйте"),
        ("engage", "напрягите"),
        ("keep", "держите"),
        ("place", "поместите"),
        ("put", "положите"),
        ("set", "установите"),
        ("position", "разместите"),

        # Direction verbs
        ("raise", "поднимите"),
        ("lift", "поднимите"),
        ("lower", "опустите"),
        ("drop", "опустите"),
        ("pull", "тяните"),
        ("push", "толкайте"),
        ("press", "нажимайте"),
        ("rotate", "вращайте"),
        ("twist", "скручивайте"),
        ("bend", "согните"),
        ("straighten", "выпрямите"),
        ("extend", "выпрямите"),
        ("flex", "согните"),
        ("lean", "наклонитесь"),
        ("lean forward", "наклонитесь вперед"),
        ("lean backward", "наклонитесь назад"),
        ("step", "шагните"),
        ("lunge", "сделайте выпад"),
        ("squat", "приседайте"),
        ("jump", "прыгайте"),
        ("curl", "сгибайте"),

        # Body terms
        ("your hands", "ваши руки"),
        ("your arms", "ваши руки"),
        ("your legs", "ваши ноги"),
        ("your feet", "ваши стопы"),
        ("your back", "ваша спина"),
        ("your chest", "ваша грудь"),
        ("your shoulders", "ваши плечи"),
        ("your head", "ваша голова"),
        ("your torso", "ваш торс"),
        ("your hips", "ваши бедра"),
        ("your knees", "ваши колени"),
        ("your abs", "ваш пресс"),
        ("your core", "ваш корпус"),
        ("your body", "ваше тело"),
        ("the floor", "пол"),
        ("the ground", "пол"),
        ("the machine", "тренажер"),
        ("the bench", "скамья"),

        # Numbers and measurements
        ("90 degree", "90 градусов"),
        ("45 degree", "45 градусов"),
        ("¾", "3/4"),
        ("1/2", "1/2"),
        ("one", "одна/один"),
        ("two", "две/два"),
        ("three", "три"),
        ("four", "четыре"),
        ("five", "пять"),
        ("ten", "десять"),
        ("twenty", "двадцать"),
        ("thirty", "тридцать"),
        ("forty", "сорок"),
        ("fifty", "пятьдесят"),

        # Connectors
        ("and", "и"),
        ("or", "или"),
        ("as you", "когда вы"),
        ("while you", "пока вы"),
        ("until", "пока"),
        ("before", "до"),
        ("after", "после"),
        ("during", "во время"),
        ("then", "затем"),
        ("next", "далее"),
        ("finally", "наконец"),

        # Additional terms
        ("resistance", "сопротивление"),
        ("weight", "вес"),
        ("pad", "подушечка"),
        ("handle", "рукоятка"),
        ("grip", "хват"),
        ("position", "положение"),
        ("movement", "движение"),
        ("exercise", "упражнение"),
        ("form", "техника"),
        ("technique", "техника"),
        ("motion", "движение"),
        ("contraction", "сокращение"),
        ("pause", "пауза"),
        ("stretch", "растяжение"),
        ("stretched", "растянутый"),
    ]

    # Применяем замены (от более длинных фраз к более коротким)
    for eng, rus in replacements:
        # Заменяем с учетом регистра, но сохраняя регистр результата
        text = re.sub(re.escape(eng), rus, text, flags=re.IGNORECASE)

    # Дополнительная обработка - замена оставшихся английских слов
    words_to_replace = {
        "hands": "руки",
        "feet": "ноги",
        "legs": "ноги",
        "arms": "руки",
        "back": "спина",
        "head": "голова",
        "torso": "торс",
        "body": "тело",
        "floor": "пол",
        "ground": "пол",
        "knees": "колени",
        "hips": "бедра",
        "abs": "пресс",
        "core": "корпус",
        "air": "воздух",
        "weight": "вес",
        "machine": "тренажер",
        "pads": "подушечки",
        "handles": "рукоятки",
        "angle": "угол",
        "degrees": "градусов",
        "seconds": "секунд",
        "reps": "раз",
        "movement": "движение",
        "position": "положение",
        "breath": "дыхание",
    }

    for word, translation in words_to_replace.items():
        text = re.sub(r'\b' + re.escape(word) + r's?\b', translation, text, flags=re.IGNORECASE)

    return text

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
            translated_instructions = [translate_instruction(inst) for inst in exercise['instructions']]
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
