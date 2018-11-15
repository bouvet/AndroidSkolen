package no.bouvet.androidskolen.asynccontacts.fragments.details

import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_details.*
import no.bouvet.androidskolen.asynccontacts.R
import no.bouvet.androidskolen.asynccontacts.models.Contact
import no.bouvet.androidskolen.asynccontacts.storage.Contacts
import no.bouvet.androidskolen.asynccontacts.threading.AsyncWork

class DetailsFragment : Fragment() {

    companion object {
        val BUNDLE_ID = "bundle_id"

        fun newInstance(id: Int) : DetailsFragment {
            val detailsFragment = DetailsFragment()
            val arguments = Bundle()
            arguments.putInt(BUNDLE_ID, id)
            detailsFragment.arguments = arguments
            return detailsFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val bundle = arguments
        if(bundle == null || !bundle.containsKey(BUNDLE_ID)) {
            close() //todo
        } else {
            AsyncWork.getWorker().backgroundThenGui(
                activity!!,
                ::fetchContact,
                ::updateContactData
            )
        }
    }

    @WorkerThread
    private fun fetchContact() : Contact? {
        val id = arguments!!.getInt(BUNDLE_ID)
        return Contacts.getStorage(context!!.applicationContext).get(id)
    }

    @MainThread
    private fun updateContactData(contact: Contact?) {
        if (contact != null) {
            textName.text = contact.name
            textPhone.text = contact.telephone
            textEmail.text = contact.email
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_edit -> {
                editContact()
                true
            }
            R.id.action_cancel -> {
                close()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editContact(){
        //todo
    }

    private fun close(){
        fragmentManager?.popBackStack()
    }

}
