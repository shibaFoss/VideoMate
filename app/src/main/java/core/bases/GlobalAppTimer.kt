package core.bases

import android.os.CountDownTimer
import java.lang.ref.WeakReference

open class GlobalAppTimer(millisInFuture: Long, countDownInterval: Long) :
    CountDownTimer(millisInFuture, countDownInterval) {

    private val timerListeners = ArrayList<WeakReference<GlobalTimerListener>>()
    private var loopCount = 0.0

    override fun onTick(millisUntilFinished: Long) {
        loopCount++
        timerListeners.removeAll { it.get() == null }
        timerListeners.forEach { listenerRef ->
            listenerRef.get()?.onGlobalTimerTick(loopCount)
        }
    }

    override fun onFinish() {
        this.start()
    }

    fun register(listener: GlobalTimerListener) {
        if (timerListeners.none { it.get() == listener }) {
            timerListeners.add(WeakReference(listener))
        }
    }

    fun unregister(listener: GlobalTimerListener) {
        timerListeners.removeAll { it.get() == listener }
    }

    fun stop() {
        this.cancel()
        timerListeners.clear()
    }

    interface GlobalTimerListener {
        fun onGlobalTimerTick(loopCount: Double)
    }
}