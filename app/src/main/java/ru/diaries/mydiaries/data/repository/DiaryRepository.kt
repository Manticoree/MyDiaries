package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.DiaryDao
import ru.diaries.mydiaries.data.local.dao.PhotoDao
import ru.diaries.mydiaries.data.local.mapper.toDomain
import ru.diaries.mydiaries.data.local.mapper.toDomainList
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.data.local.mapper.toEntityList
import ru.diaries.mydiaries.data.model.DiaryEntry
import javax.inject.Inject
import javax.inject.Singleton

interface DiaryRepository {
    fun getEntries(): Flow<List<DiaryEntry>>
    suspend fun getEntry(id: String): DiaryEntry?
    suspend fun saveEntry(entry: DiaryEntry)
    suspend fun deleteEntry(id: String)
}

@Singleton
class RoomDiaryRepository @Inject constructor(
    private val diaryDao: DiaryDao,
    private val photoDao: PhotoDao
) : DiaryRepository {

    override fun getEntries(): Flow<List<DiaryEntry>> {
        return diaryDao.getAllEntries()
            .map { entities ->
                entities.map { entity ->
                    val photos = photoDao.getPhotosForEntrySync(entity.id)
                        .toDomainList()
                    entity.toDomain(photos)
                }
            }
    }

    override suspend fun getEntry(id: String): DiaryEntry? {
        val entity = diaryDao.getEntryById(id) ?: return null
        val photos = photoDao.getPhotosForEntrySync(id).toDomainList()
        return entity.toDomain(photos)
    }

    override suspend fun saveEntry(entry: DiaryEntry) {
        val entity = entry.toEntity()
        diaryDao.insertEntry(entity)

        photoDao.deletePhotosForEntry(entry.id)
        if (entry.photos.isNotEmpty()) {
            val photoEntities = entry.photos.toEntityList(entry.id)
            photoDao.insertPhotos(photoEntities)
        }
    }

    override suspend fun deleteEntry(id: String) {
        diaryDao.deleteEntry(id)
    }
}
