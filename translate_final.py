# -*- coding: utf-8 -*-
import json
import re

# Простой словарь перевода
translation_map = {
    'Move the cables': 'Переместите тросы',
    'select an appropriate weight': 'выберите подходящий вес',
    'Grasp the cables': 'Возьмите тросы',
    'hold them': 'удерживайте их',
    'at shoulder height': 'на уровне плеч',
    'with your palms facing forward': 'ладонями направленными вперед',
    'This will be your starting position': 'Это будет исходное положение',
    'Keeping your head and chest up': 'Держа голову и грудь поднятыми',
    'extend through the elbow': 'разгибайте через локоть',
    'to press one side': 'чтобы нажать одну сторону',
    'directly over your head': 'прямо над головой',
    'After pausing at the top': 'После паузы вверху',
    'return to the starting position': 'вернитесь в исходное положение',
    'and repeat on the opposite side': 'и повторите на противоположной стороне',
    'In a sitting position': 'В положении сидя',
    'hold a pair of dumbbells': 'держите пару гантелей',
    'at your side': 'по бокам',
    'Keeping your elbows slightly bent': 'Держа локти слегка согнутыми',
    'raise the weights': 'поднимите веса',
    'directly in front of you': 'прямо перед собой',
    'to shoulder height': 'на уровень плеч',
    'avoiding any swinging or cheating': 'избегая раскачивания или читинга',
    'Return the weights to your side': 'Верните веса по бокам',
    'On the next repetition': 'На следующем повторении',
    'raise the weights laterally': 'поднимите веса в стороны',
    'raising them out to your side': 'поднимая их наружу к бокам',
    'to approximately shoulder height': 'приблизительно на уровень плеч',
    'Return the weights to the starting position': 'Верните веса в исходное положение',
    'and continue alternating to the front and side': 'и продолжайте чередование кпереду и в стороны',
    'Clean two kettlebells to your shoulders': 'Поднимите две гири к плечам',
    'by extending through the legs and hips': 'разгибая через ноги и бедра',
    'as you pull the kettlebells towards your shoulders': 'когда тянете гири к плечам',
    'Rotate your wrists': 'Поверните запястья',
    'as you do so': 'делая это',
    'Press one directly overhead': 'Нажмите одну прямо над головой',
    'by extending through the elbow': 'разгибая через локоть',
    'turning it so the palm faces forward': 'поворачивая ее чтобы ладонь смотрела вперед',
    'while holding the other kettlebell stationary': 'удерживая при этом другую гирю на месте',
    'Lower the pressed kettlebell to the starting position': 'Опустите нажатую гирю в исходное положение',
    'and immediately press': 'и немедленно нажмите',
    'with your other arm': 'другой рукой',
    'Place two kettlebells in front of your feet': 'Поместите две гири перед ступнями',
    'Squat down': 'присядьте',
    'to pick them up': 'чтобы поднять их',
    'keeping your back straight': 'держа спину прямо',
    'and looking forward': 'и глядя вперед',
    'Pull one kettlebell off of the floor': 'Тяните одну гирю с пола',
    'while holding on to the other kettlebell': 'удерживая другую гирю',
    'Retract the shoulder blade': 'Втяните лопатку',
    'of the working side': 'рабочей стороны',
    'as you flex the elbow': 'когда сгибаете локоть',
    'drawing the kettlebell towards your stomach': 'тяча гирю к животу',
    'or rib cage': 'или к грудной клетке',
    'Lower the kettlebell in the working arm': 'Опустите гирю в рабочей руке',
    'and repeat with your other arm': 'и повторите другой рукой',
    'Place two kettlebells on the floor': 'Поместите две гири на пол',
    'about shoulder width apart': 'приблизительно на ширине плеч',
    'Position yourself on your toes': 'Встаньте на носки',
    'and your hips': 'и бедра',
    'as though you were doing a pushup': 'как будто делаете отжимание',
    'with the body straight': 'с телом прямым',
    'and extended': 'и вытянутым',
    'Use the handles': 'Используйте рукоятки',
    'of the kettlebells': 'гирь',
    'to support your upper body': 'для поддержки верхней части тела',
    'You may need to position your feet wide': 'Возможно вам придется широко поставить ноги',
    'for support': 'для поддержки',
    'Push one kettlebell into the floor': 'Толкайте одну гирю в пол',
    'and row the other kettlebell': 'и тяните другую гирю',
    'retracting the shoulder blade of the working side': 'втягивая лопатку рабочей стороны',
    'as you flex the elbow': 'когда сгибаете локоть',
    'pulling it to your side': 'тяча ее к боку',
    'Then lower the kettlebell to the floor': 'Затем опустите гирю на пол',
    'and begin the kettlebell in the opposite hand': 'и начните с гири в противоположной руке',
    'Repeat several times': 'Повторите несколько раз',
    'Use a sturdy object': 'Используйте прочный объект',
    'like a squat rack': 'как стойка для приседаний',
    'to hold yourself': 'чтобы удержаться',
    'Raise the right leg': 'Поднимите правую ногу',
    'in the air': 'в воздух',
    'and perform a circular motion': 'и выполните круговое движение',
    'with the big toe': 'большим пальцем',
    'Pretend that you are drawing a big circle': 'Представьте что рисуете большой круг',
    'with it': 'им',
    'One circle equals 1 repetition': 'Один круг равен одному повторению',
    'Breathe normally': 'Дышите нормально',
    'as you perform the movement': 'когда выполняете движение',
    'When you are done with the right foot': 'Когда закончите с правой ногой',
    'then repeat with the left leg': 'затем повторите с левой ногой',
    'From a lying position': 'Из положения лежа',
    'bend your knees': 'согните колени',
    'and keep your feet on the floor': 'и держите стопы на полу',
    'Place your ankle': 'Поместите лодыжку',
    'of one foot': 'одной стопы',
    'on your opposite knee': 'на противоположное колено',
    'Grasp the thigh': 'Возьмите бедро',
    'or knee': 'или колено',
    'of the bottom leg': 'нижней ноги',
    'and pull both of your legs into the chest': 'и подтяните обе ноги к груди',
    'Relax your neck and shoulders': 'Расслабьте шею и плечи',
    'Hold for 10-20 seconds': 'Удерживайте 10-20 секунд',
    'and then switch sides': 'и затем смените стороны',
    'Begin seated on the floor': 'Начните сидя на полу',
    'with your legs bent': 'с согнутыми ногами',
    'and your feet on the floor': 'и стопами на полу',
    'Using a Muscle Roller': 'Используя мышечный ролик',
    'or a rolling pin': 'или скалку',
    'apply pressure to the muscles': 'приложите давление к мышцам',
    'on the outside of your shins': 'снаружи голеней',
    'Work from just below the knee': 'Работайте сразу ниже колена',
    'to above the ankle': 'до выше лодыжки',
    'pausing at points of tension': 'задерживаясь в точках напряжения',
    'for 10-30 seconds': 'на 10-30 секунд',
    'Repeat on the other leg': 'Повторите на другой ноге',
    'Place the barbell on the floor': 'Поместите штангу на пол',
    'in front of the head of an incline bench': 'перед головой наклонной скамьи',
    'Lay on the bench face down': 'Лягте на скамью лицом вниз',
    'With a pronated grip': 'Пронированным хватом',
    'pick the barbell up from the floor': 'поднимите штангу с пола',
    'Bend the elbows': 'Согните локти',
    'performing a reverse curl': 'выполняя обратное сгибание',
    'to bring the barbell near your chest': 'чтобы поднести штангу к груди',
    'This will be the starting position': 'Это будет исходное положение',
    'To begin': 'Для начала',
    'press the barbell out': 'жмите штангу наружу',
    'in front of your head': 'перед головой',
    'by extending your elbows': 'разгибая локти',
    'Keep your arms parallel to the floor': 'Держите руки параллельно полу',
    'throughout the movement': 'на протяжении всего движения',
    'Return to the starting position': 'Вернитесь в исходное положение',
    'and repeat': 'и повторите',
    'to complete the set': 'чтобы завершить подход',
    'Lie back on a flat bench': 'Лягте на спину на ровную скамью',
    'Using a medium width grip': 'Используя хват средней ширины',
    'lift the barbell from the rack': 'поднимите штангу со стойки',
    'and hold it straight over you': 'и удерживайте её прямо над собой',
    'with your arms locked': 'с выпрямленными руками',
    'This will be the starting position': 'Это будет исходное положение',
    'From the starting position': 'Из исходного положения',
    'breathe in': 'вдохните',
    'and begin coming down slowly': 'и начните медленно опускаться',
    'until the barbell touches your middle chest': 'пока штанга не коснется средней части груди',
    'After a brief pause': 'После краткой паузы',
    'push the barbell back to the starting position': 'жмите штангу обратно в исходное положение',
    'as you exhale': 'когда выдыхаете',
    'Focus on pushing the barbell': 'Сосредоточьтесь на жиме штанги',
    'using your chest muscles': 'используя мышцы груди',
    'Lock your arms': 'Выпрямите руки',
    'and squeeze your chest': 'и сожмите грудь',
    'in the contracted position': 'в сокращенном положении',
    'hold for a second': 'удерживайте секунду',
    'and then start coming down slowly again': 'и затем начните снова медленно опускаться',
    'Ideally': 'В идеале',
    'lowering the weight should take': 'опускание веса должно занимать',
    'about twice as long': 'примерно в два раза дольше',
    'as raising it': 'чем подъем',
    'Repeat the movement': 'Повторите движение',
    'for the prescribed amount of repetitions': 'предписанное количество раз',
    'When you are done': 'Когда закончите',
    'place the barbell back in the rack': 'поместите штангу обратно в стойку',
}

def translate_instruction(text):
    """Переводит инструкцию по словарю"""
    result = text
    for eng, rus in sorted(translation_map.items(), key=lambda x: len(x[0]), reverse=True):
        if eng.lower() in result.lower():
            result = result.replace(eng, rus)
    return result

def main():
    print("Загрузка exercises.json...")
    with open('feature/workout/src/main/assets/exercises.json', 'r', encoding='utf-8') as f:
        exercises = json.load(f)

    print(f"Всего упражнений: {len(exercises)}")

    for i, exercise in enumerate(exercises):
        if 'instructions' in exercise and exercise['instructions']:
            exercise['instructions'] = [translate_instruction(inst) for inst in exercise['instructions']]

        if (i + 1) % 100 == 0:
            print(f"Обработано {i + 1} упражнений...")

    print(f"Обработано {len(exercises)} упражнений")

    print("Сохранение переведенного файла...")
    with open('feature/workout/src/main/assets/exercises.json', 'w', encoding='utf-8') as f:
        json.dump(exercises, f, ensure_ascii=False, indent=2)

    print("Готово!")

if __name__ == '__main__':
    main()
