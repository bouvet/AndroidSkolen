package no.bouvet.androidskolen.nearbycontacts

import no.bouvet.androidskolen.nearbycontacts.models.Contact

interface ContactSelectedListener {

    fun onContactSelected(contact: Contact)

}
