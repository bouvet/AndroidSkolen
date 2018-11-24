package no.bouvet.androidskolen.asynccontacts.threading

import android.app.Activity
import android.content.Context
import android.os.AsyncTask

class TaskAsyncWorker : AsyncWorker {

    override fun run(context: Context, runnable: () -> Unit) {
        // Kan forkortes til:
        // AsyncTask.execute(runnable)
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                runnable()
                return null
            }
        }.execute()
    }

    override fun <V> backgroundThenGui(host: Activity, backgroundWork: () -> V, presentInGui: (V) -> Unit) {
        object : AsyncTask<Void, Void, V>() {
            override fun doInBackground(vararg params: Void?): V {
                return backgroundWork()
            }
            override fun onPostExecute(result: V) {
                presentInGui(result)
            }
        }.execute()
    }

}