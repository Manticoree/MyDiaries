# -*- coding: utf-8 -*-
import json
import re

print("Checking exercises.json for English text...")

with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

total = len(exercises)
has_english = 0

for idx, exercise in enumerate(exercises):
    instructions = exercise.get('instructions', [])
    has_en = any(re.search(r'[a-zA-Z]{3,}', inst) for inst in instructions)
    if has_en:
        has_english += 1
        if has_english <= 10:
            print(f"{idx+1}. {exercise.get('name', 'Unknown')}")
            for inst in instructions[:2]:
                print(f"   {inst[:100]}")

print(f"\nTotal exercises: {total}")
print(f"Exercises with English text: {has_english}")
