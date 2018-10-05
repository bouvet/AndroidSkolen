package no.bouvet.androidskolen.nearbycontacts

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import no.bouvet.androidskolen.nearbycontacts.models.Contact
import no.bouvet.androidskolen.nearbycontacts.models.ModelUpdateListener
import no.bouvet.androidskolen.nearbycontacts.models.NearbyContactsListViewModel

class NearbyContactsFragment : Fragment(), ModelUpdateListener {

    // TODO: Oppgave 2

    override fun onModelChanged() {
        updateAdapterModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.nearby_contacts_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()

        NearbyContactsListViewModel.INSTANCE.setModelUpdateListener(this)
        updateAdapterModel()
    }

    override fun onPause() {
        super.onPause()

        NearbyContactsListViewModel.INSTANCE.removeModelUpdateListener(this)
    }

    private fun updateAdapterModel() {
        val contactList = NearbyContactsListViewModel.INSTANCE.nearbyContacts

        // TODO: Oppgave 2
    }
}
