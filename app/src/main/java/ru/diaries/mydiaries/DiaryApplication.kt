package ru.diaries.mydiaries

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration as OsmConfig
import ru.diaries.mydiaries.data.local.DiaryDatabase
import ru.diaries.mydiaries.receiver.MidnightReceiver
import ru.diaries.mydiaries.worker.StepResetWorker
import ru.diaries.mydiaries.worker.TrackFinalizationWorker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class DiaryApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var database: DiaryDatabase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var initializeAchievementsUseCase: ru.diaries.mydiaries.domain.usecase.InitializeAchievementsUseCase

    override fun onCreate() {
        super.onCreate()

        // Initialize database eagerly
        CoroutineScope(Dispatchers.IO).launch {
            database.openHelper.writableDatabase
            // Initialize achievements and user profile
            initializeAchievementsUseCase()
        }

        // Initialize osmdroid
        OsmConfig.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        OsmConfig.getInstance().userAgentValue = packageName

        // Schedule step reset worker to run exactly at midnight
        scheduleMidnightWorkers()
    }

    /**
     * Schedules workers to run exactly at midnight each day.
     * This ensures step counter is reset at the exact start of a new calendar day.
     */
    private fun scheduleMidnightWorkers() {
        val now = LocalDateTime.now()
        val midnight = LocalDate.now().plusDays(1).atStartOfDay()
        val initialDelay = java.time.Duration.between(now, midnight).toMinutes()

        // Step reset worker - runs at midnight to finalize yesterday's steps
        val stepResetRequest = PeriodicWorkRequestBuilder<StepResetWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            StepResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            stepResetRequest
        )

        // Track finalization worker - runs at midnight as well
        val finalizationRequest = PeriodicWorkRequestBuilder<TrackFinalizationWorker>(
            1, TimeUnit.DAYS
        ).setInitialDelay(initialDelay, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            TrackFinalizationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            finalizationRequest
        )

        // Schedule AlarmManager to trigger midnight receiver for immediate step counter refresh
        scheduleMidnightAlarm()
    }

    /**
     * Schedules an alarm to fire at exactly midnight.
     * This ensures StepCounterService detects the day change immediately.
     */
    private fun scheduleMidnightAlarm() {
        val alarmManager = getSystemService(AlarmManager::class.java)
        val midnight = LocalDate.now().plusDays(1).atStartOfDay()
        val midnightMillis = midnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(this, MidnightReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set repeating alarm for midnight every day
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            midnightMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
