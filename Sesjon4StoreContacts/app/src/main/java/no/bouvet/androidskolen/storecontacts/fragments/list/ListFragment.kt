package no.bouvet.androidskolen.storecontacts.fragments.list

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list.*
import no.bouvet.androidskolen.storecontacts.R
import no.bouvet.androidskolen.storecontacts.models.ContactSelectedListener
import no.bouvet.androidskolen.storecontacts.storage.ContactStorage
import no.bouvet.androidskolen.storecontacts.storage.Contacts
import kotlin.concurrent.thread

class ListFragment : Fragment() {

    private lateinit var storage : ContactStorage
    private lateinit var adapter: ListAdapter
    private val _contactListener by lazy { context as ContactSelectedListener }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storage = Contacts.getStorage(context!!)
        adapter = ListAdapter(_contactListener)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        contactsChanged()
        storage.listenToChange { contactsChanged() }
    }

    private fun contactsChanged() {
        thread {
            // TODO: Oppgave 4 - ta i bruk permissions-sjekking
            // if (PermissionHandling.hasPermissionsForContacts(activity!!)) {
                val contacts = storage.all()
                activity!!.runOnUiThread { adapter.updateContacts(contacts) }
            // }
        }
    }

}
