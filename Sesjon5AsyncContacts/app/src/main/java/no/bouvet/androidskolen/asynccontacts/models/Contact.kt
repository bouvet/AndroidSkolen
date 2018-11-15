package no.bouvet.androidskolen.asynccontacts.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.Gson

@Entity
class Contact(
        val name: String,
        val email: String,
        val telephone: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    override fun toString(): String {
        return name
    }

    fun toJson(): String {
        return gson.toJson(this)
    }

    companion object {

        private val gson = Gson()

        fun fromJson(json: String): Contact {
            return gson.fromJson(json, Contact::class.java)
        }
    }

}
