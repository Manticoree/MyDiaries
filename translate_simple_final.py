import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Simple but comprehensive translation map
simple_translations = {
    # Action verbs
    "Lie": "Лягте", "lay": "лягте", "lying": "лежа",
    "Sit": "Сядьте", "sitting": "сидя",
    "Stand": "Встаньте", "standing": "стоя",
    "Place": "Поместите", "placing": "помещая", "placed": "помещенный",
    "Position": "Положите", "positioning": "устанавливая",
    "Hold": "Удерживайте", "holding": "удерживая", "held": "удерживаемый",
    "Keep": "Держите", "keeping": "держа",
    "Bend": "Согните", "bending": "сгибая", "bent": "согнутый",
    "Extend": "Выпрямите", "extending": "выпрямляя", "extended": "выпрямленный",
    "Lift": "Поднимите", "lifting": "поднимая", "lifted": "поднятый",
    "Lower": "Опустите", "lowering": "опуская", "lowered": "опущенный",
    "Raise": "Поднимите", "raising": "поднимая", "raised": "поднятый",
    "Pull": "Тяните", "pulling": "тя", "pulled": "потянутый",
    "Push": "Толкайте", "pushing": "толкая", "pushed": "толкнутый",
    "Press": "Жмите", "pressing": "жимая", "pressed": "жатый",
    "Squeeze": "Сжимайте", "squeezing": "сжимая", "squeezed": "сжатый",
    "Contract": "Сокращайте", "contracting": "сокращая", "contracted": "сокращенный",
    "Relax": "Расслабьте", "relaxing": "расслабляя", "relaxed": "расслабленный",
    "Return": "Вернитесь", "returning": "возвращаясь", "returned": "возвращенный",
    "Repeat": "Повторите", "repeating": "повторяя", "repeated": "повторенный",
    "Rotate": "Поверните", "rotating": "поворачивая", "rotated": "повернутый",
    "Turn": "Поверните", "turning": "поворачивая", "turned": "повернутый",
    "Step": "Шагните", "stepping": "шагая", "stepped": "шагнувший",
    "Jump": "Прыгайте", "jumping": "прыгая", "jumped": "прыгавший",
    "Lean": "Наклонитесь", "leaning": "наклоняясь", "leaned": "наклонившийся",
    "Look": "Смотрите", "looking": "смотря", "looked": "посмотрел",
    "Move": "Переместите", "moving": "перемещая", "moved": "перемещенный",
    "Walk": "Ходите", "walking": "ходя", "walked": "прошел",
    "Drive": "Толкайте", "driving": "толкая",
    "Clean": "Подтяните", "cleaning": "подтягивая",
    "Drag": "Тяните", "dragging": "тя",
    
    # Body parts
    "feet": "ступни", "foot": "ступня", "toes": "пальцы", "toe": "палец",
    "hands": "руки", "hand": "рука", "fingers": "пальцы", "finger": "палец",
    "arms": "руки", "arm": "рука", "forearms": "предплечья", "forearm": "предплечье",
    "legs": "ноги", "leg": "нога", "knees": "колени", "knee": "колено",
    "hips": "бедра", "hip": "бедро", "glutes": "ягодицы", "glute": "ягодица",
    "chest": "грудь", "back": "спина", "shoulders": "плечи", "shoulder": "плечо",
    "head": "голова", "neck": "шея", "torso": "торс", "core": "кор",
    "elbows": "локти", "elbow": "локоть", "wrists": "запястья", "wrist": "запястье",
    "abs": "пресс", "abdominals": "пресс", "biceps": "бицепс", "triceps": "трицепс",
    "lats": "широчайшие", "deltoids": "дельты", "traps": "трапеция",
    "quadriceps": "квадрицепс", "quads": "квадрицепс",
    "hamstrings": "бицепс бедра", "calves": "икры", "calf": "икра",
    
    # Equipment
    "floor": "пол", "ground": "пол", "bench": "скамья",
    "barbell": "штанга", "bar": "гриф", "dumbbells": "гантели", "dumbbell": "гантель",
    "kettlebells": "гири", "kettlebell": "гиря",
    "machine": "тренажер", "band": "эспандер", "bands": "эспандеры",
    "cable": "трос", "cables": "тросы", "rope": "канат", "chain": "цепь",
    "ball": "мяч", "box": "ящик", "platform": "платформа",
    "block": "блок", "pad": "подушка", "mat": "коврик",
    "weights": "веса", "weight": "вес", "plates": "блины",
    
    # Direction words
    "forward": "вперед", "backward": "назад", "up": "вверх", "down": "вниз",
    "left": "левый", "right": "правый", "side": "сторона", "sides": "стороны",
    "front": "перед", "rear": "зад", "top": "верх", "bottom": "низ",
    "through": "через", "across": "через", "around": "вокруг",
    
    # Common phrases
    "starting position": "исходное положение", "initial position": "исходное положение",
    "original position": "исходное положение",
    "this will be the starting position": "Это будет исходное положение",
    "this is your starting position": "Это ваше исходное положение",
    "return to the starting position": "Вернитесь в исходное положение",
    "repeat for the recommended amount of repetitions": "Повторите рекомендуемое количество раз",
    "repeat the movement for the prescribed amount of times": "Повторите движение заданное количество раз",
    
    # Breathing
    "inhale": "вдыхайте", "exhale": "выдыхайте", "breathe in": "вдохните", "breathe out": "выдохните",
    "as you exhale": "когда выдыхаете", "as you inhale": "когда вдыхаете",
    "while exhaling": "выдыхая", "while inhaling": "вдыхая",
    
    # Time
    "second": "секунда", "seconds": "секунды", "pause for a second": "Пауза на секунду",
    "after a brief pause": "После короткой паузы", "after a second pause": "После секундной паузы",
    
    # Numbers
    "one": "один", "two": "два", "three": "три", "four": "четыре",
    "five": "пять", "six": "шесть", "seven": "семь", "eight": "восемь",
    "nine": "девять", "ten": "десять", "twenty": "двадцать",
    
    # Quality
    "slowly": "медленно", "quickly": "быстро", "slightly": "слегка",
    "fully": "полностью", "completely": "полностью", "carefully": "осторожно",
    "controlled": "контролируемо", "steady": "устойчиво",
    
    # Exercise terms
    "rep": "повторение", "reps": "повторения", "set": "подход", "sets": "подходы",
    "grip": "хват", "grasp": "захват", "overhand grip": "пронированный хват",
    "underhand grip": "супинированный хват", "mixed grip": "смешанный хват",
    "shoulder-width grip": "хват на ширине плеч", "wide grip": "широкий хват",
    "narrow grip": "узкий хват", "close grip": "узкий хват",
    "shoulder width apart": "на ширине плеч", "hip width apart": "на ширине бедер",
    
    # Instructions
    "tip:": "Совет:", "note:": "Примечание:",
    "make sure": "Убедитесь", "be careful": "Будьте осторожны",
    "ensure": "Убедитесь", "focus": "Сосредоточьтесь", "concentrate": "Сосредоточьтесь",
    
    # Connectors
    "towards": "к", "away": "от", "through": "через",
    "and": "и", "or": "или", "but": "но", "then": "затем",
    "now": "теперь", "next": "далее", "also": "также", "too": "тоже",
    "while": "пока", "when": "когда", "if": "если", "as": "когда",
    
    # Adjectives
    "heavy": "тяжелый", "light": "легкий", "strong": "сильный", "weak": "слабый",
    "high": "высокий", "low": "низкий", "medium": "средний",
    "wide": "широкий", "narrow": "узкий", "short": "короткий", "long": "длинный",
    "stable": "стабильный", "balanced": "сбалансированный",
    "straight": "прямой", "bent": "согнутый",
    
    # Common cleanup
    "the": "", "to": "", "of": "", "in": "", "on": "", "at": "",
    "by": "", "from": "", "for": "", "with": "", "without": "без",
    "a": "", "an": "", "is": "", "are": "", "was": "", "were": "",
    "be": "", "been": "", "being": "", "have": "", "has": "", "had": "",
    "do": "", "does": "", "did": "", "doing": "", "will": "", "would": "",
    "could": "", "should": "", "may": "", "might": "", "must": "", "can": "",
    "your": "ваш", "Your": "Ваш", "you": "вы", "You": "Вы",
}

def translate_text(text):
    result = text.lower()
    
    # Apply translations (longest first to handle phrases)
    for en, ru in sorted(simple_translations.items(), key=lambda x: len(x[0]), reverse=True):
        result = result.replace(en.lower(), ru)
    
    # Capitalize first letter if original was capitalized
    if text and text[0].isupper():
        result = result[0].upper() + result[1:] if result else result
    
    return result

# Process all exercises
modified_count = 0
for exercise in exercises:
    if 'instructions' in exercise:
        instructions = exercise['instructions']
        modified = False
        for i, instruction in enumerate(instructions):
            # Check if has English
            if any(ord(c) < 128 and c.isalpha() for c in instruction):
                translated = translate_text(instruction)
                if translated != instruction:
                    instructions[i] = translated
                    modified = True
        if modified:
            modified_count += 1

print(f"Modified {modified_count} exercises")

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("Translation complete!")
