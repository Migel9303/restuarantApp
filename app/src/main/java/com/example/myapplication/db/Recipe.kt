package com.example.myapplication.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Parcelable
import android.widget.Toast
import androidx.core.content.contentValuesOf
import kotlinx.parcelize.Parcelize
import java.net.IDN
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//lateinit var dbHelper: Database
private var database: SQLiteDatabase? = null

@Parcelize
data class Recipe(
    val id: Int,
    val name: String? = null,
    val cookTime: Int? = null,
    val ingredients: List<String>? = null,
    val instructions: String? = null) : Parcelable

fun getAllRecipe(dbhelper: Database): MutableList<Recipe> {
    database = dbhelper.writableDatabase
    val db = dbhelper.readableDatabase
    val recipes = mutableListOf<Recipe>()

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_RECIPE_NAME, Database.COLUMN_RECIPE_ID),
        null,
        null,
        null, null, null
    )
    cursor.use { c ->
        val idxID = c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_ID)
        val idxName = c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_NAME)

        while (c.moveToNext()) {
            val recipe = Recipe(
                id = c.getInt(idxID),
                name = c.getString(idxName)
            )
            recipes.add(recipe)
        }
    }
    return recipes
}

fun getUserRecipes(dbhelper: Database, id: Int): MutableList<Recipe> {
    database = dbhelper.writableDatabase
    val db = dbhelper.readableDatabase
    val recipes = mutableListOf<Recipe>()

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_RECIPE_NAME, Database.COLUMN_RECIPE_ID),
        "${Database.COLUMN_USER_ID} = ?",
        arrayOf(id.toString()),
        null, null, null
    )
    cursor.use { c ->
        val idxID = c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_ID)
        val idxName = c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_NAME)

        while (c.moveToNext()) {
            val recipe = Recipe(
                id = c.getInt(idxID),
                name = c.getString(idxName)
            )
            recipes.add(recipe)
        }
    }
    return recipes
}

fun insertRecipe(context: Context, dbhelper: Database, name: String, cookTime: Int?, ingredients: List<String>,
                 instructions: String, userID: Int) {
    database = dbhelper.writableDatabase
//    val db = dbHelper.readableDatabase

    val ingredientsString = ingredients.joinToString(separator = ",")
    val recipe_values = contentValuesOf(
        Database.COLUMN_RECIPE_NAME to name,
        Database.COLUMN_COOK_TIME to cookTime,
        Database.COLUMN_INGREDIENTS to ingredientsString,
        Database.COLUMN_INSTRUCTIONS to instructions,
        Database.COLUMN_USER_ID to userID
    )

    val newUserRowId = database?.insert(
        Database.TABLE_RECIPES,
        null,
        recipe_values
    )

    if (newUserRowId != null && newUserRowId != -1L) {
        Toast.makeText(context, "Блюдо успешно добавлено ✅", Toast.LENGTH_LONG).show()
    }
    database?.takeIf { it.isOpen }?.close()
}

fun getRecipeTitleByID(dbHelper: Database, id: Int): String {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_RECIPE_NAME),     // выбираем только id
        "${Database.COLUMN_RECIPE_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_NAME))
        } else {
            ""
        }
    }
}

fun getRecipeCookTimeByID(dbHelper: Database, id: Int): Int {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_COOK_TIME),     // выбираем только id
        "${Database.COLUMN_RECIPE_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_COOK_TIME))
        } else {
            0
        }
    }
}

fun getRecipeIngredientsByID(dbHelper: Database, id: Int): String {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_INGREDIENTS),     // выбираем только id
        "${Database.COLUMN_RECIPE_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_INGREDIENTS))
        } else {
            ""
        }
    }
}

fun getRecipeInstructionsByID(dbHelper: Database, id: Int): String {
    database = dbHelper.writableDatabase
    val db = dbHelper.readableDatabase

    val cursor = db.query(
        Database.TABLE_RECIPES,
        arrayOf(Database.COLUMN_INSTRUCTIONS),     // выбираем только id
        "${Database.COLUMN_RECIPE_ID} = ?",       // WHERE email = ?
        arrayOf(id.toString()),                                 // значение для ?
        null, null, null
    )

    return cursor.use { c ->
        if (c.moveToFirst()) {
            c.getString(c.getColumnIndexOrThrow(Database.COLUMN_INSTRUCTIONS))
        } else {
            ""
        }
    }
}