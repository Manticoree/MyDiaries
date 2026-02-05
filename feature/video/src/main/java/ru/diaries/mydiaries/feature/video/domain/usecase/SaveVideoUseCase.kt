package ru.diaries.mydiaries.feature.video.domain.usecase

import ru.diaries.mydiaries.feature.video.data.model.Video
import ru.diaries.mydiaries.feature.video.data.repository.VideoRepository
import javax.inject.Inject

class SaveVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(video: Video) {
        videoRepository.saveVideo(video)
    }
}
