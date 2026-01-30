package ru.diaries.mydiaries.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var trackRepository: TrackRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var initialStepCount: Int = -1
    private var lastPersistedSteps: Int = -1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startCounting()
            ACTION_STOP -> stopCounting()
        }
        return START_STICKY
    }

    private fun startCounting() {
        if (_isRunning.value) return

        val notification = buildNotification(0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        _isRunning.value = true

        // Load saved steps for today (in case service was restarted)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_DATE, "")
        val todayDate = LocalDate.now().toString()
        if (savedDate == todayDate) {
            val savedSteps = prefs.getInt(KEY_STEPS, 0)
            val savedInitial = prefs.getInt(KEY_INITIAL_STEPS, -1)
            _todaySteps.value = savedSteps
            initialStepCount = savedInitial
        } else {
            // New day — reset
            initialStepCount = -1
            _todaySteps.value = 0
            prefs.edit()
                .putString(KEY_DATE, todayDate)
                .putInt(KEY_STEPS, 0)
                .putInt(KEY_INITIAL_STEPS, -1)
                .apply()
        }

        if (stepCounterSensor != null) {
            sensorManager.registerListener(
                this,
                stepCounterSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    private fun stopCounting() {
        _isRunning.value = false
        sensorManager.unregisterListener(this)
        persistStepsToDb(_todaySteps.value)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        val totalStepsSinceBoot = event.values[0].toInt()

        if (initialStepCount == -1) {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val savedInitial = prefs.getInt(KEY_INITIAL_STEPS, -1)
            if (savedInitial != -1 && prefs.getString(KEY_DATE, "") == LocalDate.now().toString()) {
                initialStepCount = savedInitial
            } else {
                initialStepCount = totalStepsSinceBoot
                prefs.edit()
                    .putInt(KEY_INITIAL_STEPS, initialStepCount)
                    .putString(KEY_DATE, LocalDate.now().toString())
                    .apply()
            }
        }

        val todayStepCount = totalStepsSinceBoot - initialStepCount
        _todaySteps.value = todayStepCount

        // Persist to SharedPreferences
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putInt(KEY_STEPS, todayStepCount)
            .apply()

        // Persist to DB every 50 steps to avoid excessive writes
        if (todayStepCount - lastPersistedSteps >= PERSIST_INTERVAL) {
            persistStepsToDb(todayStepCount)
            lastPersistedSteps = todayStepCount
        }

        // Update notification
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(todayStepCount))
    }

    private fun persistStepsToDb(steps: Int) {
        serviceScope.launch {
            try {
                trackRepository.updateSteps(LocalDate.now(), steps)
            } catch (_: Exception) {
                // Silently handle to keep service running
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Step counter running"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(steps: Int): Notification {
        val stopIntent = Intent(this, StepCounterService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.step_counter_notification_title))
            .setContentText(getString(R.string.step_counter_notification_text, steps))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(0, getString(R.string.stop_step_counter), stopPendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        _isRunning.value = false
        sensorManager.unregisterListener(this)
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START_STEP_COUNTER"
        const val ACTION_STOP = "ACTION_STOP_STEP_COUNTER"

        private const val CHANNEL_ID = "step_counter_channel"
        private const val CHANNEL_NAME = "Step Counter"
        private const val NOTIFICATION_ID = 1002

        private const val PREFS_NAME = "step_counter_prefs"
        private const val KEY_STEPS = "today_steps"
        private const val KEY_DATE = "step_date"
        private const val KEY_INITIAL_STEPS = "initial_step_count"
        private const val PERSIST_INTERVAL = 50

        private val _todaySteps = MutableStateFlow(0)
        val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

        fun start(context: Context) {
            val intent = Intent(context, StepCounterService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, StepCounterService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
