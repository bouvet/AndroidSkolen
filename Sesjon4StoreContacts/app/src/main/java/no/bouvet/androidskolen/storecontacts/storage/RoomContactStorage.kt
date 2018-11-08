package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context
import no.bouvet.androidskolen.storecontacts.models.Contact

class RoomContactStorage(val context : Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()
    val dao = RoomContactsDatabase.getDatabase(context).dao()

    override fun get(id: Int): Contact? {
        return dao.get(id)
    }

    override fun all(): List<Contact> {
        return dao.all()
    }

    override fun save(contact: Contact) {
        dao.save(contact)
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