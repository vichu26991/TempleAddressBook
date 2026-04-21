package com.snuggy.templeaddressbook.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.snuggy.templeaddressbook.ui.contacts.AppliedContactFilters
import com.snuggy.templeaddressbook.ui.contacts.ContactDraft
import com.snuggy.templeaddressbook.ui.contacts.ContactFilterOptions
import com.snuggy.templeaddressbook.ui.contacts.ContactRecord
import com.snuggy.templeaddressbook.ui.contacts.SmartGroupRecord

class TempleDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_CONTACTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                primary_phone TEXT NOT NULL,
                phone_label TEXT NOT NULL,
                village_town TEXT NOT NULL,
                district TEXT NOT NULL,
                state_name TEXT NOT NULL,
                country_name TEXT NOT NULL,
                tags TEXT NOT NULL,
                is_favorite INTEGER NOT NULL DEFAULT 0,
                photo_uri TEXT,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_SMART_GROUPS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                countries TEXT NOT NULL,
                states TEXT NOT NULL,
                districts TEXT NOT NULL,
                village_towns TEXT NOT NULL,
                tags TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        seedContacts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SMART_GROUPS")
        onCreate(db)
    }

    fun getContacts(): List<ContactRecord> {
        val contacts = mutableListOf<ContactRecord>()
        readableDatabase.query(
            TABLE_CONTACTS,
            null,
            null,
            null,
            null,
            null,
            "first_name COLLATE NOCASE ASC, last_name COLLATE NOCASE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                contacts += ContactRecord(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                    lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                    primaryPhone = cursor.getString(cursor.getColumnIndexOrThrow("primary_phone")),
                    phoneLabel = cursor.getString(cursor.getColumnIndexOrThrow("phone_label")),
                    villageTown = cursor.getString(cursor.getColumnIndexOrThrow("village_town")),
                    district = cursor.getString(cursor.getColumnIndexOrThrow("district")),
                    state = cursor.getString(cursor.getColumnIndexOrThrow("state_name")),
                    country = cursor.getString(cursor.getColumnIndexOrThrow("country_name")),
                    tags = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("tags"))),
                    isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    photoUri = cursor.getString(cursor.getColumnIndexOrThrow("photo_uri"))
                )
            }
        }
        return contacts
    }

    fun insertContact(draft: ContactDraft): Long {
        return writableDatabase.insert(TABLE_CONTACTS, null, draft.toValues())
    }

    fun updateFavorite(contactId: Long, isFavorite: Boolean) {
        writableDatabase.update(
            TABLE_CONTACTS,
            ContentValues().apply { put("is_favorite", if (isFavorite) 1 else 0) },
            "id = ?",
            arrayOf(contactId.toString())
        )
    }

    fun saveSmartGroup(name: String, filters: AppliedContactFilters): Long {
        val values = ContentValues().apply {
            put("name", name)
            put("countries", filters.countries.joinToString(TAG_SEPARATOR))
            put("states", filters.states.joinToString(TAG_SEPARATOR))
            put("districts", filters.districts.joinToString(TAG_SEPARATOR))
            put("village_towns", filters.villageTowns.joinToString(TAG_SEPARATOR))
            put("tags", filters.tags.joinToString(TAG_SEPARATOR))
            put("created_at", System.currentTimeMillis())
        }
        return writableDatabase.insert(TABLE_SMART_GROUPS, null, values)
    }

    fun getSmartGroups(): List<SmartGroupRecord> {
        val groups = mutableListOf<SmartGroupRecord>()
        readableDatabase.query(
            TABLE_SMART_GROUPS,
            null,
            null,
            null,
            null,
            null,
            "created_at DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                groups += SmartGroupRecord(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    filters = AppliedContactFilters(
                        countries = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("countries"))).toSet(),
                        states = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("states"))).toSet(),
                        districts = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("districts"))).toSet(),
                        villageTowns = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("village_towns"))).toSet(),
                        tags = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("tags"))).toSet(),
                    ),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                )
            }
        }
        return groups
    }

    fun getFilterOptions(): ContactFilterOptions {
        val contacts = getContacts()
        return ContactFilterOptions(
            countries = contacts.map { it.country }.distinct().sorted(),
            states = contacts.map { it.state }.distinct().sorted(),
            districts = contacts.map { it.district }.distinct().sorted(),
            villageTowns = contacts.map { it.villageTown }.distinct().sorted(),
            tags = contacts.flatMap { it.tags }.distinct().sorted()
        )
    }

    private fun ContactDraft.toValues(): ContentValues = ContentValues().apply {
        put("first_name", firstName.trim())
        put("last_name", lastName.trim())
        put("primary_phone", primaryPhone.trim())
        put("phone_label", phoneLabel.trim())
        put("village_town", villageTown.trim())
        put("district", district.trim())
        put("state_name", state.trim())
        put("country_name", country.trim())
        put("tags", tags.filter { it.isNotBlank() }.joinToString(TAG_SEPARATOR))
        put("is_favorite", if (isFavorite) 1 else 0)
        put("photo_uri", photoUri)
        put("created_at", System.currentTimeMillis())
    }

    private fun seedContacts(db: SQLiteDatabase) {
        val namePrefixes = listOf(
            "Aadhi", "Bala", "Chitra", "Dinesh", "Elango", "Farook", "Gokul", "Hari", "Indhu", "Jeeva",
            "Karthik", "Lakshmi", "Madhan", "Nandha", "Omkar", "Prabhu", "Qadir", "Raja", "Selvam", "Tamil",
            "Udhaya", "Velu", "Winson", "Xavier", "Yazhini", "Zubin"
        )
        val lastNames = listOf(
            "Arun", "Bala", "Chitra", "Devi", "Eswar", "Ganesh", "Hari", "Jothi", "Kumar", "Lakshmi"
        )
        val districtVillageMap = listOf(
            "Namakkal" to listOf("Rasipuram", "Puduchatram", "Mohanur"),
            "Salem" to listOf("Attur", "Omalur", "Edappadi"),
            "Erode" to listOf("Gobichettipalayam", "Perundurai", "Bhavani"),
            "Karur" to listOf("Kulithalai", "Krishnarayapuram", "Aravakurichi"),
            "Tiruchirappalli" to listOf("Srirangam", "Manapparai", "Thuraiyur")
        )
        val tagPool = listOf(
            "Thai Amavasai", "Committee", "Senior Donor", "New Family", "Festival",
            "June Kattalai", "Nirvaga", "Annadhanam", "Donor", "Temple Family"
        )

        namePrefixes.forEachIndexed { letterIndex, prefix ->
            repeat(10) { index ->
                val (district, villages) = districtVillageMap[(letterIndex + index) % districtVillageMap.size]
                val village = villages[index % villages.size]
                val tags = buildList {
                    add(tagPool[(letterIndex + index) % tagPool.size])
                    if (index % 3 == 0) add(tagPool[(letterIndex + index + 3) % tagPool.size])
                    if (index % 5 == 0) add(tagPool[(letterIndex + index + 5) % tagPool.size])
                }.distinct()

                val serial = letterIndex * 10 + index + 1
                val values = ContentValues().apply {
                    put("first_name", prefix)
                    put("last_name", lastNames[index % lastNames.size])
                    put("primary_phone", "9${(100000000 + serial * 78123).toString().take(9)}")
                    put("phone_label", listOf("Personal", "Office", "Home", "Landline")[serial % 4])
                    put("village_town", village)
                    put("district", district)
                    put("state_name", "Tamil Nadu")
                    put("country_name", "India")
                    put("tags", tags.joinToString(TAG_SEPARATOR))
                    put("is_favorite", if (serial % 9 == 0) 1 else 0)
                    put("created_at", System.currentTimeMillis() - (serial * 86_400_000L))
                }
                db.insert(TABLE_CONTACTS, null, values)
            }
        }
    }

    companion object {
        private const val DB_NAME = "temple_address_book.db"
        private const val DB_VERSION = 3
        const val SCHEMA_VERSION = DB_VERSION
        private const val TABLE_CONTACTS = "contacts"
        private const val TABLE_SMART_GROUPS = "smart_groups"
        private const val TAG_SEPARATOR = "|"

        private fun splitTags(raw: String?): List<String> = raw
            ?.split(TAG_SEPARATOR)
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }
}
