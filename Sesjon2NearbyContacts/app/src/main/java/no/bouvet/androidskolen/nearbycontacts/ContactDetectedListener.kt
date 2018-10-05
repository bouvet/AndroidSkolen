package no.bouvet.androidskolen.nearbycontacts

import no.bouvet.androidskolen.nearbycontacts.models.Contact

interface ContactDetectedListener {

    fun onContactDetected(contact: Contact)

    fun onContactLost(contact: Contact)

}
