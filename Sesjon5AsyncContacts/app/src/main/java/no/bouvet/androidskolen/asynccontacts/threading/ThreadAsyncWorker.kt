package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import kotlin.concurrent.thread

class ThreadAsyncWorker() : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        thread {
            runnable.invoke()
        }
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        thread {
            val result = backgroundWork.invoke()
            host.runOnUiThread { presentInGui.invoke(result) }
        }
    }

}