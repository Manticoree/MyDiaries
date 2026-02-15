package ru.diaries.mydiaries.feature.workout.data.local

import ru.diaries.mydiaries.feature.workout.data.api.WgerCategory
import ru.diaries.mydiaries.feature.workout.data.api.WgerCategoryRef
import ru.diaries.mydiaries.feature.workout.data.api.WgerEquipment
import ru.diaries.mydiaries.feature.workout.data.api.WgerExerciseInfo
import ru.diaries.mydiaries.feature.workout.data.api.WgerImage
import ru.diaries.mydiaries.feature.workout.data.api.WgerMuscle

private const val IMAGE_BASE_URL =
    "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/"

private fun String.toStableId(): Int = hashCode()

private fun String.toMuscle(): WgerMuscle = WgerMuscle(
    id = toStableId(),
    name = replaceFirstChar { it.uppercase() },
    nameEn = this,
    imageUrlMain = null,
    imageUrlSecondary = null
)

private fun String.toEquipment(): WgerEquipment = WgerEquipment(
    id = toStableId(),
    name = replaceFirstChar { it.uppercase() }
)

fun ExerciseDbJson.toWgerExerciseInfo(): WgerExerciseInfo = WgerExerciseInfo(
    id = id.toStableId(),
    name = name,
    description = instructions.joinToString("\n\n").ifEmpty { null },
    category = category?.let { WgerCategoryRef(id = it.toStableId(), name = it.replaceFirstChar { c -> c.uppercase() }) },
    muscles = primaryMuscles.map { it.toMuscle() },
    musclesSecondary = secondaryMuscles.map { it.toMuscle() },
    images = images.map { path ->
        WgerImage(
            id = path.toStableId(),
            image = "$IMAGE_BASE_URL$path",
            isMain = images.indexOf(path) == 0
        )
    },
    equipment = listOfNotNull(equipment?.toEquipment())
)

fun List<ExerciseDbJson>.extractCategories(): List<WgerCategory> =
    mapNotNull { it.category }
        .distinct()
        .sorted()
        .map { WgerCategory(id = it.toStableId(), name = it.replaceFirstChar { c -> c.uppercase() }) }
