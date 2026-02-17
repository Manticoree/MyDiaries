package ru.diaries.mydiaries.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate

/**
 * Worker that runs at midnight to finalize the previous day's step count.
 * This ensures steps are counted strictly within calendar days and reset exactly at midnight.
 */
@HiltWorker
class StepResetWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val trackRepository: TrackRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Finalize steps for yesterday (which ended at midnight)
            val yesterday = LocalDate.now().minusDays(1)
            trackRepository.finalizeTrack(yesterday)

            // Note: StepCounterService will handle the actual reset when it detects the date change
            // through its onSensorChanged callback

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "step_reset_midnight"
    }
}
