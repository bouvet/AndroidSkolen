package no.bouvet.androidskolen.asynccontacts.service

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService


class WorkService : JobIntentService() {


    override fun onHandleWork(intent: Intent) {
        var work = PendingWork.INSTANCE.next()
        while (work != null) {
            work.invoke()
            work = PendingWork.INSTANCE.next()
        }
    }

    companion object {

        // TODO: Oppgave 4 - Entrypoint for å starte opp servicen kan f.eks. være her.
        
    }

}