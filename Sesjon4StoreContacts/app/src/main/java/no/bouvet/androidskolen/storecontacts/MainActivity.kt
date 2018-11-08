package no.bouvet.androidskolen.storecontacts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import no.bouvet.androidskolen.storecontacts.fragments.add.AddContactFragment
import no.bouvet.androidskolen.storecontacts.fragments.list.ListFragment

import kotlinx.android.synthetic.main.activity_main.*
import no.bouvet.androidskolen.storecontacts.fragments.PermissionHandling
import no.bouvet.androidskolen.storecontacts.fragments.details.DetailsFragment
import no.bouvet.androidskolen.storecontacts.models.Contact
import no.bouvet.androidskolen.storecontacts.models.ContactSelectedListener
import no.bouvet.androidskolen.storecontacts.storage.Contacts
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ContactSelectedListener {

    private val FRAGMENT_TAG = "ACTIVE_FRAGMENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        showRootFragment(ListFragment())

        fab.setOnClickListener { showFragment(AddContactFragment()) }

        initializeStorage()
    }

    private fun initializeStorage() {
        thread {
            if (PermissionHandling.hasPermissionsForContacts(this)) {
                val storage = Contacts.getStorage(this)
                val existing = storage.all()
                if (existing.isEmpty()) {
                    storage.save(Contact("Ola Nordmann", "ola.nordmann@online.no", "24393988"))
                    storage.save(Contact("Kari Nordmann", "kari.nordmann@online.no", "45389893"))
                    storage.save(Contact("Knut Nordmann", "knut.nordmann@online.no", "21321322"))
                }
            }
            else {
                PermissionHandling.requestPermissionForContacts(this)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        initializeStorage()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        supportFragmentManager.addOnBackStackChangedListener(_onBackStackChangedListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        supportFragmentManager.removeOnBackStackChangedListener(_onBackStackChangedListener)
    }

    private fun showRootFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
            .commit()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
            .addToBackStack(null)
            .commit()
    }

    override fun onContactSelected(id: Int) {
        showFragment(DetailsFragment.newInstance(id))
    }

    private fun toggleFAB(){
        fab.visibility = if (supportFragmentManager.backStackEntryCount == 0) View.VISIBLE else View.GONE
    }

    private val _onBackStackChangedListener =  { toggleFAB() }

}
