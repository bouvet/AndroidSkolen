package no.bouvet.androidskolen.asynccontacts.service

import java.util.*

class PendingWork {

    val queue = LinkedList<() -> Unit>()

    fun add(work : () -> Unit) {
        synchronized(queue) {
            queue.add(work)
        }
    }

    fun next(): (() -> Unit)? {
        synchronized(queue) {
           return queue.poll()
        }
    }

    companion object {

        val INSTANCE = PendingWork()

    }


}