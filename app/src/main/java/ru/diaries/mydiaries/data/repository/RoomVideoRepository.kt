package ru.diaries.mydiaries.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.diaries.mydiaries.data.local.dao.VideoDao
import ru.diaries.mydiaries.data.local.mapper.toDomainList
import ru.diaries.mydiaries.data.local.mapper.toEntity
import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import javax.inject.Inject

class RoomVideoRepository @Inject constructor(
    private val videoDao: VideoDao
) : VideoRepository {

    override fun getAllVideos(): Flow<List<Video>> {
        return videoDao.getAllVideos().map { it.toDomainList() }
    }

    override suspend fun saveVideo(video: Video) {
        videoDao.insertVideo(video.toEntity())
    }

    override suspend fun deleteVideo(id: String) {
        videoDao.deleteVideo(id)
    }
}
