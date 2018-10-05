package no.bouvet.androidskolen.nearbycontacts

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import no.bouvet.androidskolen.nearbycontacts.models.Contact
import no.bouvet.androidskolen.nearbycontacts.models.NearbyContactsListViewModel
import no.bouvet.androidskolen.nearbycontacts.models.OwnContactViewModel
import no.bouvet.androidskolen.nearbycontacts.models.SelectedContactViewModel

class NearbyActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ContactSelectedListener {

    private val messageListener by lazy { setupNearbyMessageListener() }
    private val googleApiClient by lazy { setupNearbyMessagesApi() }
    private val contactDetectedListener = NearbyContactsListViewModel.INSTANCE

    private var activeMessage: Message? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nearby)

        addNearbyContactsFragmentIfNotExists()

    }

    private fun addNearbyContactsFragmentIfNotExists() {

        var nearbyContactsFragment: NearbyContactsFragment? = fragmentManager.findFragmentById(R.id.nearby_contacts_list_fragment) as NearbyContactsFragment?
        if (nearbyContactsFragment == null || !nearbyContactsFragment.isInLayout) {
            nearbyContactsFragment = NearbyContactsFragment()

            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_holder, nearbyContactsFragment)
            fragmentTransaction.commit()
        }
    }


    override fun onContactSelected(contact: Contact) {
        Log.d(TAG, "Contact selected: " + contact.name)

        val selectedContactFragment = fragmentManager.findFragmentById(R.id.selected_contact_fragment) as SelectedContactFragment?

        if (selectedContactFragment == null || !selectedContactFragment.isInLayout) {

            // TODO: Oppgave 3

        }

        SelectedContactViewModel.INSTANCE.setSelectedContact(contact)

    }

    private fun setupNearbyMessageListener() : MessageListener {
        return object : MessageListener() {
            override fun onFound(message: Message) {
                Log.d(TAG, "[onFound]")
                val nearbyDevices = message.zzbxl()

                for (nearbyDevice in nearbyDevices) {
                    Log.d(TAG, nearbyDevice.zzbxr())
                }

                val messageAsJson = String(message.content)
                Log.d(TAG, "Found message: $messageAsJson")

                val contact = Contact.fromJson(messageAsJson)
                fireContactDetected(contact)
            }

            override fun onDistanceChanged(message: Message, distance: Distance) {
                Log.i(TAG, "Distance changed, message: $message, new distance: $distance")
            }

            override fun onBleSignalChanged(message: Message, bleSignal: BleSignal) {
                Log.i(TAG, "Message: $message has new BLE signal information: $bleSignal")
            }

            override fun onLost(message: Message) {
                Log.d(TAG, "[onLost]")
                val messageAsJson = String(message.content)
                Log.d(TAG, "Lost sight of message: $messageAsJson")

                val contact = Contact.fromJson(messageAsJson)
                fireContactLost(contact)
            }
        }
    }

    private fun setupNearbyMessagesApi() : GoogleApiClient {
        return GoogleApiClient.Builder(this)
                .addApi(com.google.android.gms.nearby.Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "[onStart]")
        googleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG, "[onStop]")

        if (googleApiClient.isConnected) {
            unpublish()
            unsubscribe()
            googleApiClient.disconnect()
            resetModels()
        }
    }

    private fun resetModels() {
        NearbyContactsListViewModel.INSTANCE.reset()
        SelectedContactViewModel.INSTANCE.reset()
    }

    private fun publish(contact: Contact) {

        Log.i(TAG, "[publish] Publishing information about contact: " + contact.name)
        val json = contact.toJson()
        activeMessage = Message(json.toByteArray())
        Nearby.Messages.publish(googleApiClient, activeMessage)
    }

    private fun unpublish() {

        Log.i(TAG, "[unpublish] ")
        if (activeMessage != null) {
            Nearby.Messages.unpublish(googleApiClient, activeMessage)
            activeMessage = null
        }
    }

    private fun subscribe() {
        Log.i(TAG, "Subscribing.")

        val strategy = Strategy.Builder()
                .setDiscoveryMode(Strategy.DISCOVERY_MODE_DEFAULT)
                .setTtlSeconds(Strategy.TTL_SECONDS_DEFAULT)
                .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT)
                .build()

        val callback = object : SubscribeCallback() {
            override fun onExpired() {
                super.onExpired()
                Log.d(TAG, "[onExpired] subscribing anew...")
                subscribe()
            }
        }
        val options = SubscribeOptions.Builder()
                .setStrategy(strategy)
                .setCallback(callback)
                .build()

        Nearby.Messages.subscribe(googleApiClient, messageListener, options)
    }

    private fun unsubscribe() {
        Log.i(TAG, "[unsubscribe].")
        Nearby.Messages.unsubscribe(googleApiClient, messageListener)
    }


    private fun publishContactInternally() {
        Log.d(TAG, "[publishContactInternally]")
        if (googleApiClient.isConnected) {
            publish(OwnContactViewModel.INSTANCE.contact!!)
            subscribe()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "[onConnected]")
        publishContactInternally()
    }

    override fun onConnectionSuspended(i: Int) {
        Log.e(TAG, "GoogleApiClient disconnected with cause: $i")
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR)
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "GoogleApiClient connection failed", e)
            }

        } else {
            Log.e(TAG, "GoogleApiClient connection failed")
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            if (resultCode == Activity.RESULT_OK) {
                googleApiClient.connect()
            } else {
                Log.e(TAG, "GoogleApiClient connection failed. Unable to resolve.")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fireContactDetected(contact: Contact) {
        contactDetectedListener.onContactDetected(contact)
    }

    private fun fireContactLost(contact: Contact) {
        contactDetectedListener.onContactLost(contact)
    }

    companion object {

        private val TAG = NearbyActivity::class.java.simpleName
        private const val REQUEST_RESOLVE_ERROR = 1
    }
}
