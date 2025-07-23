package core.bases

import android.app.Activity
import core.caches.GlobalRawFiles
import core.database.GlobalDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import libs.process.AsyncJobsUtility.executeOnMainThread
import libs.process.ThreadsUtility
import java.io.File

class GlobalApplication : LanguageAwareApplication() {

    companion object {
        @Volatile
        lateinit var APP_INSTANCE: GlobalApplication
        const val IS_DEBUG_MODE_ON = true
        var IS_PREMIUM_USER = true

        val internalDataFolder: File get() = APP_INSTANCE.filesDir
        val externalDataFolder: File? get() = APP_INSTANCE.getExternalFilesDir(null)

        lateinit var globalDatabaseHelper: GlobalDatabaseHelper
        lateinit var globalRawFiles: GlobalRawFiles
        lateinit var globalLanguageHelper: GlobalLanguageHelper
        lateinit var globalAppTimer: GlobalAppTimer
    }

    private val startupManager = StartupManager()

    override fun onCreate() {
        super.onCreate()
        APP_INSTANCE = this

        startupManager.apply {
            addCriticalTask {
                globalAppTimer = GlobalAppTimer(3600000, 500)
                globalAppTimer.start()
                globalDatabaseHelper = GlobalDatabaseHelper.getInstance()
            }

            addHighPriorityTask {
                globalLanguageHelper = GlobalLanguageHelper()
                manageActivityLifeCycle()
            }

            addBackgroundTask {
                globalRawFiles = GlobalRawFiles()
                globalRawFiles.loadRawFilesIntoMemory()
            }
        }

        startupManager.executeCriticalTasks()
        ThreadsUtility.executeInBackground(codeBlock = {
            startupManager.executeHighPriorityTasks()
            startupManager.executeBackgroundTasks()
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        ThreadsUtility.executeInBackground(codeBlock = {
            globalAppTimer.cancel()
        })
    }

    private fun manageActivityLifeCycle() {
        executeOnMainThread {
            registerActivityLifecycleCallbacks(object : GlobalAppLifeCycle {
                override fun onActivityDestroyed(activity: Activity) {
                    if (activity is GlobalBaseActivity) {
                        activity.clearWeakActivityReference()
                    }
                }
            })
        }
    }

    private class StartupManager {
        private val criticalTasks = mutableListOf<() -> Unit>()
        private val highPriorityTasks = mutableListOf<() -> Unit>()
        private val backgroundTasks = mutableListOf<() -> Unit>()
        private val scope = CoroutineScope(Dispatchers.Default)

        fun addCriticalTask(task: () -> Unit) {
            criticalTasks.add(task)
        }

        fun addHighPriorityTask(task: () -> Unit) {
            highPriorityTasks.add(task)
        }

        fun addBackgroundTask(task: () -> Unit) {
            backgroundTasks.add(task)
        }

        fun executeCriticalTasks() {
            criticalTasks.forEach { it() }
        }

        fun executeHighPriorityTasks() {
            scope.launch {
                highPriorityTasks.map {
                    async { it() }
                }.awaitAll()
            }
        }

        fun executeBackgroundTasks() {
            scope.launch {
                backgroundTasks.map {
                    async(Dispatchers.IO) { it() }
                }.awaitAll()
            }
        }
    }
}