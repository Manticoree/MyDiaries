package ru.diaries.mydiaries.feature.workout.data.api

import com.google.gson.annotations.SerializedName

data class WgerCategory(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class WgerExerciseInfo(
    val id: Int,
    val name: String,
    val description: String?,
    val category: WgerCategoryRef?,
    val muscles: List<WgerMuscle>,
    val musclesSecondary: List<WgerMuscle>,
    val images: List<WgerImage>,
    val equipment: List<WgerEquipment>
)

data class WgerCategoryRef(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class WgerMuscle(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("name_en") val nameEn: String?,
    @SerializedName("image_url_main") val imageUrlMain: String?,
    @SerializedName("image_url_secondary") val imageUrlSecondary: String?
)

data class WgerImage(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String,
    @SerializedName("is_main") val isMain: Boolean
)

data class WgerEquipment(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
