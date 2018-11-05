package no.bouvet.androidskolen.storecontacts.fragments.add

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.EditText

import kotlinx.android.synthetic.main.fragment_add.*
import no.bouvet.androidskolen.storecontacts.R
import no.bouvet.androidskolen.storecontacts.models.Contact
import no.bouvet.androidskolen.storecontacts.storage.Contacts
import kotlin.concurrent.thread

class AddContactFragment : Fragment() {

    lateinit var nameInput : EditText
    lateinit var emailInput : EditText
    lateinit var telephoneInput : EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        nameInput = view.findViewById(R.id.nameInput)
        emailInput = view.findViewById(R.id.emailInput)
        telephoneInput = view.findViewById(R.id.telephoneInput)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        saveButton.setOnClickListener { saveAndClose() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_cancel -> {
                close()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAndClose(){
        val contact = Contact(
            nameInput.text.toString(),
            emailInput.text.toString(),
            telephoneInput.text.toString()
        )
        val appContext = context!!.applicationContext
        thread {
            Contacts.getStorage(appContext).save(contact)
        }
        close()
    }

    private fun close(){
        fragmentManager?.popBackStack()
    }
}
