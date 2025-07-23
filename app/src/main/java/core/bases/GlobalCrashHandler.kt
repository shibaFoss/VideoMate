package core.bases

import android.app.Application
import java.io.PrintWriter
import java.io.StringWriter

class GlobalCrashHandler(applicationContext: Application) : Thread.UncaughtExceptionHandler {

    private val appInstance = applicationContext

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        try {
            val stackTrace = StringWriter().use { sw ->
                PrintWriter(sw).use { pw ->
                    exception.printStackTrace(pw)
                    sw.toString()
                }
            }

        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
}
