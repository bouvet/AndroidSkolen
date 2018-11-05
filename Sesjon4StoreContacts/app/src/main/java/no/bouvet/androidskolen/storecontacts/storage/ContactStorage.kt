package no.bouvet.androidskolen.storecontacts.storage

import no.bouvet.androidskolen.storecontacts.models.Contact

interface ContactStorage {

    fun get(id: Int) : Contact?

    fun all() : List<Contact>

    fun save(contact: Contact)

    fun listenToChange(listener: () -> Unit)

}