package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context
import no.bouvet.androidskolen.storecontacts.models.Contact

class SharedPreferencesContactStorage(val context: Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()
    val sharedPreferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    override fun get(id: Int): Contact? {
        if (id > sharedPreferences.getInt(lastContactInStoreKey, 0)) {
            return null
        }
        val foundId = sharedPreferences.getInt(contactIdKey(id), -1)
        val name = sharedPreferences.getString(contactNameKey(id), "")
        val email = sharedPreferences.getString(contactEmailKey(id), "")
        val telephone = sharedPreferences.getString(contactTelephoneKey(id), "")
        if (foundId < 0) {
            return null
        }
        val contact = Contact(name, email, telephone)
        contact.id = foundId
        return contact
    }

    override fun all() : List<Contact> {
        val contacts = mutableListOf<Contact>()
        val lastContactInStore = sharedPreferences.getInt(lastContactInStoreKey, 0)
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
        val id = sharedPreferences.getInt(lastContactInStoreKey, 0) + 1
        with(sharedPreferences.edit()) {
            putInt(lastContactInStoreKey, id)
            putInt(contactIdKey(id), id)
            putString(contactNameKey(id), contact.name)
            putString(contactEmailKey(id), contact.email)
            putString(contactTelephoneKey(id), contact.telephone)
            commit()
        }
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