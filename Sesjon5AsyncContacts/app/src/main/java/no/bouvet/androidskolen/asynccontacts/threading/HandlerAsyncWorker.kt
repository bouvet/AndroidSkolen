package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class HandlerAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        // TODO: Oppgave 1
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        // TODO: Oppgave 1
    }

}