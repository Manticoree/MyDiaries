import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Manual translations for specific exercises
manual_translations = {
    "Alternating_Cable_Shoulder_Press": [
        "Переместите тросы к низу тренажера и выберите подходящий вес.",
        "Захватите тросы и удерживайте их на высоте плеч, ладони смотрят вперед. Это будет исходное положение.",
        "Держите голову и грудь поднятыми, выжмите через локоть, поднимая одну сторону прямо над головой.",
        "После паузы в верхней точке вернитесь в исходное положение и повторите на противоположной стороне."
    ],
    "Alternating_Deltoid_Raise": [
        "В положении сидя, держите пару гантелей по бокам.",
        "Держа локти слегка согнутыми, поднимите гантели прямо перед собой до уровня плеч, избегая раскачивания.",
        "Верните гантели к бокам.",
        "В следующем повторении поднимите гантели в стороны, поднимая их наружу до уровня плеч.",
        "Верните гантели в исходное положение и продолжайте чередовать перед и в стороны."
    ],
    "Alternating_Floor_Press": [
        "Лягте на пол с двумя гирями у плеч.",
        "Поместите одну на грудь, затем другую, хватая гири хватом с ладонями, смотрящими вперед.",
        "Выпрямите обе руки так, чтобы гири удерживались над грудью. Опустите одну гирю к груди, поворачивая запястье в сторону зафиксированной гири.",
        "Поднимите гирю и повторите на противоположной стороне."
    ],
    "Alternating_Hang_Clean": [
        "Поместите две гири между ступнями. Для исходного положения отведите таз назад и смотрите прямо вперед.",
        "Поднимите одну гирю к плечу и удерживайте другую в висящем положении. Поднимите гирю к плечу, разгибая через ноги и бедра, тяните гирю к плечам. Поверните запястье при этом.",
        "Опустите поднятую гирю в висящее положение и поднимите другую. Повторите."
    ],
    "Alternating_Kettlebell_Press": [
        "Поднимите две гири к плечам. Поднимите гири к плечам, разгибая через ноги и бедра, тяните гири к плечам. Поверните запястья при этом.",
        "Выжмите одну прямо над головой, разгибая локоть, поворачивая так, чтобы ладонь смотрела вперед, удерживая другую гирю неподвижно.",
        "Опустите выжатую гирю в исходное положение и сразу жмите другой рукой."
    ],
}

# Apply manual translations
modified_count = 0
for exercise in exercises:
    exercise_id = exercise.get('id')
    if exercise_id in manual_translations:
        exercise['instructions'] = manual_translations[exercise_id]
        modified_count += 1
    else:
        # For exercises without manual translation, clean up the current text
        instructions = exercise.get('instructions', [])
        cleaned = []
        for instr in instructions:
            # Clean up common patterns
            cleaned_instr = re.sub(r'\s+', ' ', instr).strip()
            cleaned_instr = cleaned_instr.replace('ваше', 'ваш')
            cleaned_instr = cleaned_instr.replace('Ваше', 'Ваш')
            cleaned_instr = cleaned_instr.replace('тросs', 'тросы')
            cleaned_instr = cleaned_instr.replace('ваш', 'ваш')
            cleaned_instr = re.sub(r'\b(select|choose|pick)\b', 'выберите', cleaned_instr, flags=re.IGNORECASE)
            cleaned_instr = re.sub(r'\b(appropriate)\b', 'подходящий', cleaned_instr, flags=re.IGNORECASE)
            cleaned_instr = re.sub(r'\b(tower|machine)\b', 'тренажер', cleaned_instr, flags=re.IGNORECASE)
            cleaned_instr = re.sub(r'\b(cable|rope)\b', 'трос', cleaned_instr, flags=re.IGNORECASE)
            cleaned.append(cleaned_instr)
        exercise['instructions'] = cleaned

print(f"Applied {modified_count} manual translations")
print(f"Total exercises: {len(exercises)}")

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("File saved successfully")
