# -*- coding: utf-8 -*-
import json
import re

phrase_dict = {
    'Move the cables': 'Переместите тросы',
    'select an appropriate weight': 'выберите подходящий вес',
    'Grasp the cables': 'Возьмите тросы',
    'hold them': 'удерживайте их',
    'at shoulder height': 'на уровне плеч',
    'with your palms facing forward': 'ладонями направленными вперед',
    'This will be your starting position': 'Это будет исходное положение',
}

def translate_text(text):
    result = text
    for eng, rus in phrase_dict.items():
        if eng.lower() in result.lower():
            result = result.replace(eng, rus)
    return result

with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

print(f"Total: {len(exercises)}")
for i, exercise in enumerate(exercises[:3]):
    print(f"{i+1}. {exercise.get('name', 'Unknown')}")
    for inst in exercise.get('instructions', [])[:1]:
        print(f"  {translate_text(inst)[:100]}")
