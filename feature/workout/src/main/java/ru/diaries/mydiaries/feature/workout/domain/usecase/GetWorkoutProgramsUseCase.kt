package ru.diaries.mydiaries.feature.workout.domain.usecase

import ru.diaries.mydiaries.feature.workout.data.model.ExerciseType
import ru.diaries.mydiaries.feature.workout.data.model.ProgramDay
import ru.diaries.mydiaries.feature.workout.data.model.TemplateExercise
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutProgram
import ru.diaries.mydiaries.feature.workout.data.model.WorkoutType
import javax.inject.Inject

class GetWorkoutProgramsUseCase @Inject constructor() {

    operator fun invoke(): List<WorkoutProgram> = listOf(
        WorkoutProgram(
            id = "ppl",
            name = "Push / Pull / Legs",
            description = "Классическая программа PPL: 3 дня на разные группы мышц",
            type = WorkoutType.STRENGTH,
            days = listOf(
                ProgramDay(
                    name = "Push",
                    templateExercises = listOf(
                        TemplateExercise("Жим лёжа", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Жим стоя (OHP)", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Отжимания на брусьях", defaultSets = 3, defaultReps = 12),
                        TemplateExercise("Разводки гантелей", defaultSets = 3, defaultReps = 15)
                    )
                ),
                ProgramDay(
                    name = "Pull",
                    templateExercises = listOf(
                        TemplateExercise("Становая тяга", defaultSets = 4, defaultReps = 6),
                        TemplateExercise("Тяга штанги в наклоне", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Подтягивания", defaultSets = 3, defaultReps = 8),
                        TemplateExercise("Сгибание на бицепс", defaultSets = 3, defaultReps = 12)
                    )
                ),
                ProgramDay(
                    name = "Legs",
                    templateExercises = listOf(
                        TemplateExercise("Приседания", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Жим ногами", defaultSets = 3, defaultReps = 12),
                        TemplateExercise("Выпады", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Подъём на носки", defaultSets = 4, defaultReps = 15)
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "fullbody",
            name = "Full Body",
            description = "Тренировка на всё тело за одно занятие",
            type = WorkoutType.STRENGTH,
            days = listOf(
                ProgramDay(
                    name = "Full Body",
                    templateExercises = listOf(
                        TemplateExercise("Приседания", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Жим лёжа", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Тяга штанги в наклоне", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Жим стоя (OHP)", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Становая тяга", defaultSets = 3, defaultReps = 6)
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "upper_lower",
            name = "Upper / Lower",
            description = "Разделение на верх и низ тела",
            type = WorkoutType.STRENGTH,
            days = listOf(
                ProgramDay(
                    name = "Upper",
                    templateExercises = listOf(
                        TemplateExercise("Жим лёжа", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Тяга штанги в наклоне", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Жим стоя (OHP)", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Подтягивания", defaultSets = 3, defaultReps = 8),
                        TemplateExercise("Сгибание на бицепс", defaultSets = 3, defaultReps = 12),
                        TemplateExercise("Разгибание на трицепс", defaultSets = 3, defaultReps = 12)
                    )
                ),
                ProgramDay(
                    name = "Lower",
                    templateExercises = listOf(
                        TemplateExercise("Приседания", defaultSets = 4, defaultReps = 8),
                        TemplateExercise("Становая тяга", defaultSets = 4, defaultReps = 6),
                        TemplateExercise("Жим ногами", defaultSets = 3, defaultReps = 12),
                        TemplateExercise("Выпады", defaultSets = 3, defaultReps = 10),
                        TemplateExercise("Подъём на носки", defaultSets = 4, defaultReps = 15)
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "cardio_mix",
            name = "Cardio Mix",
            description = "Кардио-программа: бег, велосипед, HIIT, скакалка",
            type = WorkoutType.CARDIO,
            days = listOf(
                ProgramDay(
                    name = "Cardio",
                    templateExercises = listOf(
                        TemplateExercise(
                            "Бег",
                            type = ExerciseType.CARDIO,
                            defaultSets = 1,
                            defaultReps = 0,
                            defaultDurationSeconds = 1800,
                            defaultDistanceKm = 5.0
                        ),
                        TemplateExercise(
                            "Велосипед",
                            type = ExerciseType.CARDIO,
                            defaultSets = 1,
                            defaultReps = 0,
                            defaultDurationSeconds = 1200,
                            defaultDistanceKm = 8.0
                        ),
                        TemplateExercise(
                            "HIIT",
                            type = ExerciseType.CARDIO,
                            defaultSets = 1,
                            defaultReps = 0,
                            defaultDurationSeconds = 900
                        ),
                        TemplateExercise(
                            "Скакалка",
                            type = ExerciseType.CARDIO,
                            defaultSets = 1,
                            defaultReps = 0,
                            defaultDurationSeconds = 600
                        )
                    )
                )
            )
        )
    )
}
