import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Complete translation dictionary
full_translations = {
    # Verbs - all forms
    "Stand": "Встаньте", "Stands": "Стоя", "Standing": "Стоя", "stood": "Стоял",
    "Sit": "Сядьте", "Sits": "Сидит", "Sitting": "Сидя", "sat": "Сел",
    "Lie": "Лягте", "Lies": "Лежит", "Lying": "Лежа", "lay": "Лег",
    "Place": "Поместите", "Placing": "Помещая", "placed": "Помещенный",
    "Position": "Положите", "Positioning": "Устанавливая", "positioned": "Установленный",
    "Hold": "Удерживайте", "Holding": "Удерживая", "held": "Удерживаемый",
    "Keep": "Держите", "Keeping": "Держа", "kept": "Держимый",
    "Bend": "Согните", "Bends": "Сгибает", "Bending": "Сгибая", "bent": "Согнутый",
    "Extend": "Выпрямите", "Extends": "Выпрямляет", "Extending": "Выпрямляя", "extended": "Выпрямленный",
    "Lift": "Поднимите", "Lifts": "Поднимает", "Lifting": "Поднимая", "lifted": "Поднятый",
    "Lower": "Опустите", "Lowers": "Опускает", "Lowering": "Опуская", "lowered": "Опущенный",
    "Raise": "Поднимите", "Raises": "Поднимает", "Raising": "Поднимая", "raised": "Поднятый",
    "Pull": "Тяните", "Pulls": "Тянет", "Pulling": "Тя", "pulled": "Потянутый",
    "Push": "Толкайте", "Pushes": "Толкает", "Pushing": "Толкая", "pushed": "Толкнутый",
    "Press": "Жмите", "Presses": "Жмет", "Pressing": "Жимая", "pressed": "Жатый",
    "Squeeze": "Сжимайте", "Squeezes": "Сжимает", "Squeezing": "Сжимая", "squeezed": "Сжатый",
    "Contract": "Сокращайте", "Contracts": "Сокращает", "Contracting": "Сокращая", "contracted": "Сокращенный",
    "Relax": "Расслабьте", "Relaxes": "Расслабляет", "Relaxing": "Расслабляя", "relaxed": "Расслабленный",
    "Return": "Вернитесь", "Returns": "Возвращается", "Returning": "Возвращаясь", "returned": "Возвращенный",
    "Repeat": "Повторите", "Repeats": "Повторяет", "Repeating": "Повторяя", "repeated": "Повторенный",
    "Start": "Начните", "Starts": "Начинает", "Starting": "Начиная", "started": "Начатый",
    "Begin": "Начинайте", "Begins": "Начинает", "Beginning": "Начиная", "begun": "Начатый",
    "Move": "Переместите", "Moves": "Перемещает", "Moving": "Перемещая", "moved": "Перемещенный",
    "Rotate": "Поверните", "Rotates": "Поворачивает", "Rotating": "Поворачивая", "rotated": "Повернутый",
    "Turn": "Поверните", "Turns": "Поворачивает", "Turning": "Поворачивая", "turned": "Повернутый",
    "Step": "Шагните", "Steps": "Шагает", "Stepping": "Шагая", "stepped": "Шагнувший",
    "Walk": "Ходите", "Walks": "Ходит", "Walking": "Ходя", "walked": "Прошедший",
    "Jump": "Прыгайте", "Jumps": "Прыгает", "Jumping": "Прыгая", "jumped": "Прыгнувший",
    "Drive": "Толкайте", "Drives": "Толкает", "Driving": "Толкая", "driven": "Притолкнутый",
    "Roll": "Катите", "Rolls": "Катит", "Rolling": "Катая", "rolled": "Прокатанный",
    "Swing": "Раскачивайте", "Swings": "Раскачивает", "Swinging": "Раскачивая", "swung": "Раскаченный",
    "Clean": "Подтяните", "Cleans": "Подтягивает", "Cleaning": "Подтягивая", "cleaned": "Подтянутый",
    "Grab": "Возьмите", "Grabs": "Берет", "Grabbing": "Беря", "grabbed": "Взятый",
    "Grasp": "Захватите", "Grasps": "Захватывает", "Grasping": "Захватывая", "grasped": "Захваченный",
    "Pick": "Возьмите", "Picks": "Берет", "Picking": "Беря", "picked": "Взятый",
    
    # Nouns - body parts
    "Feet": "Ступни", "Foot": "Ступня", "Hands": "Руки", "Hand": "Рука",
    "Arms": "Руки", "Arm": "Рука", "Legs": "Ноги", "Leg": "Нога",
    "Knees": "Колени", "Knee": "Колено", "Elbows": "Локти", "Elbow": "Локоть",
    "Shoulders": "Плечи", "Shoulder": "Плечо", "Chest": "Грудь", "Back": "Спина",
    "Head": "Голова", "Neck": "Шея", "Torso": "Торс", "Body": "Тело",
    "Hips": "Бедра", "Hip": "Бедро", "Glutes": "Ягодицы", "Quads": "Квадрицепс",
    "Hamstrings": "Бицепс бедра", "Calves": "Икры", "Forearms": "Предплечья",
    "Biceps": "Бицепс", "Triceps": "Трицепс", "Abs": "Пресс", "Abdominals": "Пресс",
    "Lats": "Широчайшие", "Delts": "Дельты", "Traps": "Трапеция",
    
    # Equipment
    "Floor": "Пол", "Ground": "Пол", "Barbell": "Штанга", "Bar": "Гриф",
    "Dumbbells": "Гантели", "Dumbbell": "Гантель", "Kettlebells": "Гири", "Kettlebell": "Гиря",
    "Bench": "Скамья", "Rack": "Стойка", "Machine": "Тренажер", "Mat": "Коврик",
    "Band": "Эспандер", "Bands": "Эспандеры", "Cable": "Трос", "Cables": "Тросы",
    "Ball": "Мяч", "Box": "Ящик", "Platform": "Платформа", "Weight": "Вес",
    
    # Directions
    "Forward": "Вперед", "Backward": "Назад", "Up": "Вверх", "Down": "Вниз",
    "Left": "Левый", "Right": "Правый", "Side": "Сторона", "Sides": "Стороны",
    "Front": "Перед", "Rear": "Зад", "Overhead": "Над головой",
    
    # Common phrases
    "Starting position": "Исходное положение",
    "This will be the starting position": "Это будет исходное положение",
    "This is your starting position": "Это ваше исходное положение",
    "Return to the starting position": "Вернитесь в исходное положение",
    "Keep your back straight": "Держите спину прямо",
    "Keep your head up": "Держите голову поднятой",
    "Keep your chest up": "Держите грудь поднятой",
    "Squeeze your chest": "Сожмите грудь",
    "Contract your abs": "Сокращайте пресс",
    "Engage your core": "Напрягите кор",
    "Breathe in": "Вдохните", "Breathe out": "Выдохните",
    "Inhale": "Вдыхайте", "Exhale": "Выдыхайте",
    "Tip:": "Совет:", "Note:": "Примечание:",
    "Make sure": "Убедитесь", "Be careful": "Будьте осторожны",
    
    # Adjectives/Adverbs
    "Slowly": "Медленно", "Quickly": "Быстро", "Slightly": "Слегка",
    "Fully": "Полностью", "Completely": "Полностью", "Carefully": "Осторожно",
    "Properly": "Правильно", "Directly": "Прямо", "Immediately": "Сразу",
    "Eventually": "В конце концов", "Gradually": "Постепенно", "Continuously": "Непрерывно",
    
    # Numbers
    "One": "Один", "Two": "Два", "Three": "Три", "Four": "Четыре",
    "Five": "Пять", "Six": "Шесть", "Seven": "Семь", "Eight": "Восемь",
    "Nine": "Девять", "Ten": "Десять", "Twenty": "Двадцать",
    "Thirty": "Тридцать", "Forty": "Сорок", "Fifty": "Пятьдесят",
    
    # Time
    "Second": "Секунда", "Seconds": "Секунды", "Minute": "Минута",
    "Minutes": "Минуты", "Pause": "Пауза", "Brief": "Короткая",
    "Briefly": "Коротко", "Duration": "Продолжительность",
    
    # Measurements
    "Inch": "Дюйм", "Inches": "Дюймы", "Foot": "Фут", "Feet": "Футы",
    "Degree": "Градус", "Degrees": "Градусы", "Angle": "Угол",
    "Width": "Ширина", "Height": "Высота", "Length": "Длина",
    "Distance": "Расстояние", "Range": "Диапазон",
    
    # Exercise terms
    "Rep": "Повторение", "Reps": "Повторения", "Set": "Подход",
    "Sets": "Подходы", "Exercise": "Упражнение", "Movement": "Движение",
    "Grip": "Хват", "Grasp": "Захват", "Hold": "Удержание",
    "Wide grip": "Широкий хват", "Narrow grip": "Узкий хват",
    "Shoulder-width": "На ширине плеч", "Hip-width": "На ширине бедер",
    
    # Quality/State
    "Stable": "Стабильный", "Steady": "Устойчивый", "Control": "Контроль",
    "Balanced": "Сбалансированный", "Comfortable": "Комфортный", "Possible": "Возможный",
    "Impossible": "Невозможный", "Necessary": "Необходимый", "Optional": "Необязательный",
    "Recommended": "Рекомендуемый", "Prescribed": "Предписанный",
    "Appropriate": "Подходящий", "Suitable": "Подходящий",
    
    # Prepositions/Connectors
    "Towards": "К", "Away from": "От", "Through": "Через", "Across": "Через",
    "Around": "Вокруг", "Between": "Между", "Against": "Против",
    "Along": "Вдоль", "Beyond": "За", "Near": "Рядом", "Far": "Далеко",
    "Then": "Затем", "Now": "Сейчас", "Next": "Далее", "Finally": "Наконец",
    "Also": "Также", "Too": "Тоже", "Either": "Либо", "Neither": "Ни",
    "Both": "Оба", "All": "Все", "None": "Ни один",
    "Each": "Каждый", "Every": "Каждый", "Some": "Некоторые",
    "Any": "Любой", "Other": "Другой", "Another": "Другой",
    "Same": "Тот же", "Different": "Разный", "Opposite": "Противоположный",
    
    # Pronouns/Possessives (keep Russian)
    "Your": "ваш", "Your": "Ваш",
    
    # Common cleanup words
    "Select": "выберите", "Choose": "выберите", "Pick": "выберите",
    "Adjust": "отрегулируйте", "Set": "установите", "Lock": "зафиксируйте",
    "Unlock": "разблокируйте", "Secure": "закрепите", "Fasten": "прикрепите",
    "Release": "отпустите", "Let go": "отпустите", "Drop": "опустите",
}

# Words to remove
remove_words = ["the", "a", "an", "to", "of", "in", "on", "at", "by", "from", "with", "for", "and", "or", "but", "as", "if", "when", "while", "after", "before", "until", "since", "because", "so", "that", "this", "these", "those", "it", "its", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "will", "would", "could", "should", "may", "might", "must", "can", "need", "want"]

def translate_instruction(text):
    # Apply translations in order of length (longest first)
    for en, ru in sorted(full_translations.items(), key=lambda x: len(x[0]), reverse=True):
        pattern = re.compile(r'\b' + re.escape(en) + r'\b', re.IGNORECASE)
        text = pattern.sub(ru, text)
    
    # Remove common English words
    for word in remove_words:
        pattern = re.compile(r'\b' + re.escape(word) + r'\b', re.IGNORECASE)
        text = pattern.sub('', text)
    
    # Clean up multiple spaces
    text = re.sub(r'\s+', ' ', text).strip()
    
    return text

# Process all exercises
modified_count = 0
for exercise in exercises:
    if 'instructions' in exercise:
        instructions = exercise['instructions']
        modified = False
        for i, instruction in enumerate(instructions):
            # Check if has English text
            if any(ord(c) < 128 and c.isalpha() for c in instruction):
                translated = translate_instruction(instruction)
                if translated != instruction and translated:
                    instructions[i] = translated
                    modified = True
        if modified:
            modified_count += 1

print(f"Modified {modified_count} exercises")

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("Translation complete!")
