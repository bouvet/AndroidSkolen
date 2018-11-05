package no.bouvet.androidskolen.storecontacts.storage

import android.content.Context
import no.bouvet.androidskolen.storecontacts.models.Contact
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileContactStorage(val context: Context) : ContactStorage {

    val listeners = mutableListOf<() -> Unit>()

    val storageFolder = File(context.filesDir, "contacts")
    init {
        storageFolder.mkdir()
    }

    override fun get(id: Int): Contact? {
        val contactFile = File(storageFolder, "$id")
        if (contactFile.exists()) {
            val data = FileReader(contactFile).readText()
            return Contact.fromJson(data)
        }
        return null
    }

    override fun all(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        for (contactFile in storageFolder.listFiles()) {
            val fileReader = FileReader(contactFile)
            val data = fileReader.readText()
            contacts.add(Contact.fromJson(data))
            fileReader.close()
        }
        return contacts
    }

    override fun save(contact: Contact) {
        val id = storageFolder.list().size + 1
        contact.id = id
        val json = contact.toJson()
        val fileWriter = FileWriter(File(storageFolder, "$id"))
        fileWriter.write(json)
        fileWriter.close()
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