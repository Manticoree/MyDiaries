package ru.diaries.mydiaries.data.local.mapper

import ru.diaries.mydiaries.data.local.entity.DiaryEntryEntity
import ru.diaries.mydiaries.data.model.DiaryEntry
import ru.diaries.mydiaries.data.model.Photo
import java.time.LocalDate

fun DiaryEntryEntity.toDomain(photos: List<Photo> = emptyList()): DiaryEntry {
    return DiaryEntry(
        id = id,
        title = title,
        content = content,
        date = LocalDate.ofEpochDay(date),
        photos = photos
    )
}

fun DiaryEntry.toEntity(): DiaryEntryEntity {
    return DiaryEntryEntity(
        id = id,
        title = title,
        content = content,
        date = date.toEpochDay()
    )
}

fun List<DiaryEntryEntity>.toDomainList(): List<DiaryEntry> {
    return map { it.toDomain() }
}
