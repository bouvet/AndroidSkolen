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
            val contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val phones = content.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + contactId, null, null)
            val aPhoneNumber = if (phones.moveToFirst()) {
                phones.getString(phones.getColumnIndex(Phone.NUMBER))
            } else ""
            val emails = content.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null)
            val anEmail = if (emails.moveToFirst()) {
                emails.getString(emails.getColumnIndex(Email.ADDRESS))
            } else ""
            contact = Contact(name, anEmail, aPhoneNumber)
            contact.id = contactId
        }
        cursor.close()
        return contact
    }

    override fun all() : List<Contact> {
        val contacts = mutableListOf<Contact>()
        val content = context.contentResolver
        val cursor = content.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (cursor.moveToNext()) {
            val contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val phones = content.query(Phone.CONTENT_URI, null,Phone.CONTACT_ID + " = " + contactId, null, null)
            val aPhoneNumber = if (phones.moveToFirst()) {
                phones.getString(phones.getColumnIndex(Phone.NUMBER))
            } else ""
            val emails = content.query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = " + contactId, null, null)
            val anEmail = if (emails.moveToFirst()) {
                emails.getString(emails.getColumnIndex(Email.ADDRESS))
            } else ""
            val contact = Contact(name, anEmail, aPhoneNumber)
            contact.id = contactId
            contacts.add(contact)
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

        operations.add(ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(CommonDataKinds.Email.DATA, contact.email)
            .withValue(Data.MIMETYPE, CommonDataKinds.Email.CONTENT_ITEM_TYPE)
            .withValue(CommonDataKinds.Email.TYPE, "2")
            .build());

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