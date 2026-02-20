package ru.diaries.mydiaries.analytics

import android.util.Log

/**
 * Centralized analytics events manager
 * Firebase Analytics events tracking
 */
object EventsManager {

    // Event Names
    private const val EVENT_ONBOARDING_COMPLETED = "onboarding_completed"
    private const val EVENT_ONBOARDING_SKIPPED = "onboarding_skipped"
    private const val EVENT_ONBOARDING_PAGE_VIEW = "onboarding_page_view"

    private const val EVENT_PERMISSIONS_GRANTED = "permissions_granted"
    private const val EVENT_PERMISSIONS_DENIED = "permissions_denied"
    private const val EVENT_PERMISSIONS_SKIPPED = "permissions_skipped"

    // Event Parameters
    private const val PARAM_PAGE_INDEX = "page_index"
    private const val PARAM_TOTAL_PAGES = "total_pages"
    private const val PARAM_PERMISSION_TYPE = "permission_type"
    private const val PARAM_DURATION_SECONDS = "duration_seconds"

    /**
     * Log onboarding events
     */
    fun logOnboardingPageView(pageIndex: Int, totalPages: Int) {
        logEvent(EVENT_ONBOARDING_PAGE_VIEW, mapOf(
            PARAM_PAGE_INDEX to pageIndex,
            PARAM_TOTAL_PAGES to totalPages
        ))
        Log.d("EventsManager", "Analytics: Onboarding page $pageIndex of $totalPages viewed")
    }

    fun logOnboardingCompleted(durationSeconds: Long = 0) {
        val params: Map<String, Any> = if (durationSeconds > 0) {
            mapOf(PARAM_DURATION_SECONDS to durationSeconds)
        } else {
            emptyMap()
        }
        logEvent(EVENT_ONBOARDING_COMPLETED, params)
        Log.d("EventsManager", "Analytics: Onboarding completed")
    }

    fun logOnboardingSkipped(pageIndex: Int = 0) {
        logEvent(EVENT_ONBOARDING_SKIPPED, mapOf(PARAM_PAGE_INDEX to pageIndex))
        Log.d("EventsManager", "Analytics: Onboarding skipped at page $pageIndex")
    }

    /**
     * Log permission events
     */
    fun logPermissionGranted(permissionType: String) {
        logEvent(EVENT_PERMISSIONS_GRANTED, mapOf(PARAM_PERMISSION_TYPE to permissionType))
        Log.d("EventsManager", "Analytics: Permission granted: $permissionType")
    }

    fun logPermissionDenied(permissionType: String) {
        logEvent(EVENT_PERMISSIONS_DENIED, mapOf(PARAM_PERMISSION_TYPE to permissionType))
        Log.d("EventsManager", "Analytics: Permission denied: $permissionType")
    }

    fun logPermissionsSkipped() {
        logEvent(EVENT_PERMISSIONS_SKIPPED)
        Log.d("EventsManager", "Analytics: Permissions skipped")
    }



    /**
     * Helper method to log events
     * NOTE: When Firebase is enabled, replace this with FirebaseAnalytics.logEvent()
     */
    private fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        try {
            // TODO: Replace with Firebase Analytics when enabled
            // firebaseAnalytics.logEvent(eventName, bundle)

            // For now, just log to console
            Log.d("EventsManager", "Event: $eventName, Params: $params")

        } catch (e: Exception) {
            Log.e("EventsManager", "Error logging event: $eventName", e)
        }
    }
}
