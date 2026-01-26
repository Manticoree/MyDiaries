package ru.diaries.mydiaries.feature.video.data.repository

import kotlinx.coroutines.flow.Flow
import ru.diaries.mydiaries.feature.video.data.model.Video

interface VideoRepository {
    fun getAllVideos(): Flow<List<Video>>
    suspend fun saveVideo(video: Video)
    suspend fun deleteVideo(id: String)
}
