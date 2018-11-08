package no.bouvet.androidskolen.storecontacts.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import no.bouvet.androidskolen.storecontacts.models.Contact

class SqliteContactStorage(val context: Context) : ContactStorage,
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    val listeners = mutableListOf<() -> Unit>()

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = """CREATE TABLE $TABLE_NAME (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $NAME TEXT,
                $EMAIL TEXT,
                $TELEPHONE TEXT);"""
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    override fun get(id: Int): Contact? {
        var contact : Contact? = null
        val db = readableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $ID = $id"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val foundId = cursor.getInt(cursor.getColumnIndex(ID))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val email = cursor.getString(cursor.getColumnIndex(EMAIL))
                val telephone = cursor.getString(cursor.getColumnIndex(TELEPHONE))
                contact = Contact(name, email, telephone)
                contact.id = foundId
            }
        }
        cursor.close()
        return contact;
    }

    override fun all(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = readableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val email = cursor.getString(cursor.getColumnIndex(EMAIL))
                val telephone = cursor.getString(cursor.getColumnIndex(TELEPHONE))
                val contact = Contact(name, email, telephone)
                contact.id = id
                contacts.add(contact)
            }
        }
        cursor.close()
        return contacts
    }

    override fun save(contact: Contact) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(NAME, contact.name)
        values.put(EMAIL, contact.email)
        values.put(TELEPHONE, contact.telephone)
        db.insert(TABLE_NAME, null, values)
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

    companion object {

        private val DB_VERSION = 1
        private val DB_NAME = "ContactsDb"
        private val TABLE_NAME = "Contacts"
        private val ID = "Id"
        private val NAME = "Name"
        private val EMAIL = "Email"
        private val TELEPHONE = "Telephone"
    }
}