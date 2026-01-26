package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.VideoEntity
import ru.diaries.mydiaries.feature.video.data.model.Video
import java.time.LocalDate

fun VideoEntity.toDomain(): Video {
    return Video(
        id = id,
        uri = uri,
        thumbnailUri = thumbnailUri,
        durationMs = durationMs,
        title = title,
        date = LocalDate.parse(date),
        createdAt = createdAt
    )
}

fun Video.toEntity(): VideoEntity {
    return VideoEntity(
        id = id,
        uri = uri,
        thumbnailUri = thumbnailUri,
        durationMs = durationMs,
        title = title,
        date = date.toString(),
        createdAt = createdAt
    )
}

fun List<VideoEntity>.toDomainList(): List<Video> = map { it.toDomain() }
