import json
import re

# Read JSON
with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
    exercises = json.load(f)

def is_mostly_russian(text):
    # Check if text is mostly Russian (>50% Russian letters)
    cyrillic_count = sum(1 for c in text if 1040 <= ord(c) <= 1103 or 1025 <= ord(c) <= 1071)
    alpha_count = sum(1 for c in text if c.isalpha())
    if alpha_count == 0:
        return False
    return cyrillic_count / alpha_count > 0.5

def fix_mixed_text(text):
    # If text is mostly English, return None to mark for manual translation
    if not is_mostly_russian(text):
        return text  # Keep as is for now, will use generic cleanup
    
    # If mostly Russian, clean up remaining English fragments
    result = text
    
    # Remove common English words
    result = re.sub(r'\b(the|to|of|in|on|at|by|from|with|for|a|an|is|are|was|were|be|been|have|has|had|do|does|did|will|would|could|should|may|might|must|can|it|its|this|that|these|those|your|you|their|our|my|his|her|they|them|he|she|we|us|me|him)\b', '', result, flags=re.IGNORECASE)
    
    # Fix common patterns
    result = re.sub(r'\s+', ' ', result).strip()
    return result

# Process exercises
for exercise in exercises:
    if 'instructions' in exercise:
        cleaned_instructions = []
        for instruction in exercise['instructions']:
            fixed = fix_mixed_text(instruction)
            if fixed:
                cleaned_instructions.append(fixed)
        exercise['instructions'] = cleaned_instructions

# Save
with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
    json.dump(exercises, f, ensure_ascii=False, indent=2)

print("File saved - cleaned mixed text")
