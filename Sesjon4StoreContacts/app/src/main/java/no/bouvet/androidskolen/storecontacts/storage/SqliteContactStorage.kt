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
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
                db.execSQL(DROP_TABLE)
                onCreate(db)
    }

    override fun get(id: Int): Contact? {
        var contact : Contact? = null

        // TODO: Oppgave 2: Query database for Cursor, hent ut data

        return contact;
    }

    override fun all(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        // TODO: Oppgave 2: Query database for Cursor, hent ut data

        return contacts
    }

    override fun save(contact: Contact) {

        // TODO: Oppgave 2: Insert med ContentValues

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