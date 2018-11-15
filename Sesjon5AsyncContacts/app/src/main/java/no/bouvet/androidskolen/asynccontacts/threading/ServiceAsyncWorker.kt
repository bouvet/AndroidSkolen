package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.content.Intent
import no.bouvet.androidskolen.asynccontacts.service.PendingWork
import no.bouvet.androidskolen.asynccontacts.service.WorkService

class ServiceAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        // TODO: Oppgave 4
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        // TODO: Oppgave 4

    }

}