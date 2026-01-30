package ru.diaries.mydiaries.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate

@HiltWorker
class TrackFinalizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val trackRepository: TrackRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val yesterday = LocalDate.now().minusDays(1)
            trackRepository.finalizeTrack(yesterday)
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "track_finalization_daily"
    }
}
