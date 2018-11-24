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

        // "work" vil i et mer realistisk scenario inneholde informasjon om hva som skal gjøres
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, WorkService::class.java, 1000, work)
        }


    }

}