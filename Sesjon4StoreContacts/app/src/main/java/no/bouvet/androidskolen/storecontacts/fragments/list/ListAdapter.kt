package no.bouvet.androidskolen.storecontacts.fragments.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.contact_view_holder.view.*
import no.bouvet.androidskolen.storecontacts.R
import no.bouvet.androidskolen.storecontacts.models.Contact
import no.bouvet.androidskolen.storecontacts.models.ContactSelectedListener

class ListAdapter(listener: ContactSelectedListener) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var _listener = listener
    var _contacts = listOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_view_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(_contacts[position], _listener)
    }

    fun updateContacts(contacts: List<Contact>) {
        _contacts = contacts
        notifyDataSetChanged()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindViewHolder(contact: Contact, _listener: ContactSelectedListener) {
            itemView.textName.text = contact.name
            itemView.textPhone.text = contact.telephone
            itemView.setOnClickListener { _listener.onContactSelected(contact.id) }
        }

    }

}
