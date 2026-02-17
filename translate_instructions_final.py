import json
import re

# Read the JSON file
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Extended dictionary for exercise translations
translations = {
    # Verbs
    "Stand": "Встаньте",
    "Sti": "Встаньте",
    "sting": "стоя",
    "stи": "встаньте",
    "Lie": "Лягте",
    "Lay": "Лягте",
    "laying": "лежа",
    "Sit": "Сядьте",
    "sitting": "сидя",
    "Place": "Поместите",
    "Position": "Положите",
    "Hold": "Удерживайте",
    "holding": "удерживая",
    "Keep": "Держите",
    "keeping": "держа",
    "Maintain": "Поддерживайте",
    "Bend": "Согните",
    "bending": "сгибая",
    "Extend": "Выпрямите",
    "extending": "выпрямляя",
    "Lift": "Поднимите",
    "lifting": "поднимая",
    "Lower": "Опустите",
    "lowering": "опуская",
    "Raise": "Поднимите",
    "raising": "поднимая",
    "Pull": "Тяните",
    "pulling": "тя",
    "Push": "Толкайте",
    "press": "жмите",
    "pressing": "жмите",
    "Squeeze": "Сжимайте",
    "squeezing": "сжимая",
    "Contract": "Сокращайте",
    "contracting": "сокращая",
    "Relax": "Расслабьте",
    "relaxing": "расслабляя",
    "Return": "Вернитесь",
    "returning": "возвращаясь",
    "Repeat": "Повторите",
    "Pause": "Пауза",
    "pausing": "пауза",
    "Breath": "Дышите",
    "breathing": "дыша",
    "Inhale": "Вдыхайте",
    "inhaling": "вдыхая",
    "Exhale": "Выдыхайте",
    "exhaling": "выдыхая",
    "Start": "Начните",
    "starting": "начиная",
    "Begin": "Начинайте",
    "beginning": "начиная",
    "End": "Конец",
    "Pick": "Возьмите",
    "picking": "беря",
    "Clean": "Подтяните",
    "cleaning": "подтягивая",
    "Hang": "Вис",
    "hanging": "вися",
    "Drag": "Тяните",
    "driving": "толкайте",
    "throw": "бросайте",
    "toss": "бросьте",
    "roll": "катите",
    "rolling": "катая",
    "step": "шаг",
    "stepping": "шагая",
    "walk": "ходите",
    "jump": "прыгайте",
    "jumping": "прыгая",
    "bounce": "отталкивайтесь",
    "lean": "наклонитесь",
    "leaning": "наклоняясь",
    "lean back": "отклонитесь назад",
    "lean forward": "подайтесь вперед",
    "look": "смотрите",
    "looking": "смотря",
    "twist": "скручивайте",
    "twisting": "скручивая",
    "rotate": "поворачивайте",
    "rotating": "поворачивая",
    "turn": "поворачивайте",
    "turning": "поворачивая",
    "swing": "раскачивайте",
    "swinging": "раскачивая",
    "flex": "сгибайте",
    "flexing": "сгибая",
    "perтехника": "выполняйте",
    "perтехникаing": "выполняя",
    "wилиk": "работайте",
    "wилиking": "работая",
    "hи": "рука",
    "hиs": "руки",
    "tилиso": "торс",
    "бедро": "бедро",
    "ступня": "ступня",
    "ступни": "ступни",
    "колено": "колено",
    "локоть": "локоть",
    "локти": "локти",
    
    # Body parts
    "Feet": "Ступни",
    "Foot": "Ступня",
    "Hands": "Руки",
    "Hand": "Рука",
    "Arms": "Руки",
    "Arm": "Рука",
    "Legs": "Ноги",
    "Leg": "Нога",
    "Knees": "Колени",
    "Knee": "Колено",
    "Hips": "Бедра",
    "Hip": "Бедро",
    "Chest": "Грудь",
    "Back": "Спина",
    "Shoulders": "Плечи",
    "Shoulder": "Плечо",
    "Elbow": "Локоть",
    "Elbows": "Локти",
    "Wrist": "Запястье",
    "Wrists": "Запястья",
    "Head": "Голова",
    "Neck": "Шея",
    "Torso": "Торс",
    "Core": "Кор",
    "Biceps": "Бицепс",
    "Triceps": "Трицепс",
    "Lats": "Широчайшие",
    "Delts": "Дельты",
    "Traps": "Трапеция",
    "Quads": "Квадрицепс",
    "Hamstrings": "Бицепс бедра",
    "Glutes": "Ягодицы",
    "Calves": "Икры",
    "Forearms": "Предплечья",
    "Abs": "Пресс",
    "Abdominals": "Пресс",
    "Heel": "Пятка",
    "Heels": "Пятки",
    "Toe": "Палец",
    "Toes": "Пальцы",
    "Ankle": "Лодыжка",
    "Ankles": "Лодыжки",
    
    # Equipment
    "Floor": "Пол",
    "Ground": "Пол",
    "Weight": "Вес",
    "Weights": "Веса",
    "Bar": "Гриф",
    "Barbell": "Штанга",
    "Dumbbell": "Гантель",
    "Dumbbells": "Гантели",
    "Kettlebell": "Гиря",
    "Plate": "Блин",
    "Body": "Тело",
    "Bodyweight": "Собственный вес",
    "Bench": "Скамья",
    "Rack": "Стойка",
    "Machine": "Тренажер",
    "Mat": "Коврик",
    "Ball": "Мяч",
    "Band": "Эспандер",
    "Cable": "Трос",
    "Rope": "Канат",
    "Chain": "Цепь",
    "Box": "Ящик",
    "Platform": "Платформа",
    "Squat rack": "Стойка для приседаний",
    "Incline bench": "Наклонная скамья",
    
    # Directional words
    "Forward": "Вперед",
    "Backward": "Назад",
    "Up": "Вверх",
    "Down": "Вниз",
    "Left": "Левый",
    "Right": "Правый",
    "Straight": "Прямой",
    "Bent": "Согнутый",
    "Horizontal": "Горизонтальный",
    "Vertical": "Вертикальный",
    "Parallel": "Параллельно",
    "Perpendicular": "Перпендикулярно",
    "Across": "через",
    "Through": "через",
    "Around": "вокруг",
    
    # Common phrases
    "Starting position": "Исходное положение",
    "initial position": "исходное положение",
    "original position": "исходное положение",
    "beginning position": "начальное положение",
    "This will be the starting position": "Это будет исходное положение",
    "This is your starting position": "Это ваше исходное положение",
    
    # Muscle actions
    "Contract": "Сокращайте",
    "Squeeze": "Сжимайте",
    "Keep tension": "Держите напряжение",
    "Maintain tension": "Поддерживайте напряжение",
    
    # Time-related
    "Second": "Секунда",
    "Seconds": "Секунды",
    "Pause for a second": "Задержитесь на секунду",
    "After a second pause": "После секундной паузы",
    "After a brief pause": "После короткой паузы",
    "After pausing": "После паузы",
    "Hold for": "Удерживайте в течение",
    
    # Numbers
    "One": "Один",
    "Two": "Два",
    "Three": "Три",
    "Four": "Четыре",
    "Five": "Пять",
    "Six": "Шесть",
    "Seven": "Семь",
    "Eight": "Восемь",
    "Nine": "Девять",
    "Ten": "Десять",
    
    # Common exercise terms
    "Rep": "Повторение",
    "Reps": "Повторения",
    "Set": "Подход",
    "Sets": "Подходы",
    "Exercise": "Упражнение",
    "Movement": "Движение",
    "Range of motion": "Амплитуда движения",
    "Full range of motion": "Полная амплитуда движения",
    
    # Grip-related
    "Grip": "Хват",
    "Grasp": "Захват",
    "Grab": "Возьмите",
    "Overhand grip": "Пронированный хват",
    "Underhand grip": "Супинированный хват",
    "Mixed grip": "Смешанный хват",
    "Shoulder-width grip": "Хват на ширине плеч",
    "Wide grip": "Широкий хват",
    "Narrow grip": "Узкий хват",
    
    # Position-related
    "Position": "Положение",
    "Angle": "Угол",
    "Side": "Сторона",
    "Sides": "Стороны",
    "Front": "Перед",
    "Rear": "Зад",
    
    # Quality-related
    "Slowly": "Медленно",
    "Quickly": "Быстро",
    "Controlled": "Контролируемый",
    "Stable": "Стабильный",
    "Steady": "Устойчивый",
    
    # Measurement
    "Inch": "Дюйм",
    "Inches": "Дюймы",
    "Degree": "Градус",
    "Degrees": "Градусы",
    "Level": "Уровень",
    "Height": "Высота",
    "Width": "Ширина",
    
    # Prepositions and connectors
    "Towards": "К",
    "Away": "От",
    "With": "С",
    "Without": "Без",
    "And": "и",
    "Or": "или",
    "But": "но",
    "As": "когда",
    "Then": "затем",
    "Now": "теперь",
    "Next": "далее",
    "Also": "также",
    "Too": "тоже",
    
    # Adjectives
    "Heavy": "Тяжелый",
    "Light": "Легкий",
    "Strong": "Сильный",
    "Weak": "Слабый",
    "High": "Высокий",
    "Low": "Низкий",
    "Medium": "Средний",
    "Wide": "Широкий",
    "Narrow": "Узкий",
    "Short": "Короткий",
    "Long": "Длинный",
    "Slightly": "Слегка",
    "Fully": "Полностью",
    "Completely": "Полностью",
    
    # Instructions
    "Make sure": "Убедитесь",
    "Be careful": "Будьте осторожны",
    "Use": "Используйте",
    "Using": "Используя",
    "Focus": "Сосредоточьтесь",
    "Concentrate": "Сосредоточьтесь",
    "Ensure": "Убедитесь",
    "Keep": "Держите",
    "Maintain": "Поддерживайте",
    "Try to": "Попытайтесь",
    "Attempt to": "Попытайтесь",
    
    # Common phrases
    "Tip:": "Совет:",
    "Note:": "Примечание:",
    "Advice:": "Совет:",
    "Once": "Как только",
    "When": "Когда",
    "While": "Пока",
    
    # Action verbs
    "Drive": "Толкайте",
    "Drive through": "Толкайте через",
    "Push": "Толкайте",
    "Push through": "Толкайте через",
    "Step": "Шагните",
    "Step onto": "Встаньте на",
    "Step off": "Сойдите с",
    "Stand up": "Встаньте",
    "Sit down": "Сядьте",
    "Lay down": "Лягте",
    
    # Cleaning up garbled text
    "fилиward": "вперед",
    "ваше": "ваше",
    "fили": "для",
    "тросs": "тросы",
    "хват": "хват",
    "хватping": "хватаясь",
    "ваше": "ваше",
    "Ваше": "Ваше",
    "Ваш": "Ваш",
    "вы": "вы",
    "Вы": "Вы",
    "the": "",
    "to": "",
    "of": "",
    "in": "",
    "on": "",
    "at": "",
    "by": "",
    "from": "",
    "with": "с",
    "for": "для",
    "a": "",
    "an": "",
    "your": "ваш",
    "Your": "Ваш",
    "you": "вы",
    "You": "Вы",
}

def clean_and_translate(text):
    # Remove extra spaces
    text = re.sub(r'\s+', ' ', text).strip()
    
    # Replace common phrases first (longer ones first)
    for en, ru in sorted(translations.items(), key=lambda x: len(x[0]), reverse=True):
        pattern = re.compile(r'\b' + re.escape(en) + r'\b', re.IGNORECASE)
        text = pattern.sub(ru, text)
    
    # Clean up extra spaces again
    text = re.sub(r'\s+', ' ', text).strip()
    
    return text

# Process all exercises
modified_count = 0

for exercise in exercises:
    if 'instructions' in exercise:
        instructions = exercise['instructions']
        modified = False
        for i, instruction in enumerate(instructions):
            # Check if instruction has English characters
            if any(ord(c) < 128 and c.isalpha() for c in instruction):
                translated = clean_and_translate(instruction)
                if translated != instruction:
                    instructions[i] = translated
                    modified = True
        if modified:
            modified_count += 1

print(f"Modified exercises: {modified_count}")

# Save to new file
with open('exercises_final.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("Final translation complete! Saved to exercises_final.json")
