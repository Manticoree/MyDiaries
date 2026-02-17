package ru.diaries.mydiaries.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.diaries.mydiaries.service.StepCounterService

/**
 * Broadcast receiver that triggers at midnight to ensure StepCounterService
 * detects the day change and resets steps even if the user is inactive.
 */
class MidnightReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        // Check if step counter is running and trigger sensor update
        if (StepCounterService.isRunning.value) {
            // Send intent to trigger step counter refresh
            val refreshIntent = Intent(context, StepCounterService::class.java).apply {
                action = StepCounterService.ACTION_REFRESH
            }
            context.startService(refreshIntent)
        }
    }
}
