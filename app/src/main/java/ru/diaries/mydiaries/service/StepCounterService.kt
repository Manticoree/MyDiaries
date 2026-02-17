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
import org.json.JSONObject
import ru.diaries.mydiaries.MainActivity
import ru.diaries.mydiaries.R
import ru.diaries.mydiaries.feature.track.data.repository.TrackRepository
import ru.diaries.mydiaries.domain.usecase.CheckAndUnlockAchievementsUseCase
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var trackRepository: TrackRepository

    @Inject
    lateinit var checkAchievementsUseCase: CheckAndUnlockAchievementsUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var initialStepCount: Int = -1
    private var lastPersistedSteps: Int = -1
    private var currentDate: LocalDate = LocalDate.now()
    private var lastHourlySteps: Int = 0
    private var lastHourlyHour: Int = -1

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
            ACTION_REFRESH -> refreshDayChange()
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
        currentDate = LocalDate.now()
        val todayDate = currentDate.toString()
        if (savedDate == todayDate) {
            val savedSteps = prefs.getInt(KEY_STEPS, 0)
            val savedInitial = prefs.getInt(KEY_INITIAL_STEPS, -1)
            _todaySteps.value = savedSteps
            initialStepCount = savedInitial

            // Load hourly data
            val hourlyJson = prefs.getString(KEY_HOURLY_STEPS, null) ?: "{}"
            _hourlySteps.value = parseHourlySteps(hourlyJson)
            lastHourlySteps = prefs.getInt(KEY_LAST_HOURLY_STEPS, 0)
            lastHourlyHour = prefs.getInt(KEY_LAST_HOURLY_HOUR, -1)
        } else {
            // New day — reset
            initialStepCount = -1
            _todaySteps.value = 0
            lastPersistedSteps = -1
            _hourlySteps.value = emptyMap()
            lastHourlySteps = 0
            lastHourlyHour = -1
            prefs.edit()
                .putString(KEY_DATE, todayDate)
                .putInt(KEY_STEPS, 0)
                .putInt(KEY_INITIAL_STEPS, -1)
                .putString(KEY_HOURLY_STEPS, "{}")
                .putInt(KEY_LAST_HOURLY_STEPS, 0)
                .putInt(KEY_LAST_HOURLY_HOUR, -1)
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

    /**
     * Triggered at midnight to force day change detection.
     * This ensures steps reset exactly at midnight even if the user is inactive.
     */
    private fun refreshDayChange() {
        if (stepCounterSensor != null) {
            // Force a sensor event check by checking current state
            val now = LocalDate.now()
            val totalStepsSinceBoot = initialStepCount

            if (now != currentDate) {
                handleDayChange(now, totalStepsSinceBoot)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_STEP_COUNTER) return

        val totalStepsSinceBoot = event.values[0].toInt()
        val now = LocalDate.now()

        // Check if day has changed (midnight passed)
        if (now != currentDate) {
            handleDayChange(now, totalStepsSinceBoot)
        }

        if (initialStepCount == -1) {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val savedInitial = prefs.getInt(KEY_INITIAL_STEPS, -1)
            if (savedInitial != -1 && prefs.getString(KEY_DATE, "") == now.toString()) {
                initialStepCount = savedInitial
            } else {
                initialStepCount = totalStepsSinceBoot
                prefs.edit()
                    .putInt(KEY_INITIAL_STEPS, initialStepCount)
                    .putString(KEY_DATE, now.toString())
                    .apply()
            }
        }

        val todayStepCount = totalStepsSinceBoot - initialStepCount
        _todaySteps.value = todayStepCount

        // Update hourly breakdown
        updateHourlySteps(todayStepCount)

        // Persist to SharedPreferences
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putInt(KEY_STEPS, todayStepCount)
            .apply()

        // Persist to DB every 50 steps to avoid excessive writes
        if (todayStepCount - lastPersistedSteps >= PERSIST_INTERVAL) {
            persistStepsToDb(todayStepCount)
            lastPersistedSteps = todayStepCount

            // Check and unlock achievements
            serviceScope.launch {
                try {
                    checkAchievementsUseCase()
                } catch (_: Exception) {
                    // Silently handle
                }
            }
        }

        // Update notification
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(todayStepCount))
    }

    /**
     * Handles day change by saving previous day's steps and resetting for the new day.
     * This ensures steps are counted strictly within each calendar day.
     */
    private fun handleDayChange(newDate: LocalDate, totalStepsSinceBoot: Int) {
        // Save final steps for the previous day
        val previousDaySteps = _todaySteps.value
        if (previousDaySteps > 0) {
            serviceScope.launch {
                try {
                    trackRepository.updateSteps(currentDate, previousDaySteps)
                } catch (_: Exception) {
                    // Silently handle
                }
            }
        }

        // Reset for the new day
        currentDate = newDate
        initialStepCount = totalStepsSinceBoot
        _todaySteps.value = 0
        lastPersistedSteps = -1
        _hourlySteps.value = emptyMap()
        lastHourlySteps = 0
        lastHourlyHour = -1

        // Update SharedPreferences for the new day
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putString(KEY_DATE, newDate.toString())
            .putInt(KEY_STEPS, 0)
            .putInt(KEY_INITIAL_STEPS, initialStepCount)
            .putString(KEY_HOURLY_STEPS, "{}")
            .putInt(KEY_LAST_HOURLY_STEPS, 0)
            .putInt(KEY_LAST_HOURLY_HOUR, -1)
            .apply()

        // Update notification to show reset
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(0))
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

    private fun updateHourlySteps(todayStepCount: Int) {
        val currentHour = LocalTime.now().hour
        val currentMap = _hourlySteps.value.toMutableMap()

        if (lastHourlyHour == -1) {
            currentMap[currentHour] = todayStepCount
        } else {
            val delta = todayStepCount - lastHourlySteps
            if (delta > 0) {
                currentMap[currentHour] = (currentMap[currentHour] ?: 0) + delta
            }
        }

        lastHourlySteps = todayStepCount
        lastHourlyHour = currentHour
        _hourlySteps.value = currentMap

        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
            .putString(KEY_HOURLY_STEPS, hourlyStepsToJson(currentMap))
            .putInt(KEY_LAST_HOURLY_STEPS, todayStepCount)
            .putInt(KEY_LAST_HOURLY_HOUR, currentHour)
            .apply()
    }

    private fun parseHourlySteps(json: String): Map<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        try {
            val obj = JSONObject(json)
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key.toInt()] = obj.getInt(key)
            }
        } catch (_: Exception) {
            // Silently handle malformed JSON
        }
        return map
    }

    private fun hourlyStepsToJson(map: Map<Int, Int>): String {
        val obj = JSONObject()
        map.forEach { (hour, steps) -> obj.put(hour.toString(), steps) }
        return obj.toString()
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
        val numberFormat = java.text.NumberFormat.getNumberInstance(java.util.Locale("ru", "RU"))
        val stepGoal = STEP_GOAL
        val percentage = ((steps.toFloat() / stepGoal) * 100).toInt().coerceIn(0, 100)

        val stopIntent = Intent(this, StepCounterService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openChartIntent = Intent(this, MainActivity::class.java).apply {
            action = ACTION_SHOW_STEPS_CHART
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openChartPendingIntent = PendingIntent.getActivity(
            this, 2, openChartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Collapsed custom view
        val collapsedView = android.widget.RemoteViews(packageName, R.layout.notification_step_counter)
        collapsedView.setTextViewText(
            R.id.notification_title,
            "\uD83D\uDEB6 ${getString(R.string.step_counter_notification_title)}"
        )
        collapsedView.setTextViewText(R.id.notification_percentage, "$percentage%")
        collapsedView.setProgressBar(R.id.notification_progress, stepGoal, steps, false)
        collapsedView.setTextViewText(
            R.id.notification_step_text,
            "${numberFormat.format(steps)} / ${numberFormat.format(stepGoal)} ${getString(R.string.unit_steps)}"
        )

        // Expanded custom view
        val expandedView = android.widget.RemoteViews(packageName, R.layout.notification_step_counter_expanded)
        expandedView.setTextViewText(
            R.id.notification_title,
            "\uD83D\uDEB6 ${getString(R.string.step_counter_notification_title)}"
        )
        expandedView.setTextViewText(R.id.notification_step_count, numberFormat.format(steps))
        expandedView.setTextViewText(
            R.id.notification_subtitle,
            getString(R.string.notification_steps_today_subtitle)
        )
        expandedView.setProgressBar(R.id.notification_progress, stepGoal, steps, false)
        expandedView.setTextViewText(R.id.notification_percentage, "$percentage%")
        expandedView.setTextViewText(
            R.id.notification_goal_text,
            getString(R.string.notification_step_goal, numberFormat.format(stepGoal))
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.step_counter_notification_title))
            .setContentText(getString(R.string.step_counter_notification_text, steps))
            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openChartPendingIntent)
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
        const val ACTION_SHOW_STEPS_CHART = "ACTION_SHOW_STEPS_CHART"
        const val ACTION_REFRESH = "ACTION_REFRESH_STEP_COUNTER"

        private const val CHANNEL_ID = "step_counter_channel"
        private const val CHANNEL_NAME = "Step Counter"
        private const val NOTIFICATION_ID = 1002

        private const val PREFS_NAME = "step_counter_prefs"
        private const val KEY_STEPS = "today_steps"
        private const val KEY_DATE = "step_date"
        private const val KEY_INITIAL_STEPS = "initial_step_count"
        private const val PERSIST_INTERVAL = 50
        private const val STEP_GOAL = 10_000
        private const val KEY_HOURLY_STEPS = "hourly_steps"
        private const val KEY_LAST_HOURLY_STEPS = "last_hourly_steps"
        private const val KEY_LAST_HOURLY_HOUR = "last_hourly_hour"

        private val _todaySteps = MutableStateFlow(0)
        val todaySteps: StateFlow<Int> = _todaySteps.asStateFlow()

        private val _hourlySteps = MutableStateFlow<Map<Int, Int>>(emptyMap())
        val hourlySteps: StateFlow<Map<Int, Int>> = _hourlySteps.asStateFlow()

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
