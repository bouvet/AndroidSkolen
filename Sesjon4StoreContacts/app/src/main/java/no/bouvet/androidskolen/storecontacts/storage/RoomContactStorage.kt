package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context
import no.bouvet.androidskolen.storecontacts.models.Contact

class RoomContactStorage(val context : Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()
    // TODO: Oppgave 3 - finne Room-DAOen

    override fun get(id: Int): Contact? {
        // TODO: Oppgave 3
        return null;
    }

    override fun all(): List<Contact> {
        // TODO: Oppgave 3
        return emptyList()
    }

    override fun save(contact: Contact) {
        // TODO: Oppgave 3
        triggerListeners()
    }

    override fun listenToChange(listener: () -> Unit) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    private fun triggerListeners() {
        synchronized(listeners) {
            for (listener in listeners) {
                listener.invoke()
            }
        }
    }
}