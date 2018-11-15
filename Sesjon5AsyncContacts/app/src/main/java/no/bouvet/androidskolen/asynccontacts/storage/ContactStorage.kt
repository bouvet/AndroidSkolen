package no.bouvet.androidskolen.asynccontacts.storage

import no.bouvet.androidskolen.asynccontacts.models.Contact

interface ContactStorage {

    fun get(id: Int) : Contact?

    fun all() : List<Contact>

    fun save(contact: Contact)

    fun listenToChange(listener: () -> Unit)

}