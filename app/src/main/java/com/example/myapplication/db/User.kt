package com.example.myapplication.db

import android.database.sqlite.SQLiteDatabase
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.mutableListOf

lateinit var dbHelper: Database
private var database: SQLiteDatabase? = null
@Parcelize
data class User(
    val id: Int? = 0,
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val dateJoined: LocalDateTime? = null,
    val active: Boolean? = false
) : Parcelable {

    fun getFormattedDate(): String =
        dateJoined?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) ?: "—"
}

fun getUserIdByEmail(dbHelper: Database, email: String?): Int {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_USERS,
        arrayOf(Database.COLUMN_USER_ID),     // выбираем только id
        "${Database.COLUMN_EMAIL} = ?",       // WHERE email = ?
        arrayOf(email),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_USER_ID))
        } else {
            0
        }
    }
}

fun getUserIdInPreferencesByEmail(dbHelper: Database, email: String?): Int {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase
    val id = getUserIdByEmail(dbHelper, email)

    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf("COUNT(*)"),
        "${Database.COLUMN_USER_ID} = ?",
        arrayOf(id.toString()),
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getInt(0)
        } else {
            0
        }
    }
}

fun getPassByEmail(dbHelper: Database, email: String?): String? {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_USERS,
        arrayOf(Database.COLUMN_PASSWORD),     // выбираем только id
        "${Database.COLUMN_EMAIL} = ?",       // WHERE email = ?
        arrayOf(email),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_PASSWORD))
        } else {
            null
        }
    }
}

fun getIsEmailExists(dbHelper: Database, email: String?): Int {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_USERS,
        arrayOf("COUNT(*)"),
        "${Database.COLUMN_EMAIL} = ?",
        arrayOf(email),
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getInt(0)
        } else {
            0
        }
    }
}

fun getNameByEmail(dbHelper: Database, email: String?): String? {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_USERS,
        arrayOf(Database.COLUMN_USERNAME),     // выбираем только id
        "${Database.COLUMN_EMAIL} = ?",       // WHERE email = ?
        arrayOf(email),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_USERNAME))
        } else {
            null
        }
    }
}

fun getDietByEmail(dbHelper: Database, email: String?): String? {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val id = getUserIdByEmail(dbHelper, email)

    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf(Database.COLUMN_DIETARY_RESTRICTIONS),     // выбираем только id
        "${Database.COLUMN_USER_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_DIETARY_RESTRICTIONS))
        } else {
            null
        }
    }
}

fun getCuisinesByEmail(dbHelper: Database, email: String?): String? {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val id = getUserIdByEmail(dbHelper, email)

    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf(Database.COLUMN_FAVORITE_CUISINES),     // выбираем только id
        "${Database.COLUMN_USER_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_FAVORITE_CUISINES))
        } else {
            null
        }
    }
}

fun getAllergiesByEmail(dbHelper: Database, email: String?): String? {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val id = getUserIdByEmail(dbHelper, email)

    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf(Database.COLUMN_ALLERGIES),     // выбираем только id
        "${Database.COLUMN_USER_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_ALLERGIES))
        } else {
            null
        }
    }
}

fun getSkillsLevelByEmail(dbHelper: Database, email: String?): String {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val id = getUserIdByEmail(dbHelper, email)

    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf(Database.COLUMN_COOKING_SKILL_LEVEL),     // выбираем только id
        "${Database.COLUMN_USER_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    var skillSelected = ""

    var skills = ""
    cursor.use { c ->
        if (c.moveToFirst()) {
            skills = c.getString(c.getColumnIndexOrThrow(Database.COLUMN_COOKING_SKILL_LEVEL))
        } else {
            null
        }
    }

    if (skills == "1") {
        skillSelected = "Новичок"
    }
    else if (skills == "2") {
        skillSelected = "Средний"
    }
    else if (skills == "3") {
        skillSelected = "Продвинутый"
    }

    return  skillSelected
}

fun getAllNames(dbHelper: Database): MutableList<User> {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase
    val users = mutableListOf<User>()

    val cursor = db.query(
        Database.TABLE_USERS,
        arrayOf(Database.COLUMN_USERNAME, Database.COLUMN_EMAIL),
        null,
        null,
        null, null, null
    )

    cursor.use { c ->
        val idxUsername = c.getColumnIndexOrThrow(Database.COLUMN_USERNAME)
        val idxEmail    = c.getColumnIndexOrThrow(Database.COLUMN_EMAIL)
        // val idxId       = c.getColumnIndexOrThrow(Database.COLUMN_ID)

        while (c.moveToNext()) {
            val user = User(
                name = c.getString(idxUsername),
                email = c.getString(idxEmail),
                // id       = c.getLong(idxId),
            )
            users.add(user)
        }
    }
    return users
}



