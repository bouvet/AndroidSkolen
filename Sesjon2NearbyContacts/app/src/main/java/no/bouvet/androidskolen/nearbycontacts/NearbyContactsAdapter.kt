package no.bouvet.androidskolen.nearbycontacts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import no.bouvet.androidskolen.nearbycontacts.models.Contact

class NearbyContactsAdapter(val contactSelectedListener: ContactSelectedListener) : RecyclerView.Adapter<NearbyContactsAdapter.ViewHolder>() {

    var items = listOf<Contact>()

    fun updateItems(items : List<Contact>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyContactsAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.nearby_contacts_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position), contactSelectedListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        fun bind(item : Contact, contactSelectedListener: ContactSelectedListener) {
            // TODO: Oppgave 2
        }

    }

}
