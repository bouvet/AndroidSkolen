package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.os.AsyncTask

class TaskAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        // TODO: Oppgave 2
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        // TODO: Oppgave 2
    }

}