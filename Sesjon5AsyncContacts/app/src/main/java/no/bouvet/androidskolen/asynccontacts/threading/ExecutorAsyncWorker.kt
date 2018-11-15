package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import java.util.concurrent.Executors

class ExecutorAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        // TODO: Oppgave 3
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        // TODO: Oppgave 3
    }

    companion object {

        // TODO: Oppgave 3

    }

}