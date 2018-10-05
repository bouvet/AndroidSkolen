package no.bouvet.androidskolen.nearbycontacts

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import no.bouvet.androidskolen.nearbycontacts.models.Contact
import no.bouvet.androidskolen.nearbycontacts.models.ModelUpdateListener
import no.bouvet.androidskolen.nearbycontacts.models.SelectedContactViewModel

class SelectedContactFragment : Fragment(), ModelUpdateListener, View.OnClickListener {

    // TODO: Oppgave 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.selected_contact_fragment, container, false)

        // TODO: Oppgave 3

        return view
    }

    override fun onResume() {
        super.onResume()

        SelectedContactViewModel.INSTANCE.setModelUpdateListener(this)
        updateGui(SelectedContactViewModel.INSTANCE.contact)
    }

    override fun onModelChanged() {
        updateGui(SelectedContactViewModel.INSTANCE.contact)
    }


    private fun updateGui(contact: Contact?) {
        if (contact != null) {
            Log.d(TAG, "Contact selected: " + contact.name)

            // TODO: Oppgave 3

        }
    }

    override fun onClick(view: View) {
        val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION)
        contactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE

        val contact = SelectedContactViewModel.INSTANCE.contact

        if (contact != null) {
            contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, contact.name)
            contactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
            contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.telephone)
        }

        startActivityForResult(contactIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, "Added Contact", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity, "Cancelled Added Contact", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        private val TAG = SelectedContactFragment::class.java.simpleName
    }
}
