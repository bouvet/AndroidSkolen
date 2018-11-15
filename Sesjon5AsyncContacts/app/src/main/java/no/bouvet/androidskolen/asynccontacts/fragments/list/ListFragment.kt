package no.bouvet.androidskolen.asynccontacts.fragments.list

import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list.*
import no.bouvet.androidskolen.asynccontacts.R
import no.bouvet.androidskolen.asynccontacts.models.Contact
import no.bouvet.androidskolen.asynccontacts.models.ContactSelectedListener
import no.bouvet.androidskolen.asynccontacts.storage.ContactStorage
import no.bouvet.androidskolen.asynccontacts.storage.Contacts
import no.bouvet.androidskolen.asynccontacts.threading.AsyncWork

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
        AsyncWork.getWorker().backgroundThenGui(
            activity!!,
            ::fetchContacts,
            ::updateContactList
        )
    }

    @WorkerThread
    private fun fetchContacts() : List<Contact> {
        return storage.all()
    }

    @MainThread
    private fun updateContactList(contacts: List<Contact>) {
        adapter.updateContacts(contacts)
    }

}
