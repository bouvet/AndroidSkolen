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
//            return FileContactStorage(context)
//            return SharedPreferencesContactStorage(context)
//            return SqliteContactStorage(context)
//            return RoomContactStorage(context)
            return ContentProviderContactStorage(context)
        }


    }

}