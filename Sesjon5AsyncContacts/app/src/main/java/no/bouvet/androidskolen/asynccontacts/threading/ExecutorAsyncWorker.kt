package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

class ExecutorAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        executor.submit(runnable)
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        executor.submit {
            val result = backgroundWork()
            mainHandler.post { presentInGui(result) }
        }
    }

    companion object {

        val looper = Looper.getMainLooper()
        val mainHandler = Handler(looper);

        val executor = Executors.newFixedThreadPool(5)

    }

}