package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class HandlerAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        val handlerThread = HandlerThread("Work-thread")
        handlerThread.start()
        Handler(handlerThread.looper).post(runnable)
        handlerThread.quitSafely()
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        val handlerThread = HandlerThread("Work-thread")
        handlerThread.start()
        Handler(handlerThread.looper).post {
            val result = backgroundWork()
            mainHandler.post { presentInGui(result) }
        }
        handlerThread.quitSafely()
    }

    companion object {

        val looper = Looper.getMainLooper()
        val mainHandler = Handler(looper);

    }
}