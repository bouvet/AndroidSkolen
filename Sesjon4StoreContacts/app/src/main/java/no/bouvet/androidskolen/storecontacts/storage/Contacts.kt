package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context

class Contacts {

    companion object {

        @Volatile
        private var INSTANCE : ContactStorage? = null

        fun getStorage(context: Context) : ContactStorage {
            return INSTANCE ?: synchronized(this) {
                val instance = createStorage(context)
                INSTANCE = instance
                return instance
            }
        }

        private fun createStorage(context: Context): ContactStorage {
            return FileContactStorage(context)
            // TODO: Oppgave 1
//            return SharedPreferencesContactStorage(context)
            // TODO: Oppgave 2
//            return SqliteContactStorage(context)
            // TODO: Oppgave 3
//            return RoomContactStorage(context)
            // TODO: Oppgave 4
//            return ContentProviderContactStorage(context)
        }


    }

}