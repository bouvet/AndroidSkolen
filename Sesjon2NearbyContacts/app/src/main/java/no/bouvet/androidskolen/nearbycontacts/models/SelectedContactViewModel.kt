package no.bouvet.androidskolen.nearbycontacts.models

enum class SelectedContactViewModel {

    INSTANCE;

    var contact: Contact? = null
        private set

    private var modelUpdateListener: ModelUpdateListener? = null

    fun setModelUpdateListener(listener: ModelUpdateListener) {
        modelUpdateListener = listener
    }

    fun removeModelUpdateListener(listener: ModelUpdateListener) {
        if (modelUpdateListener === listener) {
            modelUpdateListener = null
        }
    }

    fun setSelectedContact(contact: Contact) {
        this.contact = contact
        fireModelUpdated()
    }


    private fun fireModelUpdated() {
        modelUpdateListener?.onModelChanged()
    }

    fun reset() {
        contact = null
    }
}
