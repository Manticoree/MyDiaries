import json
import re

with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

total = len(exercises)
needs_translation = 0

for ex in exercises:
    instr = ex.get('instructions', [])
    has_english = False
    for inst in instr:
        if re.search(r'[a-zA-Z]{3,}', inst):
            has_english = True
            break
    if has_english:
        needs_translation += 1

report = f"""
Статус перевода упражнений в exercises.json
{'=' * 60}

Всего упражнений: {total}
Упражнений переведено: {total - needs_translation} ({(total - needs_translation) * 100 // total}%)
Упражнений с английским текстом: {needs_translation} ({needs_translation * 100 // total}%)

{'=' * 60}

Требуется завершить перевод {needs_translation} упражнений.
"""

count = 0
for ex in exercises:
    instr = ex.get('instructions', [])
    has_english = any(re.search(r'[a-zA-Z]{3,}', inst) for inst in instr)
    if has_english and count < 5:
        report += f"{count + 1}. {ex['name']}\n"
        report += f"   ID: {ex['id']}\n"
        for i, inst in enumerate(instr[:2]):
            short_inst = inst[:80] + '...' if len(inst) > 80 else inst
            report += f"   {i+1}. {short_inst}\n"
        report += "\n"
        count += 1
    if count >= 5:
        break

print(report)
