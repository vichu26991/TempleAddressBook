package com.snuggy.templeaddressbook.ui.contacts

import android.content.Context
import com.snuggy.templeaddressbook.data.TempleDbHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(context: Context) {
    private val dbHelper = TempleDbHelper(context.applicationContext)

    suspend fun getContacts(): List<ContactRecord> = withContext(Dispatchers.IO) {
        dbHelper.getContacts()
    }

    suspend fun getFilterOptions(): ContactFilterOptions = withContext(Dispatchers.IO) {
        dbHelper.getFilterOptions()
    }

    suspend fun toggleFavorite(contactId: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        dbHelper.updateFavorite(contactId, isFavorite)
    }

    suspend fun addContact(draft: ContactDraft): Long = withContext(Dispatchers.IO) {
        dbHelper.insertContact(draft)
    }

    suspend fun updateContact(contactId: Long, draft: ContactDraft): Int = withContext(Dispatchers.IO) {
        dbHelper.updateContact(contactId, draft)
    }

    suspend fun saveSmartGroup(name: String, filters: AppliedContactFilters): Long = withContext(Dispatchers.IO) {
        dbHelper.saveSmartGroup(name, filters)
    }
}
