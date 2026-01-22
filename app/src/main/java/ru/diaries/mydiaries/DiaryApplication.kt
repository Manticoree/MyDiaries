package ru.diaries.mydiaries

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.diaries.mydiaries.data.local.DiaryDatabase
import javax.inject.Inject

@HiltAndroidApp
class DiaryApplication : Application() {

    @Inject
    lateinit var database: DiaryDatabase

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            database.openHelper.writableDatabase
        }
    }
}
