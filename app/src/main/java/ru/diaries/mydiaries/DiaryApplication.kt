package ru.diaries.mydiaries

import android.app.Application
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
import ru.diaries.mydiaries.worker.TrackFinalizationWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class DiaryApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var database: DiaryDatabase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize database eagerly
        CoroutineScope(Dispatchers.IO).launch {
            database.openHelper.writableDatabase
        }

        // Initialize osmdroid
        OsmConfig.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        OsmConfig.getInstance().userAgentValue = packageName

        // Schedule daily track finalization worker
        val finalizationRequest = PeriodicWorkRequestBuilder<TrackFinalizationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            TrackFinalizationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            finalizationRequest
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
