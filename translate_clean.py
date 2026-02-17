import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Clean up remaining English fragments and fix common issues
def clean_instruction(text):
    # Remove common English words and fragments
    text = re.sub(r'\b(the|to|of|in|on|at|by|from|with|for|a|an|as|is|are|be|do|have|it|this|that|your|you|their|its|our|my|his|her|they|them|these|those)\b', '', text, flags=re.IGNORECASE)
    
    # Fix common translation errors
    text = re.sub(r'Ваше(\s+)', r'Ваш\1', text)
    text = re.sub(r'Удерживайтеing', 'удерживая', text)
    text = re.sub(r'Держитеing', 'держа', text)
    text = re.sub(r'Сгибая(\s+)', 'Сгибая\1', text)
    text = re.sub(r'Выпрямляясь(\s+)', 'выпрямляя\1', text)
    text = re.sub(r'Поднимая(\s+)', 'поднимая\1', text)
    text = re.sub(r'Опуская(\s+)', 'опуская\1', text)
    text = re.sub(r'Тя(\s+)', 'Тяня\1', text)
    text = re.sub(r'Толкая(\s+)', 'толкая\1', text)
    
    # Fix remaining English words
    replacements = {
        "stepping": "шагая",
        "walking": "ходя",
        "moving": "двигая",
        "holding": "удерживая",
        "keeping": "держа",
        "using": "используя",
        "placing": "помещая",
        "positioning": "устанавливая",
        "lowering": "опуская",
        "raising": "поднимая",
        "pulling": "тя",
        "pushing": "толкая",
        "pressing": "жимая",
        "squeezing": "сжимая",
        "contracting": "сокращая",
        "relaxing": "расслабляя",
        "bending": "сгибая",
        "extending": "выпрямляя",
        "rotating": "поворачивая",
        "turning": "поворачивая",
        "leaning": "наклоняясь",
        "looking": "смотря",
        "twisting": "скручивая",
        "swinging": "раскачивая",
        "flexing": "сгибая",
        "rolling": "катая",
        "jumping": "прыгая",
        "driving": "толкайте",
        "cleaning": "подтягивая",
        "dragging": "тя",
        "working": "работая",
        "facing": "обращены",
        "starting": "начиная",
        "beginning": "начиная",
        "ending": "заканчивая",
        "finishing": "завершая",
        "stopping": "останавливая",
        "stopping": "останавливаясь",
        "standing": "стоя",
        "sitting": "сидя",
        "lying": "лежа",
        "hanging": "вися",
        "kneeling": "на коленях",
        
        # More common words
        "side": "сторона",
        "sides": "стороны",
        "front": "перед",
        "back": "спина",
        "top": "верх",
        "bottom": "низ",
        "middle": "середина",
        "center": "центр",
        "edge": "край",
        "corner": "угол",
        "angle": "угол",
        "range": "диапазон",
        "distance": "расстояние",
        "height": "высота",
        "width": "ширина",
        "level": "уровень",
        "amount": "количество",
        "number": "число",
        "count": "счет",
        "time": "время",
        "second": "секунда",
        "seconds": "секунды",
        "minute": "минута",
        "minutes": "минуты",
        "step": "шаг",
        "steps": "шаги",
        "rep": "повторение",
        "reps": "повторения",
        "set": "подход",
        "sets": "подходы",
        
        # Body parts
        "hand": "рука",
        "hands": "руки",
        "foot": "ступня",
        "feet": "ступни",
        "leg": "нога",
        "legs": "ноги",
        "arm": "рука",
        "arms": "руки",
        "knee": "колено",
        "knees": "колени",
        "elbow": "локоть",
        "elbows": "локти",
        "shoulder": "плечо",
        "shoulders": "плечи",
        "hip": "бедро",
        "hips": "бедра",
        "chest": "грудь",
        "back": "спина",
        "neck": "шея",
        "head": "голова",
        "face": "лицо",
        "eyes": "глаза",
        "body": "тело",
        "muscle": "мышца",
        "muscles": "мышцы",
        
        # Directions
        "forward": "вперед",
        "backward": "назад",
        "upward": "вверх",
        "downward": "вниз",
        "outward": "наружу",
        "inward": "внутрь",
        "toward": "к",
        "away": "от",
        "through": "через",
        "across": "через",
        "around": "вокруг",
        "between": "между",
        "against": "против",
        "along": "вдоль",
        "past": "мимо",
        "beyond": "за",
        
        # Common phrases
        "possible": "возможно",
        "comfortable": "комфортно",
        "properly": "правильно",
        "correctly": "правильно",
        "safely": "безопасно",
        "slowly": "медленно",
        "quickly": "быстро",
        "carefully": "осторожно",
        "gently": "аккуратно",
        "firmly": "прочно",
        "tightly": "крепко",
        "loosely": "свободно",
        "directly": "прямо",
        "slightly": "слегка",
        "fully": "полностью",
        "completely": "полностью",
        "gradually": "постепенно",
        "continuously": "непрерывно",
        "alternately": "поочередно",
        "repeatedly": "повторно",
        "successively": "последовательно",
        "simultaneously": "одновременно",
    }
    
    # Apply replacements
    for en, ru in replacements.items():
        text = re.sub(r'\b' + re.escape(en) + r'\b', ru, text, flags=re.IGNORECASE)
    
    # Clean up multiple spaces
    text = re.sub(r'\s+', ' ', text).strip()
    
    # Remove any remaining English-only words (approximate detection)
    words = text.split()
    clean_words = []
    for word in words:
        # Keep word if it contains Cyrillic characters
        if any(ord(c) > 127 for c in word):
            clean_words.append(word)
    text = ' '.join(clean_words)
    
    return text

# Process all exercises
modified_count = 0
remaining_english = 0

for exercise in exercises:
    if 'instructions' in exercise:
        instructions = exercise['instructions']
        has_english = False
        modified = False
        for i, instruction in enumerate(instructions):
            # Check if instruction has English text
            if any(ord(c) < 128 and c.isalpha() for c in instruction):
                has_english = True
                cleaned = clean_instruction(instruction)
                if cleaned != instruction and cleaned:
                    instructions[i] = cleaned
                    modified = True
        if has_english:
            remaining_english += 1
        if modified:
            modified_count += 1

print(f"Exercises with remaining English: {remaining_english}")
print(f"Modified exercises: {modified_count}")

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("File saved successfully")
