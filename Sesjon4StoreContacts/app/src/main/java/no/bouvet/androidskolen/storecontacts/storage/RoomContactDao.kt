package no.bouvet.androidskolen.storecontacts.storage

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import no.bouvet.androidskolen.storecontacts.models.Contact

@Dao
interface RoomContactDao {

    @Query("SELECT * FROM contact WHERE id = :id")
    fun get(id : Int) : Contact?

    @Query("SELECT * FROM contact")
    fun all() : List<Contact>

    @Insert
    fun save(contact: Contact)

}

