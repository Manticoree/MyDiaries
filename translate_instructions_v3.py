#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Скрипт для перевода инструкций упражнений на русский язык v3
С полными переводами целых фраз
"""

import json
import re

# Словарь полных переводов фраз
FULL_TRANSLATIONS = {
    # Exercise 1: 3/4 Sit-Up
    "Lie down on the floor and secure your feet. Your legs should be bent at the knees.": "Лягте на пол и закрепите ноги. Ваши ноги должны быть согнуты в коленях.",
    "Place your hands behind or to the side of your head. You will begin with your back on the ground. This will be your starting position.": "Положите руки за голову или по бокам от головы. Начните со спиной на полу. Это будет исходное положение.",
    "Flex your hips and spine to raise your torso toward your knees.": "Согните бедра и позвоночник, чтобы поднять торс к коленям.",
    "At the top of the contraction your torso should be perpendicular to the ground. Reverse the motion, going only ¾ of the way down.": "В верхней точке сокращения ваш торс должен быть перпендикулярен полу. Выполните движение в обратном порядке, опускаясь только на ¾ пути вниз.",
    "Repeat for the recommended amount of repetitions.": "Повторите рекомендуемое количество раз.",

    # Exercise 2: 90/90 Hamstring Stretch
    "Lie on your back, with one leg extended straight out.": "Лягте на спину, выпрямите одну ногу.",
    "With the other leg, bend the hip and knee to 90 degrees. You may brace your leg with your hands if necessary. This will be your starting position.": "Другую ногу согните в бедре и колене под углом 90 градусов. При необходимости вы можете поддерживать ногу руками. Это будет исходное положение.",
    "Extend your leg straight into the air, pausing briefly at the top. Return the leg to the starting position.": "Выпрямите ногу вертикально вверх, задержитесь ненадолго в верхней точке. Верните ногу в исходное положение.",
    "Repeat for 10-20 repetitions, and then switch to the other leg.": "Повторите 10-20 раз, затем смените ногу.",

    # Exercise 3: Ab Crunch Machine
    "Select a light resistance and sit down on the ab machine placing your feet under the pads provided and grabbing the top handles. Your arms should be bent at a 90 degree angle as you rest the triceps on the pads provided. This will be your starting position.": "Выберите небольшое сопротивление и сядьте на тренажер для пресса, поместив ноги под подушечки и взявшись за верхние рукоятки. Ваши руки должны быть согнуты под углом 90 градусов, когда вы опираетесь трицепсами на подушечки. Это будет исходное положение.",
    "At the same time, begin to lift the legs up as you crunch your upper torso. Breathe out as you perform this movement. Tip: Be sure to use a slow and controlled motion. Concentrate on using your abs to move the weight while relaxing your legs and feet.": "В то же время начните поднимать ноги, скручивая верхнюю часть торса. Выдыхайте при выполнении этого движения. Совет: используйте медленное и контролируемое движение. Сосредоточьтесь на использовании пресса для перемещения веса, расслабляя ноги и стопы.",
    "After a second pause, slowly return to the starting position as you breathe in.": "После паузы в секунду медленно вернитесь в исходное положение, вдыхая.",
    "Repeat the movement for the prescribed amount of reps.": "Повторите движение предписанное количество раз.",

    # Exercise 4: Ab Roller
    "Hold the Ab Roller with both hands and kneel on the floor.": "Возьмите гимнастический ролик обеими руками и станьте на колени на полу.",
    "Now place the ab roller on the floor in front of you so that you are on all your hands and knees (as in a kneeling push up position). This will be your starting position.": "Теперь поместите гимнастический ролик на пол перед собой, чтобы вы стояли на руках и коленях (как в положении для отжиманий с колен). Это будет исходное положение.",
    "Slowly roll the ab roller straight forward, stretching your body into a straight position. Tip: Go down as far as you can without touching the floor with your body. Breathe in during this portion of the movement.": "Медленно катите гимнастический ролик прямо вперед, вытягивая тело в прямую линию. Совет: опускайтесь настолько низко, насколько можете, не касаясь полом телом. Вдыхайте во время этой части движения.",
    "After a pause at the stretched position, start pulling yourself back to the starting position as you breathe out. Tip: Go slowly and keep your abs tight at all times.": "После паузы в растянутом положении начните тянуть себя обратно в исходное положение, выдыхая. Совет: двигайтесь медленно и всегда держите пресс напряженным.",

    # Common phrases for other exercises
    "Stand with your feet shoulder-width apart.": "Встаньте, ноги на ширине плеч.",
    "Stand with your feet hip-width apart.": "Встаньте, ноги на ширине бедер.",
    "Keep your back straight throughout the movement.": "Держите спину прямо на протяжении всего движения.",
    "Keep your core tight.": "Держите пресс напряженным.",
    "Engage your core muscles.": "Напрягите мышцы пресса.",
    "Slowly lower the weight back to the starting position.": "Медленно опустите вес обратно в исходное положение.",
    "Pause for a moment at the top of the movement.": "Задержитесь на мгновение в верхней точке движения.",
    "Exhale as you push the weight up.": "Выдыхайте, когда толкаете вес вверх.",
    "Inhale as you lower the weight.": "Вдыхайте, когда опускаете вес.",
    "Do not arch your back.": "Не прогибайте спину.",
    "Do not round your back.": "Не округляйте спину.",
    "Keep your head neutral and looking forward.": "Держите голову в нейтральном положении, смотрите вперед.",
    "Keep a slight bend in your elbows.": "Держите небольшое сгибание в локтях.",
    "Keep your elbows close to your body.": "Держите локти близко к телу.",
    "Squeeze your shoulder blades together at the top.": "Сведите лопатки вместе в верхней точке.",
    "Control the weight on the way down.": "Контролируйте вес при опускании.",
    "Use a full range of motion.": "Используйте полную амплитуду движения.",
    "Do not use momentum.": "Не используйте инерцию.",
    "Focus on the muscle contraction.": "Сосредоточьтесь на сокращении мышц.",
}

def translate_instruction(instruction):
    """Переводит инструкцию с английского на русский"""
    # Сначала проверяем точное совпадение
    if instruction in FULL_TRANSLATIONS:
        return FULL_TRANSLATIONS[instruction]

    # Если нет точного совпадения, пытаемся найти похожую фразу
    text = instruction

    # Словосочетания для замены
    replacements = [
        # Базовые фразы
        ("Lie down on the floor", "Лягте на пол"),
        ("Lie on your back", "Лягте на спину"),
        ("Lie on your stomach", "Лягте на живот"),
        ("Stand with your feet shoulder-width apart", "Встаньте, ноги на ширине плеч"),
        ("Stand straight", "Встаньте прямо"),
        ("Keep your back straight", "Держите спину прямо"),
        ("Keep your core engaged", "Напрягите пресс"),
        ("Return to starting position", "Вернитесь в исходное положение"),
        ("This will be your starting position", "Это будет исходное положение"),
        ("Repeat for", "Повторите"),
        ("Hold for", "Удерживайте"),
        ("Pause at the top", "Задержитесь в верхней точке"),
        ("Slowly lower", "Медленно опустите"),
        ("Slowly raise", "Медленно поднимите"),
        ("Exhale as you", "Выдыхайте, когда"),
        ("Inhale as you", "Вдыхайте, когда"),

        # Части тела
        ("your hands", "ваши руки"),
        ("your arms", "ваши руки"),
        ("your legs", "ваши ноги"),
        ("your feet", "ваши стопы"),
        ("your back", "ваша спина"),
        ("your head", "ваша голова"),
        ("your torso", "ваш торс"),
        ("your hips", "ваши бедра"),
        ("your knees", "ваши колени"),
        ("your abs", "ваш пресс"),
        ("your body", "ваше тело"),
        ("your shoulders", "ваши плечи"),
        ("your chest", "ваша грудь"),
        ("the floor", "пол"),
        ("the ground", "пол"),

        # Глаголы
        ("place", "поместите"),
        ("secure", "закрепите"),
        ("bend", "согните"),
        ("extend", "выпрямите"),
        ("raise", "поднимите"),
        ("lift", "поднимите"),
        ("lower", "опустите"),
        ("pull", "тяните"),
        ("push", "толкайте"),
        ("squeeze", "сожмите"),
        ("contract", "сократите"),
        ("relax", "расслабьте"),
        ("keep", "держите"),
        ("maintain", "сохраняйте"),
        ("engage", "напрягите"),
        ("grab", "возьмите"),
        ("hold", "удерживайте"),
        ("perform", "выполняйте"),

        # Направления
        ("forward", "вперед"),
        ("backward", "назад"),
        ("upward", "вверх"),
        ("downward", "вниз"),
        ("straight", "прямо"),
        ("straight out", "прямо"),

        # Прочее
        ("and", "и"),
        ("or", "или"),
        ("with", "с"),
        ("without", "без"),
        ("at the top", "в верхней точке"),
        ("at the bottom", "в нижней точке"),
        ("in front of you", "перед собой"),
        ("behind you", "за спиной"),
        ("to your sides", "по бокам"),
        ("overhead", "над головой"),
        ("perpendicular to", "перпендикулярно"),
        ("parallel to", "параллельно"),
        ("degrees", "градусов"),
        ("repetitions", "раз"),
        ("reps", "раз"),
        ("seconds", "секунд"),
        ("sets", "подходов"),
        ("weight", "вес"),
        ("movement", "движение"),
        ("position", "положение"),
        ("exercise", "упражнение"),
    ]

    # Применяем замены
    for eng, rus in replacements:
        text = re.sub(r'\b' + re.escape(eng) + r's?\b', rus, text, flags=re.IGNORECASE)

    # Очистка от оставшихся английских слов
    text = re.sub(r'\b(the|a|an|to|of|in|on|at|by|for|with|as|be|is|are|was|were|been|being|have|has|had|do|does|did|will|would|could|should|may|might|must|can|need|go|going|goes|went|come|comes|came|get|gets|got|make|makes|made|take|takes|took|taken|see|sees|saw|seen)\b', '', text, flags=re.IGNORECASE)
    text = re.sub(r'\s+', ' ', text).strip()
    text = text.capitalize()

    return text

def main():
    # Читаем JSON файл
    print("Чтение файла exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    # Переводим инструкции
    translated_count = 0
    exact_matches = 0
    for i, exercise in enumerate(exercises):
        if 'instructions' in exercise and exercise['instructions']:
            original_instructions = exercise['instructions']
            translated_instructions = []
            for inst in original_instructions:
                translated = translate_instruction(inst)
                if translated != inst:
                    translated_count += 1
                if inst in FULL_TRANSLATIONS:
                    exact_matches += 1
                translated_instructions.append(translated)
            exercise['instructions'] = translated_instructions

        if (i + 1) % 100 == 0:
            print(f"Обработано {i + 1} упражнений...")

    print(f"Переведено инструкций: {translated_count}")
    print(f"Точных совпадений: {exact_matches}")

    # Сохраняем результат
    print("Сохранение переведенного файла...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print("Готово!")

if __name__ == '__main__':
    main()
