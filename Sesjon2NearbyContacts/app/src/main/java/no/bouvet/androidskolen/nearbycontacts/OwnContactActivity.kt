package no.bouvet.androidskolen.nearbycontacts

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import no.bouvet.androidskolen.nearbycontacts.models.Contact
import no.bouvet.androidskolen.nearbycontacts.models.OwnContactViewModel

class OwnContactActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_own_contact)

    }

    override fun onStart() {
        super.onStart()

        val contact = createContactFromPreferences()

        // TODO: Oppgave 1
    }

    private fun createContactFromPreferences(): Contact {
        val preferences = getSharedPreferences(PREFERENCE_FILE, 0)

        val name = preferences.getString(PREFERENCE_NAME, "")
        val email = preferences.getString(PREFERENCE_EMAIL, "")
        val telephone = preferences.getString(PREFERENCE_TELEPHONE, "")

        return Contact(name, email, telephone)
    }

    override fun onClick(view: View) {
        val contact = createContactFromInput()
        saveOwnContactInfo(contact)
        OwnContactViewModel.INSTANCE.contact = contact

        // TODO: Oppgave 1
    }

    private fun saveOwnContactInfo(contact: Contact) {
        val preferences = getSharedPreferences(PREFERENCE_FILE, 0)

        val edit = preferences.edit()
        edit.putString(PREFERENCE_NAME, contact.name)
        edit.putString(PREFERENCE_EMAIL, contact.email)
        edit.putString(PREFERENCE_TELEPHONE, contact.telephone)
        edit.apply()

    }

    private fun createContactFromInput(): Contact {
        // TODO: Oppgave 1
        val name = ""
        val email = ""
        val telephone = ""

        return Contact(name, email, telephone)
    }

    companion object {

        private const val PREFERENCE_FILE = "OwnContactInfo"
        private const val PREFERENCE_NAME = "OwnContactName"
        private const val PREFERENCE_EMAIL = "OwnContactEmail"
        private const val PREFERENCE_TELEPHONE = "OwnContactTelephone"
    }
}
