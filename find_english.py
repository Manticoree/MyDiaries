import json

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

# Find exercises with English in instructions
english_exercises = []
for ex in exercises:
    for instr in ex.get('instructions', []):
        # Check for English letters (Latin alphabet)
        if any(c.isalpha() and ord(c) < 128 for c in instr):
            english_exercises.append(ex['id'])
            break

print(f'Exercises with English text: {len(english_exercises)}')
print('First 30:')
for i, ex_id in enumerate(english_exercises[:30], 1):
    print(f'{i}. {ex_id}')
