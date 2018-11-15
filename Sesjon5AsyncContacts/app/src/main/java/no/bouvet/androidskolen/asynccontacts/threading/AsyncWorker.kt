package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread

interface AsyncWorker {

    fun run(context: Context, @WorkerThread runnable: () -> Unit)

    fun <V : Any?> backgroundThenGui(host: Activity, @WorkerThread backgroundWork: () -> V, @MainThread presentInGui: (V) -> Unit)

}