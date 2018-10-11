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

    private lateinit var adapter : NearbyContactsAdapter
    // Context her er aktiviteten fragmentet ligger i, altså "NearbyActivity"
    private val contactSelectedListener by lazy { context as ContactSelectedListener }

    override fun onModelChanged() {
        updateAdapterModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.nearby_contacts_fragment, container, false)
        // Binde opp hver gang viewet lages på nytt, så vi alltid har koblet adapteret til viewet som faktisk vises (se backstack i NearbyActivty)
        val recyclerView = view.findViewById<RecyclerView>(R.id.nearby_recyclerview)
        adapter = createAndConnectAdapter(recyclerView)

        return view
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
    private fun createAndConnectAdapter(recyclerView: RecyclerView) : NearbyContactsAdapter {
        val adapter = NearbyContactsAdapter(contactSelectedListener)
        recyclerView.adapter = adapter
        // LayoutManager for å liste ut alle elementer nedover. Alternativet er horisontalt eller "grid" layout
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        return adapter
    }
    private fun updateAdapterModel() {
        // Uten "Nearby" enheter:
        val contactList = listOf<Contact>(
                Contact("Ola Nordmann", "ola.nordmann@online.no", "24393988"),
                Contact("Kari Nordmann", "kari.nordmann@online.no", "45389893"),
                Contact("Knut Nordmann", "knut.nordmann@online.no", "21321322")
        )

        // Med "Nearby" enheter
        // val contactList = NearbyContactsListViewModel.INSTANCE.nearbyContacts

        adapter.updateItems(contactList)
    }
}
