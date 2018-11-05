package no.bouvet.androidskolen.storecontacts.fragments.details

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import kotlinx.android.synthetic.main.fragment_details.*
import no.bouvet.androidskolen.storecontacts.R
import no.bouvet.androidskolen.storecontacts.storage.Contacts
import kotlin.concurrent.thread

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
            val id = bundle.getInt(BUNDLE_ID);
            thread {
                val contact = Contacts.getStorage(context!!).get(id)
                activity!!.runOnUiThread {
                    if (contact != null) {
                        textName.text = contact.name
                        textPhone.text = contact.telephone
                        textEmail.text = contact.email
                    }
                }
            }
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
