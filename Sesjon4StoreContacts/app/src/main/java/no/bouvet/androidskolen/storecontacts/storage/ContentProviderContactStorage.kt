package no.bouvet.androidskolen.storecontacts.storage

import android.content.ContentProviderOperation
import android.provider.ContactsContract
import android.content.Context
import android.provider.ContactsContract.*
import android.provider.ContactsContract.CommonDataKinds.*
import no.bouvet.androidskolen.storecontacts.models.Contact

class ContentProviderContactStorage(val context: Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()

    override fun get(id: Int): Contact? {
        var contact : Contact? = null
        val content = context.contentResolver
        val cursor = content.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = " + id, null, null)
        if (cursor.moveToNext()) {
            // TODO: Oppgave 4 - hent ut data fra de ulike Cursorene

            val contactId = 0;

            val phones = content.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + contactId, null, null)

            val emails = content.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null)

        }
        cursor.close()
        return contact
    }

    override fun all() : List<Contact> {
        val contacts = mutableListOf<Contact>()
        val content = context.contentResolver
        val cursor = content.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (cursor.moveToNext()) {
            // TODO: Oppgave 4 - hent ut data fra de ulike Cursorene

            val contactId = 0

            val phones = content.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + contactId, null, null)

            val emails = content.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null)

        }
        cursor.close()
        return contacts;
    }

    override fun save(contact: Contact) {
        val content = context.contentResolver
        val operations = ArrayList<ContentProviderOperation>()

        operations.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
            .withValue(RawContacts.ACCOUNT_TYPE, null)
            .withValue(RawContacts.ACCOUNT_NAME, null).build());

        operations.add(ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, contact.telephone)
            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.TYPE, "1").build());

        operations.add(ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.DISPLAY_NAME, contact.name)
            .build());


        // TODO: Oppgave 4 - finn tilsvarende for å legge til en epost-adresse i operasjonene

        content.applyBatch(ContactsContract.AUTHORITY, operations)
        triggerListeners()
    }

    override fun listenToChange(listener: () -> Unit) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    private fun triggerListeners() {
        synchronized(listeners) {
            for (listener in listeners) {
                listener.invoke()
            }
        }
    }
}