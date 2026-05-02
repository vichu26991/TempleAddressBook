package com.snuggy.templeaddressbook.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.snuggy.templeaddressbook.ui.contacts.AppliedContactFilters
import com.snuggy.templeaddressbook.ui.contacts.ContactDraft
import com.snuggy.templeaddressbook.ui.contacts.ContactEmailRecord
import com.snuggy.templeaddressbook.ui.contacts.ContactPhoneRecord
import com.snuggy.templeaddressbook.ui.contacts.ContactFilterOptions
import com.snuggy.templeaddressbook.ui.contacts.ContactRecord
import com.snuggy.templeaddressbook.ui.contacts.SmartGroupRecord
import com.snuggy.templeaddressbook.ui.contacts.TagRecord
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

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
                gender TEXT NOT NULL DEFAULT '',
                dob TEXT NOT NULL DEFAULT '',
                rasi TEXT NOT NULL DEFAULT '',
                nakshatra TEXT NOT NULL DEFAULT '',
                door_no TEXT NOT NULL DEFAULT '',
                building_name TEXT NOT NULL DEFAULT '',
                street_name TEXT NOT NULL DEFAULT '',
                area TEXT NOT NULL DEFAULT '',
                post_office TEXT NOT NULL DEFAULT '',
                taluk TEXT NOT NULL DEFAULT '',
                pin_code TEXT NOT NULL DEFAULT '',
                google_map_link TEXT NOT NULL DEFAULT '',
                notes TEXT NOT NULL DEFAULT '',
                phones_data TEXT NOT NULL DEFAULT '',
                emails_data TEXT NOT NULL DEFAULT '',
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

        createTagTables(db)
        seedContacts(db)
        migrateLegacyTagsToMaster(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            addColumnIfMissing(db, TABLE_CONTACTS, "gender", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "dob", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "rasi", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "nakshatra", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "door_no", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "building_name", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "street_name", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "area", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "post_office", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "taluk", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "pin_code", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "google_map_link", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "notes", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "phones_data", "TEXT NOT NULL DEFAULT ''")
            addColumnIfMissing(db, TABLE_CONTACTS, "emails_data", "TEXT NOT NULL DEFAULT ''")
        }
        if (oldVersion < 5) {
            createTagTables(db)
            migrateLegacyTagsToMaster(db)
        }
    }

    private fun addColumnIfMissing(db: SQLiteDatabase, tableName: String, columnName: String, columnDefinition: String) {
        val exists = db.rawQuery("PRAGMA table_info($tableName)", null).use { cursor ->
            var found = false
            while (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name")) == columnName) {
                    found = true
                    break
                }
            }
            found
        }
        if (!exists) {
            db.execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $columnDefinition")
        }
    }

    fun getContacts(): List<ContactRecord> {
        val contacts = mutableListOf<ContactRecord>()
        val legacyTagMap = mutableMapOf<Long, List<String>>()
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
                val contactId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val legacyTags = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")))
                legacyTagMap[contactId] = legacyTags
                contacts += ContactRecord(
                    id = contactId,
                    firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                    lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                    primaryPhone = cursor.getString(cursor.getColumnIndexOrThrow("primary_phone")),
                    phoneLabel = cursor.getString(cursor.getColumnIndexOrThrow("phone_label")),
                    villageTown = cursor.getString(cursor.getColumnIndexOrThrow("village_town")),
                    district = cursor.getString(cursor.getColumnIndexOrThrow("district")),
                    state = cursor.getString(cursor.getColumnIndexOrThrow("state_name")),
                    country = cursor.getString(cursor.getColumnIndexOrThrow("country_name")),
                    tags = legacyTags,
                    isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) == 1,
                    photoUri = cursor.getStringOrNullSafe("photo_uri"),
                    gender = cursor.getStringOrEmpty("gender"),
                    dob = cursor.getStringOrEmpty("dob"),
                    rasi = cursor.getStringOrEmpty("rasi"),
                    nakshatra = cursor.getStringOrEmpty("nakshatra"),
                    doorNo = cursor.getStringOrEmpty("door_no"),
                    buildingName = cursor.getStringOrEmpty("building_name"),
                    streetName = cursor.getStringOrEmpty("street_name"),
                    area = cursor.getStringOrEmpty("area"),
                    postOffice = cursor.getStringOrEmpty("post_office"),
                    taluk = cursor.getStringOrEmpty("taluk"),
                    pinCode = cursor.getStringOrEmpty("pin_code"),
                    googleMapLink = cursor.getStringOrEmpty("google_map_link"),
                    notes = cursor.getStringOrEmpty("notes"),
                    phoneNumbers = decodePhones(cursor.getStringOrEmpty("phones_data")),
                    emailAddresses = decodeEmails(cursor.getStringOrEmpty("emails_data"))
                )
            }
        }
        val tagMap = getTagsForContactIds(contacts.map { it.id })
        return contacts.map { contact ->
            val mappedTags = tagMap[contact.id].orEmpty()
            contact.copy(tags = mappedTags.ifEmpty { legacyTagMap[contact.id].orEmpty() })
        }
    }

    fun insertContact(draft: ContactDraft): Long {
        val contactId = writableDatabase.insert(TABLE_CONTACTS, null, draft.toValues(includeCreatedAt = true))
        if (contactId > 0) setContactTags(contactId, draft.tags)
        return contactId
    }

    fun updateContact(contactId: Long, draft: ContactDraft): Int {
        val updated = writableDatabase.update(
            TABLE_CONTACTS,
            draft.toValues(includeCreatedAt = false),
            "id = ?",
            arrayOf(contactId.toString())
        )
        if (updated > 0) setContactTags(contactId, draft.tags)
        return updated
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
            countries = contacts.map { it.country }.filter { it.isNotBlank() }.distinct().sorted(),
            states = contacts.map { it.state }.filter { it.isNotBlank() }.distinct().sorted(),
            districts = contacts.map { it.district }.filter { it.isNotBlank() }.distinct().sorted(),
            villageTowns = contacts.map { it.villageTown }.filter { it.isNotBlank() }.distinct().sorted(),
            tags = getTags().map { it.name }
        )
    }

    fun getTags(): List<TagRecord> {
        ensureTagTables(readableDatabase)
        val usageCounts = mutableMapOf<Long, Int>()
        readableDatabase.rawQuery(
            "SELECT tag_id, COUNT(contact_id) AS usage_count FROM $TABLE_CONTACT_TAGS GROUP BY tag_id",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                usageCounts[cursor.getLong(cursor.getColumnIndexOrThrow("tag_id"))] =
                    cursor.getInt(cursor.getColumnIndexOrThrow("usage_count"))
            }
        }
        val tags = mutableListOf<TagRecord>()
        readableDatabase.query(
            TABLE_TAGS,
            null,
            null,
            null,
            null,
            null,
            "name COLLATE NOCASE ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                tags += TagRecord(
                    id = id,
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    usageCount = usageCounts[id] ?: 0,
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                    updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow("updated_at"))
                )
            }
        }
        return tags
    }

    fun createTag(name: String): Long {
        val normalized = name.trim()
        if (normalized.isBlank()) return -1L
        findTagIdByName(writableDatabase, normalized)?.let { return it }
        val now = System.currentTimeMillis()
        return writableDatabase.insert(TABLE_TAGS, null, ContentValues().apply {
            put("name", normalized)
            put("created_at", now)
            put("updated_at", now)
        })
    }

    fun renameTag(tagId: Long, newName: String): Boolean {
        val normalized = newName.trim()
        if (tagId <= 0 || normalized.isBlank()) return false
        val duplicateId = findTagIdByName(writableDatabase, normalized)
        if (duplicateId != null && duplicateId != tagId) return false
        val updated = writableDatabase.update(
            TABLE_TAGS,
            ContentValues().apply {
                put("name", normalized)
                put("updated_at", System.currentTimeMillis())
            },
            "id = ?",
            arrayOf(tagId.toString())
        )
        syncLegacyTagsFromMappings(writableDatabase)
        return updated > 0
    }

    fun deleteTag(tagId: Long): Boolean {
        if (tagId <= 0) return false
        writableDatabase.delete(TABLE_CONTACT_TAGS, "tag_id = ?", arrayOf(tagId.toString()))
        val deleted = writableDatabase.delete(TABLE_TAGS, "id = ?", arrayOf(tagId.toString()))
        syncLegacyTagsFromMappings(writableDatabase)
        return deleted > 0
    }

    fun addContactsToTag(tagId: Long, contactIds: List<Long>) {
        if (tagId <= 0 || contactIds.isEmpty()) return
        val db = writableDatabase
        db.beginTransaction()
        try {
            contactIds.distinct().forEach { contactId ->
                db.insertWithOnConflict(
                    TABLE_CONTACT_TAGS,
                    null,
                    ContentValues().apply {
                        put("contact_id", contactId)
                        put("tag_id", tagId)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        syncLegacyTagsFromMappings(db)
    }

    fun removeContactFromTag(contactId: Long, tagId: Long) {
        writableDatabase.delete(
            TABLE_CONTACT_TAGS,
            "contact_id = ? AND tag_id = ?",
            arrayOf(contactId.toString(), tagId.toString())
        )
        syncLegacyTagsFromMappings(writableDatabase)
    }

    private fun createTagTables(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_TAGS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL COLLATE NOCASE UNIQUE,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CONTACT_TAGS (
                contact_id INTEGER NOT NULL,
                tag_id INTEGER NOT NULL,
                PRIMARY KEY(contact_id, tag_id),
                FOREIGN KEY(contact_id) REFERENCES $TABLE_CONTACTS(id) ON DELETE CASCADE,
                FOREIGN KEY(tag_id) REFERENCES $TABLE_TAGS(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_contact_tags_contact ON $TABLE_CONTACT_TAGS(contact_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_contact_tags_tag ON $TABLE_CONTACT_TAGS(tag_id)")
    }

    private fun ensureTagTables(db: SQLiteDatabase) {
        createTagTables(db)
    }

    private fun migrateLegacyTagsToMaster(db: SQLiteDatabase) {
        createTagTables(db)
        db.query(TABLE_CONTACTS, arrayOf("id", "tags"), null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val legacyTags = splitTags(cursor.getString(cursor.getColumnIndexOrThrow("tags")))
                legacyTags.forEach { tagName ->
                    val tagId = findOrCreateTagId(db, tagName)
                    if (tagId > 0) {
                        db.insertWithOnConflict(
                            TABLE_CONTACT_TAGS,
                            null,
                            ContentValues().apply {
                                put("contact_id", contactId)
                                put("tag_id", tagId)
                            },
                            SQLiteDatabase.CONFLICT_IGNORE
                        )
                    }
                }
            }
        }
    }

    private fun setContactTags(contactId: Long, tagNames: List<String>) {
        val db = writableDatabase
        val cleaned = tagNames.map { it.trim() }.filter { it.isNotBlank() }.distinctBy { it.lowercase(Locale.ROOT) }
        db.beginTransaction()
        try {
            db.delete(TABLE_CONTACT_TAGS, "contact_id = ?", arrayOf(contactId.toString()))
            cleaned.forEach { tagName ->
                val tagId = findOrCreateTagId(db, tagName)
                if (tagId > 0) {
                    db.insertWithOnConflict(
                        TABLE_CONTACT_TAGS,
                        null,
                        ContentValues().apply {
                            put("contact_id", contactId)
                            put("tag_id", tagId)
                        },
                        SQLiteDatabase.CONFLICT_IGNORE
                    )
                }
            }
            db.update(
                TABLE_CONTACTS,
                ContentValues().apply { put("tags", cleaned.joinToString(TAG_SEPARATOR)) },
                "id = ?",
                arrayOf(contactId.toString())
            )
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun findOrCreateTagId(db: SQLiteDatabase, tagName: String): Long {
        val normalized = tagName.trim()
        if (normalized.isBlank()) return -1L
        findTagIdByName(db, normalized)?.let { return it }
        val now = System.currentTimeMillis()
        return db.insertWithOnConflict(
            TABLE_TAGS,
            null,
            ContentValues().apply {
                put("name", normalized)
                put("created_at", now)
                put("updated_at", now)
            },
            SQLiteDatabase.CONFLICT_IGNORE
        ).takeIf { it > 0 } ?: findTagIdByName(db, normalized).orNegativeOne()
    }

    private fun Long?.orNegativeOne(): Long = this ?: -1L

    private fun findTagIdByName(db: SQLiteDatabase, tagName: String): Long? {
        db.query(
            TABLE_TAGS,
            arrayOf("id"),
            "name = ? COLLATE NOCASE",
            arrayOf(tagName.trim()),
            null,
            null,
            null,
            "1"
        ).use { cursor ->
            if (cursor.moveToFirst()) return cursor.getLong(cursor.getColumnIndexOrThrow("id"))
        }
        return null
    }

    private fun getTagsForContactIds(contactIds: List<Long>): Map<Long, List<String>> {
        if (contactIds.isEmpty()) return emptyMap()
        ensureTagTables(readableDatabase)
        val result = linkedMapOf<Long, MutableList<String>>()
        val idSet = contactIds.toSet()
        readableDatabase.rawQuery(
            """
            SELECT ct.contact_id, t.name
            FROM $TABLE_CONTACT_TAGS ct
            INNER JOIN $TABLE_TAGS t ON t.id = ct.tag_id
            ORDER BY t.name COLLATE NOCASE ASC
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(cursor.getColumnIndexOrThrow("contact_id"))
                if (contactId in idSet) {
                    result.getOrPut(contactId) { mutableListOf() }
                        .add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                }
            }
        }
        return result
    }

    private fun syncLegacyTagsFromMappings(db: SQLiteDatabase) {
        val mapped = mutableMapOf<Long, MutableList<String>>()
        db.rawQuery(
            """
            SELECT ct.contact_id, t.name
            FROM $TABLE_CONTACT_TAGS ct
            INNER JOIN $TABLE_TAGS t ON t.id = ct.tag_id
            ORDER BY t.name COLLATE NOCASE ASC
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                mapped.getOrPut(cursor.getLong(cursor.getColumnIndexOrThrow("contact_id"))) { mutableListOf() }
                    .add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
            }
        }
        db.query(TABLE_CONTACTS, arrayOf("id"), null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                db.update(
                    TABLE_CONTACTS,
                    ContentValues().apply { put("tags", mapped[contactId].orEmpty().joinToString(TAG_SEPARATOR)) },
                    "id = ?",
                    arrayOf(contactId.toString())
                )
            }
        }
    }

    private fun ContactDraft.toValues(includeCreatedAt: Boolean): ContentValues = ContentValues().apply {
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
        put("gender", gender.trim())
        put("dob", dob.trim())
        put("rasi", rasi.trim())
        put("nakshatra", nakshatra.trim())
        put("door_no", doorNo.trim())
        put("building_name", buildingName.trim())
        put("street_name", streetName.trim())
        put("area", area.trim())
        put("post_office", postOffice.trim())
        put("taluk", taluk.trim())
        put("pin_code", pinCode.trim())
        put("google_map_link", googleMapLink.trim())
        put("notes", notes.trim())
        put("phones_data", encodePhones(phoneNumbers))
        put("emails_data", encodeEmails(emailAddresses))
        if (includeCreatedAt) {
            put("created_at", System.currentTimeMillis())
        }
    }

    private fun android.database.Cursor.getStringOrEmpty(columnName: String): String {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getString(index).orEmpty() else ""
    }

    private fun android.database.Cursor.getStringOrNullSafe(columnName: String): String? {
        val index = getColumnIndex(columnName)
        return if (index >= 0 && !isNull(index)) getString(index) else null
    }

    private fun encodePhones(phones: List<ContactPhoneRecord>): String {
        if (phones.isEmpty()) return ""
        return JSONArray().apply {
            phones.filter { it.displayNumber.isNotBlank() || it.localNumber.isNotBlank() }.forEach { phone ->
                put(JSONObject().apply {
                    put("countryName", phone.countryName)
                    put("countryCode", phone.countryCode)
                    put("countryFlag", phone.countryFlag)
                    put("countryCompactLabel", phone.countryCompactLabel)
                    put("localNumber", phone.localNumber)
                    put("fullNumber", phone.displayNumber)
                    put("label", phone.label)
                    put("isPrimary", phone.isPrimary)
                    put("isWhatsApp", phone.isWhatsApp)
                })
            }
        }.toString()
    }

    private fun decodePhones(raw: String): List<ContactPhoneRecord> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val phone = ContactPhoneRecord(
                        countryName = item.optString("countryName"),
                        countryCode = item.optString("countryCode"),
                        countryFlag = item.optString("countryFlag"),
                        countryCompactLabel = item.optString("countryCompactLabel"),
                        localNumber = item.optString("localNumber"),
                        fullNumber = item.optString("fullNumber"),
                        label = item.optString("label"),
                        isPrimary = item.optBoolean("isPrimary"),
                        isWhatsApp = item.optBoolean("isWhatsApp")
                    )
                    if (phone.displayNumber.isNotBlank()) add(phone)
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun encodeEmails(emails: List<ContactEmailRecord>): String {
        if (emails.isEmpty()) return ""
        return JSONArray().apply {
            emails.filter { it.email.isNotBlank() }.forEach { email ->
                put(JSONObject().apply {
                    put("email", email.email)
                    put("label", email.label)
                    put("isPrimary", email.isPrimary)
                })
            }
        }.toString()
    }

    private fun decodeEmails(raw: String): List<ContactEmailRecord> {
        if (raw.isBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    val email = ContactEmailRecord(
                        email = item.optString("email"),
                        label = item.optString("label"),
                        isPrimary = item.optBoolean("isPrimary")
                    )
                    if (email.email.isNotBlank()) add(email)
                }
            }
        }.getOrDefault(emptyList())
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
        private const val DB_VERSION = 5
        const val SCHEMA_VERSION = DB_VERSION
        private const val TABLE_CONTACTS = "contacts"
        private const val TABLE_SMART_GROUPS = "smart_groups"
        private const val TABLE_TAGS = "tags"
        private const val TABLE_CONTACT_TAGS = "contact_tags"
        private const val TAG_SEPARATOR = "|"

        private fun splitTags(raw: String?): List<String> = raw
            ?.split(TAG_SEPARATOR)
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            .orEmpty()
    }
}
