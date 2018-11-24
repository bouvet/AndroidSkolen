package no.bouvet.androidskolen.asynccontacts.threading

class AsyncWork {

    companion object {

        fun getWorker() : AsyncWorker {
            // return ThreadAsyncWorker()
            // return HandlerAsyncWorker()
            // return TaskAsyncWorker()
            // return ExecutorAsyncWorker()
            return ServiceAsyncWorker()
        }

    }

}