import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Comprehensive translation dictionary
translations = {
    # Verbs
    "Stand": "Встаньте", "Sit": "Сядьте", "Lie": "Лягте", "Place": "Поместите",
    "Hold": "Удерживайте", "Keep": "Держите", "Keep": "Держите",
    "Bend": "Согните", "Extend": "Выпрямите", "Lift": "Поднимите",
    "Lower": "Опустите", "Raise": "Поднимите", "Pull": "Тяните",
    "Push": "Толкайте", "Press": "Жмите", "Squeeze": "Сжимайте",
    "Contract": "Сокращайте", "Relax": "Расслабьте", "Return": "Вернитесь",
    "Repeat": "Повторите", "Start": "Начните", "Begin": "Начинайте",
    "Move": "Переместите", "Rotate": "Поверните", "Turn": "Поверните",
    "Twist": "Скручивайте", "Lean": "Наклонитесь", "Look": "Смотрите",
    "Step": "Шагните", "Jump": "Прыгайте", "Walk": "Ходите",
    "Clean": "Подтяните", "Drag": "Тяните", "Drive": "Толкайте",
    "Roll": "Катите", "Swing": "Раскачивайте", "Grab": "Возьмите",
    "Grasp": "Захватите", "Pick": "Возьмите", "Set": "Установите",
    "Load": "Загрузите", "Rack": "Стойка", "Rack": "Положите",
    
    # Nouns - Body
    "Feet": "Ступни", "Foot": "Ступня", "Feet": "Ступни",
    "Hands": "Руки", "Hand": "Рука", "Arms": "Руки", "Arm": "Рука",
    "Legs": "Ноги", "Leg": "Нога", "Knees": "Колени", "Knee": "Колено",
    "Hips": "Бедра", "Hip": "Бедро", "Chest": "Грудь",
    "Back": "Спина", "Shoulders": "Плечи", "Shoulder": "Плечо",
    "Elbows": "Локти", "Elbow": "Локоть", "Wrists": "Запястья",
    "Wrist": "Запястье", "Head": "Голова", "Neck": "Шея",
    "Torso": "Торс", "Body": "Тело", "Core": "Кор",
    
    # Muscles
    "Abs": "Пресс", "Abdominals": "Пресс", "Biceps": "Бицепс",
    "Triceps": "Трицепс", "Lats": "Широчайшие", "Delts": "Дельты",
    "Traps": "Трапеция", "Quads": "Квадрицепс",
    "Hamstrings": "Бицепс бедра", "Glutes": "Ягодицы",
    "Calves": "Икры", "Forearms": "Предплечья",
    "Heel": "Пятка", "Heels": "Пятки", "Toe": "Палец",
    "Toes": "Пальцы", "Ankle": "Лодыжка", "Ankles": "Лодыжки",
    
    # Equipment
    "Floor": "Пол", "Ground": "Пол", "Barbell": "Штанга",
    "Dumbbells": "Гантели", "Dumbbell": "Гантель", "Kettlebell": "Гиря",
    "Bar": "Гриф", "Bench": "Скамья", "Machine": "Тренажер",
    "Cable": "Трос", "Cables": "Тросы", "Rope": "Канат",
    "Band": "Эспандер", "Ball": "Мяч", "Plate": "Блин",
    "Box": "Ящик", "Platform": "Платформа", "Mat": "Коврик",
    "Pad": "Подушка", "Block": "Блок", "Step": "Шаг",
    "Squat rack": "Стойка для приседаний", "Incline bench": "Наклонная скамья",
    "Flat bench": "Плоская скамья", "Weight": "Вес",
    
    # Directions
    "Forward": "Вперед", "Backward": "Назад", "Up": "Вверх",
    "Down": "Вниз", "Left": "Левый", "Right": "Правый",
    "Straight": "Прямой", "Bent": "Согнутый", "Horizontal": "Горизонтальный",
    "Vertical": "Вертикальный", "Parallel": "Параллельно",
    "Perpendicular": "Перпендикулярно", "Side": "Сторона",
    "Sides": "Стороны", "Front": "Перед", "Rear": "Зад",
    "Overhead": "Над головой", "Behind": "Позади", "Next to": "Рядом с",
    
    # Qualifiers
    "Slowly": "Медленно", "Quickly": "Быстро", "Slightly": "Слегка",
    "Fully": "Полностью", "Completely": "Полностью", "Carefully": "Осторожно",
    "Properly": "Правильно", "Controlled": "Контролируемо",
    "Stable": "Стабильно", "Steady": "Устойчиво",
    "Straight": "Прямо", "Directly": "Прямо",
    
    # Numbers
    "One": "Один", "Two": "Два", "Three": "Три",
    "Four": "Четыре", "Five": "Пять", "Six": "Шесть",
    "Seven": "Семь", "Eight": "Восемь", "Nine": "Девять",
    "Ten": "Десять", "Twenty": "Двадцать", "Thirty": "Тридцать",
    
    # Time
    "Second": "Секунда", "Seconds": "Секунды", "Minute": "Минута",
    "Minutes": "Минуты", "Pause": "Пауза", "Brief": "Короткая",
    "Pause for a second": "Пауза на секунду", "After a second pause": "После секундной паузы",
    "After a brief pause": "После короткой паузы",
    
    # Common phrases
    "Starting position": "Исходное положение", "Initial position": "Исходное положение",
    "Original position": "Исходное положение", "Begin in a": "Начните в положении",
    "This will be the starting position": "Это будет исходное положение",
    "This is your starting position": "Это ваше исходное положение",
    "Return to the starting position": "Вернитесь в исходное положение",
    "Return to starting position": "Вернитесь в исходное положение",
    "Repeat for the recommended amount of repetitions": "Повторите рекомендуемое количество раз",
    "Repeat for recommended amount of times": "Повторите рекомендуемое количество раз",
    "Repeat for prescribed amount of reps": "Повторите заданное количество раз",
    "Repeat for the prescribed amount of times": "Повторите заданное количество раз",
    "Repeat the movement for the recommended amount of repetitions": "Повторите движение рекомендуемое количество раз",
    
    # Exercise terms
    "Rep": "Повторение", "Reps": "Повторения", "Set": "Подход",
    "Sets": "Подходы", "Exercise": "Упражнение", "Movement": "Движение",
    "Range of motion": "Амплитуда движения", "Grip": "Хват",
    "Grasp": "Захват", "Overhand grip": "Пронированный хват",
    "Underhand grip": "Супинированный хват", "Mixed grip": "Смешанный хват",
    "Shoulder-width grip": "Хват на ширине плеч", "Wide grip": "Широкий хват",
    "Narrow grip": "Узкий хват", "Close grip": "Узкий хват",
    "Shoulder width apart": "На ширине плеч",
    
    # Breathing
    "Inhale": "Вдыхайте", "Exhale": "Выдыхайте",
    "Breathe in": "Вдохните", "Breathe out": "Выдохните",
    "Breath normally": "Дышите нормально", "As you exhale": "Когда выдыхаете",
    "As you inhale": "Когда вдыхаете", "When you exhale": "Когда выдыхаете",
    "When you inhale": "Когда вдыхаете",
    
    # Tips/Notes
    "Tip:": "Совет:", "Note:": "Примечание:", "Advice:": "Совет:",
    "Make sure": "Убедитесь", "Be careful": "Будьте осторожны",
    "Ensure": "Убедитесь", "Focus": "Сосредоточьтесь",
    "Concentrate": "Сосредоточьтесь",
    
    # Prepositions/Connectors
    "Towards": "К", "Away from": "От", "With": "С",
    "Without": "Без", "And": "и", "Or": "или", "But": "но",
    "Then": "затем", "Now": "теперь", "Next": "далее",
    "Also": "также", "Too": "тоже", "While": "Пока",
    "As": "когда", "When": "Когда", "If": "Если",
    
    # Adjectives
    "Heavy": "Тяжелый", "Light": "Легкий", "Strong": "Сильный",
    "Weak": "Слабый", "High": "Высокий", "Low": "Низкий",
    "Medium": "Средний", "Wide": "Широкий", "Narrow": "Узкий",
    "Short": "Короткий", "Long": "Длинный", "Good": "Хороший",
    "Bad": "Плохой", "Best": "Лучший", "Better": "Лучше",
    "Worse": "Хуже", "Worst": "Худший", "Small": "Маленький",
    "Big": "Большой", "Large": "Большой", "Little": "Маленький",
    
    # Common words to translate
    "Use": "Используйте", "Using": "Используя", "Try to": "Попытайтесь",
    "Attempt to": "Попытайтесь", "Drive through": "Толкайте через",
    "Step onto": "Встаньте на", "Step off": "Сойдите с",
    "Lean forward": "Подайтесь вперед", "Lean back": "Отклонитесь назад",
    "Look forward": "Смотрите вперед", "Look up": "Смотрите вверх",
    "Look down": "Смотрите вниз", "Keep your back straight": "Держите спину прямо",
    "Keep your head up": "Держите голову поднятой", "Keep your chest up": "Держите грудь поднятой",
    "Engage your core": "Напрягите кор", "Tighten your abs": "Напрягите пресс",
    
    # Garbage cleanup
    "ваше": "ваше", "Ваше": "Ваше", "Ваш": "Ваш",
    "fилиward": "вперед", "fили": "для", "тросs": "тросы",
    "hи": "рука", "hиs": "руки", "tилиso": "торс",
    "perтехника": "выполните", "perтехникаing": "выполняя",
    "wилиk": "работайте", "wилиking": "работая",
    "select": "выберите", "choose": "выберите", "pick": "выберите",
    "appropriate": "подходящий", "suitable": "подходящий",
    "tower": "тренажер", "machine": "тренажер",
    "one": "один", "both": "оба", "each": "каждый",
    "all": "все", "other": "другой", "opposite": "противоположный",
    "same": "тот же", "different": "разный",
}

# Remove short words that shouldn't be translated
remove_words = ["the", "to", "of", "in", "on", "at", "by", "from", "with", "for", "a", "an", "is", "are", "was", "were", "be", "been", "have", "has", "had", "do", "does", "did", "will", "would", "could", "should", "may", "might", "must", "can", "it", "its", "this", "that", "these", "those"]

def translate_instruction(text):
    result = text
    # Apply longest phrases first
    for en, ru in sorted(translations.items(), key=lambda x: len(x[0]), reverse=True):
        pattern = re.compile(r'\b' + re.escape(en) + r'\b', re.IGNORECASE)
        result = pattern.sub(ru, result)
    
    # Remove common English words
    for word in remove_words:
        pattern = re.compile(r'\b' + re.escape(word) + r'\b', re.IGNORECASE)
        result = pattern.sub('', result)
    
    # Clean up extra spaces
    result = re.sub(r'\s+', ' ', result).strip()
    return result

# Process all exercises
modified_count = 0
total_with_english = 0

for exercise in exercises:
    if 'instructions' in exercise:
        instructions = exercise['instructions']
        has_english = False
        modified = False
        for i, instruction in enumerate(instructions):
            # Check if instruction has English text
            if any(ord(c) < 128 and c.isalpha() for c in instruction):
                has_english = True
                translated = translate_instruction(instruction)
                if translated != instruction:
                    instructions[i] = translated
                    modified = True
        if has_english:
            total_with_english += 1
        if modified:
            modified_count += 1

print(f"Total exercises with English: {total_with_english}")
print(f"Modified exercises: {modified_count}")

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("File saved successfully")
