package no.bouvet.androidskolen.nearbycontacts.models

import no.bouvet.androidskolen.nearbycontacts.ContactDetectedListener
import java.util.*

enum class NearbyContactsListViewModel : ContactDetectedListener {

    INSTANCE;

    private val detectedContacts = HashMap<String, Contact>()
    private var modelUpdateListener: ModelUpdateListener? = null

    val nearbyContacts: List<Contact>
        get() = ArrayList(detectedContacts.values)

    fun setModelUpdateListener(listener: ModelUpdateListener) {
        modelUpdateListener = listener
    }

    fun removeModelUpdateListener(listener: ModelUpdateListener) {
        if (modelUpdateListener === listener) {
            modelUpdateListener = null
        }
    }

    override fun onContactDetected(contact: Contact) {
        detectedContacts[contact.name] = contact
        fireModelUpdated()
    }

    override fun onContactLost(contact: Contact) {
        detectedContacts.remove(contact.name)
        fireModelUpdated()
    }

    private fun fireModelUpdated() {
        modelUpdateListener?.onModelChanged()
    }

    fun reset() {
        detectedContacts.clear()
    }
}
