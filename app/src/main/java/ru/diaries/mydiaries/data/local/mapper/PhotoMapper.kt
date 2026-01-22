package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.PhotoEntity
import ru.diaries.mydiaries.data.model.Photo

fun PhotoEntity.toDomain(): Photo {
    return Photo(
        id = id,
        uri = uri,
        position = position
    )
}

fun Photo.toEntity(entryId: String): PhotoEntity {
    return PhotoEntity(
        id = id,
        entryId = entryId,
        uri = uri,
        position = position
    )
}

fun List<PhotoEntity>.toDomainList(): List<Photo> {
    return map { it.toDomain() }
}

fun List<Photo>.toEntityList(entryId: String): List<PhotoEntity> {
    return map { it.toEntity(entryId) }
}
