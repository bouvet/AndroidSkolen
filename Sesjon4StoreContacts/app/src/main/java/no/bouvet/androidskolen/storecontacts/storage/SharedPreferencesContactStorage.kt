package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context
import no.bouvet.androidskolen.storecontacts.models.Contact

class SharedPreferencesContactStorage(val context: Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()
    // TODO: Oppgave 1: Få tak i shared preferences

    override fun get(id: Int): Contact? {
//        if (id > sharedPreferences.getInt(lastContactInStoreKey, 0)) {
//            return null
//        }
        // TODO: Oppgave 1: Hent contact-felter fra preferences, se companion object
        return null

    }

    override fun all() : List<Contact> {
        val contacts = mutableListOf<Contact>()
        val lastContactInStore = 0 // TODO: = sharedPreferences.getInt(lastContactInStoreKey, 0)
        if (lastContactInStore == 0) {
            return contacts
        }
        for (id in 1..lastContactInStore) {
            val contact = get(id)
            if (contact != null) {
                contacts.add(contact)
            }
        }
        return contacts
    }

    override fun save(contact: Contact) {
        val id = 0 // TODO: = sharedPreferences.getInt(lastContactInStoreKey, 0) + 1
        // TODO: Lagre contact-felter fra preferences, se companion object.
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

    companion object {
        val lastContactInStoreKey = "last-contact-in-store";
        fun contactIdKey(id : Int) : String {
            return "contact-$id-id"
        }
        fun contactNameKey(id : Int) : String {
            return "contact-$id-name"
        }
        fun contactEmailKey(id : Int) : String {
            return "contact-$id-email"
        }
        fun contactTelephoneKey(id : Int) : String {
            return "contact-$id-telephone"
        }
    }
}