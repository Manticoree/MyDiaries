package ru.diaries.mydiaries.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.diaries.mydiaries.ui.profile.AppTheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mydiaries_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_INSTALL_DATE = "install_date"
        private const val KEY_THEME = "app_theme"
    }

    // Onboarding
    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var installDate: Long
        get() = prefs.getLong(KEY_INSTALL_DATE, System.currentTimeMillis())
        set(value) = prefs.edit().putLong(KEY_INSTALL_DATE, value).apply()

    // Theme
    var appTheme: AppTheme
        get() {
            val themeOrdinal = prefs.getInt(KEY_THEME, 0)
            return AppTheme.entries.getOrNull(themeOrdinal) ?: AppTheme.SYSTEM
        }
        set(value) = prefs.edit().putInt(KEY_THEME, value.ordinal).apply()

    /**
     * Get days since install
     */
    fun getDaysSinceInstall(): Int {
        val now = System.currentTimeMillis()
        val install = installDate
        return ((now - install) / (1000 * 60 * 60 * 24)).toInt()
    }
}
