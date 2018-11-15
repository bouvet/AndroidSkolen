package no.bouvet.androidskolen.asynccontacts.storage

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import no.bouvet.androidskolen.asynccontacts.models.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class RoomContactsDatabase : RoomDatabase() {

    abstract fun dao() : RoomContactDao

    companion object {
        @Volatile
        private var INSTANCE: RoomContactsDatabase? = null

        fun getDatabase(context: Context): RoomContactsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomContactsDatabase::class.java,
                        "contact_database"
                        )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        .fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                instance
            }
        }

    }

}
