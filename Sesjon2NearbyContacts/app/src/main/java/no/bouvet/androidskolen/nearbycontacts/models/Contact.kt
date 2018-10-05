package no.bouvet.androidskolen.nearbycontacts.models

import com.google.gson.Gson

class Contact(val name: String, val email: String, val telephone: String) {

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
