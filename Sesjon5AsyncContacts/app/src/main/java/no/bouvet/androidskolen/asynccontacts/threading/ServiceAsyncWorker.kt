package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import no.bouvet.androidskolen.asynccontacts.service.PendingWork
import no.bouvet.androidskolen.asynccontacts.service.WorkService

class ServiceAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        PendingWork.INSTANCE.add(runnable)
        WorkService.enqueueWork(context, Intent(context, WorkService::class.java))
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        PendingWork.INSTANCE.add {
            val result = backgroundWork()
            mainHandler.post { presentInGui(result) }
        }
        WorkService.enqueueWork(host, Intent(host, WorkService::class.java))
    }


    companion object {

        val looper = Looper.getMainLooper()
        val mainHandler = Handler(looper);

    }
}