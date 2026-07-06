package com.example.myapplication.db

import android.content.Context
import android.database.Cursor
import androidx.core.content.contentValuesOf
import java.util.Locale

data class UserPreferences(
    val favoriteCuisines: String = "Итальянская",
    val diet: String = "Без ограничений",
    val allergies: String = "нет аллергенов",
    val budget: Int = 1000,
    val cookingTime: Int = 30,
    val skillLevel: Int = 1
)

data class Restaurant(
    val id: Int,
    val name: String,
    val location: String,
    val cuisine: String,
    val rating: Double,
    val priceRange: String,
    val avgCheck: Int,
    val distanceMin: Int,
    val description: String,
    val contact: String,
    val openingHours: String,
    val hasDelivery: Boolean,
    val hasBooking: Boolean
)

data class MenuItem(
    val id: Int,
    val restaurantId: Int,
    val dishName: String,
    val description: String,
    val price: Double,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val calories: Int,
    val ingredients: String,
    val allergens: String
)

data class RestaurantReview(
    val id: Int,
    val restaurantId: Int,
    val text: String,
    val rating: Double,
    val createdAt: String
)

data class HistoryItem(
    val id: Int,
    val type: String,
    val title: String,
    val details: String,
    val createdAt: String
)

data class ReportStats(
    val users: Int,
    val restaurants: Int,
    val recipes: Int,
    val queries: Int,
    val history: Int,
    val popularQueries: List<String>,
    val popularRestaurants: List<String>,
    val popularRecipes: List<String>
)

fun currentUserId(context: Context, dbHelper: Database): Int {
    val email = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("email", "") ?: ""
    return getUserIdByEmail(dbHelper, email)
}

fun saveSession(context: Context, email: String, active: Boolean = true) {
    context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit()
        .putString("email", email)
        .putBoolean("active", active)
        .apply()
}

fun clearSession(context: Context) {
    context.getSharedPreferences("auth", Context.MODE_PRIVATE).edit().clear().apply()
}

fun sessionEmail(context: Context): String {
    return context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("email", "") ?: ""
}

fun isLoggedIn(context: Context): Boolean = sessionEmail(context).isNotBlank()

fun isUserBlocked(dbHelper: Database, email: String): Boolean {
    val db = dbHelper.readableDatabase
    val cursor = db.query(Database.TABLE_USERS, arrayOf(Database.COLUMN_IS_BLOCKED), "${Database.COLUMN_EMAIL}=?", arrayOf(email), null, null, null)
    return cursor.use { it.moveToFirst() && it.getInt(0) == 1 }
}

fun upsertPreferences(dbHelper: Database, userId: Int, prefs: UserPreferences) {
    if (userId <= 0) return
    val db = dbHelper.writableDatabase
    val values = contentValuesOf(
        Database.COLUMN_USER_ID to userId,
        Database.COLUMN_FAVORITE_CUISINES to prefs.favoriteCuisines,
        Database.COLUMN_DIETARY_RESTRICTIONS to prefs.diet,
        Database.COLUMN_ALLERGIES to prefs.allergies,
        Database.COLUMN_BUDGET to prefs.budget,
        Database.COLUMN_COOKING_TIME to prefs.cookingTime,
        Database.COLUMN_COOKING_SKILL_LEVEL to prefs.skillLevel
    )
    val rows = db.update(Database.TABLE_USER_PREFERENCES, values, "${Database.COLUMN_USER_ID}=?", arrayOf(userId.toString()))
    if (rows == 0) db.insert(Database.TABLE_USER_PREFERENCES, null, values)
}

fun resetPreferences(dbHelper: Database, userId: Int) {
    upsertPreferences(dbHelper, userId, UserPreferences())
}

fun getPreferencesByUserId(dbHelper: Database, userId: Int): UserPreferences {
    if (userId <= 0) return UserPreferences()
    val db = dbHelper.readableDatabase
    val cursor = db.query(
        Database.TABLE_USER_PREFERENCES,
        arrayOf(
            Database.COLUMN_FAVORITE_CUISINES,
            Database.COLUMN_DIETARY_RESTRICTIONS,
            Database.COLUMN_ALLERGIES,
            Database.COLUMN_BUDGET,
            Database.COLUMN_COOKING_TIME,
            Database.COLUMN_COOKING_SKILL_LEVEL
        ),
        "${Database.COLUMN_USER_ID}=?",
        arrayOf(userId.toString()),
        null, null, null
    )
    return cursor.use { c ->
        if (c.moveToFirst()) {
            UserPreferences(
                favoriteCuisines = c.getStringOrDefault(Database.COLUMN_FAVORITE_CUISINES, "Итальянская"),
                diet = c.getStringOrDefault(Database.COLUMN_DIETARY_RESTRICTIONS, "Без ограничений"),
                allergies = c.getStringOrDefault(Database.COLUMN_ALLERGIES, "нет аллергенов"),
                budget = c.getIntOrDefault(Database.COLUMN_BUDGET, 1000),
                cookingTime = c.getIntOrDefault(Database.COLUMN_COOKING_TIME, 30),
                skillLevel = c.getIntOrDefault(Database.COLUMN_COOKING_SKILL_LEVEL, 1)
            )
        } else UserPreferences()
    }
}

fun getPreferencesByEmail(dbHelper: Database, email: String?): UserPreferences {
    return getPreferencesByUserId(dbHelper, getUserIdByEmail(dbHelper, email))
}

fun updateUser(dbHelper: Database, userId: Int, name: String, email: String, password: String?) {
    val db = dbHelper.writableDatabase
    val values = if (password.isNullOrBlank()) {
        contentValuesOf(Database.COLUMN_USERNAME to name, Database.COLUMN_EMAIL to email)
    } else {
        contentValuesOf(Database.COLUMN_USERNAME to name, Database.COLUMN_EMAIL to email, Database.COLUMN_PASSWORD to password)
    }
    db.update(Database.TABLE_USERS, values, "${Database.COLUMN_USER_ID}=?", arrayOf(userId.toString()))
}

fun setUserBlocked(dbHelper: Database, userId: Int, blocked: Boolean) {
    dbHelper.writableDatabase.update(
        Database.TABLE_USERS,
        contentValuesOf(Database.COLUMN_IS_BLOCKED to if (blocked) 1 else 0),
        "${Database.COLUMN_USER_ID}=?",
        arrayOf(userId.toString())
    )
}

fun deleteUser(dbHelper: Database, userId: Int) {
    dbHelper.writableDatabase.delete(Database.TABLE_USERS, "${Database.COLUMN_USER_ID}=?", arrayOf(userId.toString()))
}

fun searchUsers(dbHelper: Database, query: String): List<User> {
    val db = dbHelper.readableDatabase
    val selection = if (query.isBlank()) null else "${Database.COLUMN_USERNAME} LIKE ? OR ${Database.COLUMN_EMAIL} LIKE ?"
    val args = if (query.isBlank()) null else arrayOf("%$query%", "%$query%")
    val cursor = db.query(Database.TABLE_USERS, arrayOf(Database.COLUMN_USER_ID, Database.COLUMN_USERNAME, Database.COLUMN_EMAIL, Database.COLUMN_IS_BLOCKED), selection, args, null, null, Database.COLUMN_USERNAME)
    return cursor.use { c ->
        val result = mutableListOf<User>()
        while (c.moveToNext()) {
            val blocked = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_IS_BLOCKED)) == 1
            result.add(User(id = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_USER_ID)), name = c.getStringOrDefault(Database.COLUMN_USERNAME, ""), email = c.getStringOrDefault(Database.COLUMN_EMAIL, ""), active = !blocked))
        }
        result
    }
}

fun getAllRestaurants(dbHelper: Database): List<Restaurant> {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_RESTAURANTS, null, null, null, null, null, "${Database.COLUMN_RATING} DESC")
    return cursor.use { c ->
        val result = mutableListOf<Restaurant>()
        while (c.moveToNext()) result.add(c.toRestaurant())
        result
    }
}

fun searchRestaurants(dbHelper: Database, prefs: UserPreferences, query: String = ""): List<Restaurant> {
    val all = getAllRestaurants(dbHelper)
    val cuisines = prefs.favoriteCuisines.split(',', '[', ']').map { it.trim() }.filter { it.isNotBlank() }
    val allergies = normalizeAllergies(prefs.allergies)
    return all
        .filter { restaurant ->
            (query.isBlank() || restaurant.name.contains(query, true) || restaurant.cuisine.contains(query, true) || restaurant.location.contains(query, true)) &&
            (cuisines.isEmpty() || cuisines.any { restaurant.cuisine.contains(it, true) } || query.isNotBlank()) &&
            restaurant.avgCheck <= prefs.budget + 500
        }
        .filter { restaurant ->
            val suitableMenu = getMenuForRestaurant(dbHelper, restaurant.id).any { item -> matchesDiet(item, prefs.diet) && !containsAllergen(item, allergies) }
            suitableMenu
        }
        .sortedWith(compareByDescending<Restaurant> { it.rating }.thenBy { it.distanceMin }.thenBy { it.avgCheck })
}

fun getRestaurantById(dbHelper: Database, id: Int): Restaurant? {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_RESTAURANTS, null, "${Database.COLUMN_RESTAURANT_ID}=?", arrayOf(id.toString()), null, null, null)
    return cursor.use { if (it.moveToFirst()) it.toRestaurant() else null }
}

fun getMenuForRestaurant(dbHelper: Database, restaurantId: Int): List<MenuItem> {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_RESTAURANT_MENU, null, "${Database.COLUMN_RESTAURANT_ID}=?", arrayOf(restaurantId.toString()), null, null, "${Database.COLUMN_PRICE} ASC")
    return cursor.use { c ->
        val result = mutableListOf<MenuItem>()
        while (c.moveToNext()) result.add(c.toMenuItem())
        result
    }
}

fun getBestMenuItem(dbHelper: Database, restaurantId: Int, prefs: UserPreferences): MenuItem? {
    val allergies = normalizeAllergies(prefs.allergies)
    return getMenuForRestaurant(dbHelper, restaurantId)
        .filter { item ->
            matchesDiet(item, prefs.diet) &&
                    !containsAllergen(item, allergies) &&
                    (prefs.budget <= 0 || item.price <= prefs.budget + 300)
        }
        .minByOrNull { it.price }
}

fun getReviews(dbHelper: Database, restaurantId: Int): List<RestaurantReview> {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_RESTAURANT_REVIEWS, null, "${Database.COLUMN_RESTAURANT_ID}=?", arrayOf(restaurantId.toString()), null, null, "${Database.COLUMN_CREATED_AT} DESC")
    return cursor.use { c ->
        val result = mutableListOf<RestaurantReview>()
        while (c.moveToNext()) result.add(RestaurantReview(
            id = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_REVIEW_ID)),
            restaurantId = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_RESTAURANT_ID)),
            text = c.getStringOrDefault(Database.COLUMN_REVIEW_TEXT, ""),
            rating = c.getDouble(c.getColumnIndexOrThrow(Database.COLUMN_RATING)),
            createdAt = c.getStringOrDefault(Database.COLUMN_CREATED_AT, "")
        ))
        result
    }
}

fun addOrUpdateRestaurant(dbHelper: Database, restaurant: Restaurant) {
    val values = contentValuesOf(
        Database.COLUMN_REST_NAME to restaurant.name,
        Database.COLUMN_LOCATION to restaurant.location,
        Database.COLUMN_CUISINE to restaurant.cuisine,
        Database.COLUMN_RATING to restaurant.rating,
        Database.COLUMN_PRICE_RANGE to restaurant.priceRange,
        Database.COLUMN_AVG_CHECK to restaurant.avgCheck,
        Database.COLUMN_DISTANCE_MIN to restaurant.distanceMin,
        Database.COLUMN_DESCRIPTION to restaurant.description,
        Database.COLUMN_CONTACT_INFO to restaurant.contact,
        Database.COLUMN_OPENING_HOURS to restaurant.openingHours,
        Database.COLUMN_HAS_DELIVERY to if (restaurant.hasDelivery) 1 else 0,
        Database.COLUMN_HAS_BOOKING to if (restaurant.hasBooking) 1 else 0
    )
    val db = dbHelper.writableDatabase
    if (restaurant.id > 0) db.update(Database.TABLE_RESTAURANTS, values, "${Database.COLUMN_RESTAURANT_ID}=?", arrayOf(restaurant.id.toString()))
    else db.insert(Database.TABLE_RESTAURANTS, null, values)
}

fun deleteRestaurant(dbHelper: Database, id: Int) {
    dbHelper.writableDatabase.delete(Database.TABLE_RESTAURANTS, "${Database.COLUMN_RESTAURANT_ID}=?", arrayOf(id.toString()))
}

fun addToFavoriteRestaurants(dbHelper: Database, userId: Int, restaurantId: Int) {
    if (userId <= 0) return
    dbHelper.writableDatabase.insertWithOnConflict(
        Database.TABLE_FAVORITE_RESTAURANTS,
        null,
        contentValuesOf(Database.COLUMN_USER_ID to userId, Database.COLUMN_RESTAURANT_ID to restaurantId),
        android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
    )
}

fun addToFavoriteRecipes(dbHelper: Database, userId: Int, recipeId: Int) {
    if (userId <= 0 || recipeId <= 0) return
    dbHelper.writableDatabase.insertWithOnConflict(
        Database.TABLE_FAVORITE_RECIPES,
        null,
        contentValuesOf(Database.COLUMN_USER_ID to userId, Database.COLUMN_RECIPE_ID to recipeId),
        android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
    )
}

fun saveQuery(dbHelper: Database, userId: Int, query: String, result: String) {
    if (userId <= 0) return
    dbHelper.writableDatabase.insert(Database.TABLE_USER_QUERIES, null, contentValuesOf(Database.COLUMN_USER_ID to userId, Database.COLUMN_QUERY_TEXT to query, Database.COLUMN_RESULT to result))
}

fun addHistory(dbHelper: Database, userId: Int, type: String, title: String, details: String, restaurantId: Int? = null, recipeId: Int? = null) {
    if (userId <= 0 || title.isBlank()) return
    val values = contentValuesOf(
        Database.COLUMN_USER_ID to userId,
        Database.COLUMN_TYPE to type,
        Database.COLUMN_TITLE to title,
        Database.COLUMN_DETAILS to details
    )
    restaurantId?.takeIf { it > 0 }?.let { values.put(Database.COLUMN_RESTAURANT_ID, it) }
    recipeId?.takeIf { it > 0 }?.let { values.put(Database.COLUMN_RECIPE_ID, it) }
    dbHelper.writableDatabase.insert(Database.TABLE_USER_HISTORY, null, values)
}

fun getHistory(dbHelper: Database, userId: Int): List<HistoryItem> {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_USER_HISTORY, null, "${Database.COLUMN_USER_ID}=?", arrayOf(userId.toString()), null, null, "${Database.COLUMN_CREATED_AT} DESC")
    return cursor.use { c ->
        val result = mutableListOf<HistoryItem>()
        while (c.moveToNext()) result.add(HistoryItem(
            id = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_HISTORY_ID)),
            type = c.getStringOrDefault(Database.COLUMN_TYPE, ""),
            title = c.getStringOrDefault(Database.COLUMN_TITLE, ""),
            details = c.getStringOrDefault(Database.COLUMN_DETAILS, ""),
            createdAt = c.getStringOrDefault(Database.COLUMN_CREATED_AT, "")
        ))
        result
    }
}

fun clearHistory(dbHelper: Database, userId: Int) {
    dbHelper.writableDatabase.delete(Database.TABLE_USER_HISTORY, "${Database.COLUMN_USER_ID}=?", arrayOf(userId.toString()))
}

fun saveRecipeReturningId(context: Context, dbHelper: Database, name: String, cookTime: Int, calories: Int, ingredients: List<String>, missing: List<String>, instructions: String, userId: Int, source: String = "local"): Int {
    val values = contentValuesOf(
        Database.COLUMN_RECIPE_NAME to name,
        Database.COLUMN_COOK_TIME to cookTime,
        Database.COLUMN_CALORIES to calories,
        Database.COLUMN_INGREDIENTS to ingredients.joinToString(", "),
        Database.COLUMN_MISSING_INGREDIENTS to missing.joinToString(", "),
        Database.COLUMN_SHOPPING_LIST to missing.joinToString(", "),
        Database.COLUMN_INSTRUCTIONS to instructions,
        Database.COLUMN_SOURCE to source,
        Database.COLUMN_USER_ID to userId
    )
    return dbHelper.writableDatabase.insert(Database.TABLE_RECIPES, null, values).toInt()
}

fun getRecommendedRecipesFromDatabase(dbHelper: Database, selected: List<String>, prefs: UserPreferences, limit: Int = 3): List<Recipe> {
    val selectedLower = selected
        .filter { it.isNotBlank() }
        .map { it.lowercase(Locale.getDefault()) }
        .distinct()
    val allergyTerms = allergyTermsForProfile(prefs.allergies)
    val maxCookTime = maxCookTimeForProfile(prefs)

    val selectionParts = mutableListOf("${Database.COLUMN_SOURCE}=?")
    val args = mutableListOf("catalog")
    if (maxCookTime > 0) {
        selectionParts.add("${Database.COLUMN_COOK_TIME}<=?")
        args.add(maxCookTime.toString())
    }
    when {
        prefs.diet.contains("Веган", true) -> selectionParts.add("${Database.COLUMN_IS_VEGAN}=1")
        prefs.diet.contains("Вегетари", true) -> selectionParts.add("${Database.COLUMN_IS_VEGETARIAN}=1")
        prefs.diet.contains("Без глютена", true) -> selectionParts.add("${Database.COLUMN_IS_GLUTEN_FREE}=1")
    }

    val cursor = dbHelper.readableDatabase.query(
        Database.TABLE_RECIPES,
        arrayOf(
            Database.COLUMN_RECIPE_ID,
            Database.COLUMN_RECIPE_NAME,
            Database.COLUMN_DESCRIPTION,
            Database.COLUMN_COOK_TIME,
            Database.COLUMN_CALORIES,
            Database.COLUMN_IS_VEGETARIAN,
            Database.COLUMN_IS_VEGAN,
            Database.COLUMN_IS_GLUTEN_FREE,
            Database.COLUMN_INGREDIENTS,
            Database.COLUMN_INSTRUCTIONS
        ),
        selectionParts.joinToString(" AND "),
        args.toTypedArray(),
        null,
        null,
        "RANDOM()",
        "180"
    )

    val recipes = cursor.use { c ->
        val result = mutableListOf<Pair<Int, Recipe>>()
        while (c.moveToNext()) {
            val ingredientsText = c.getStringOrDefault(Database.COLUMN_INGREDIENTS, "")
            val ingredients = ingredientsText.split(',', ';').map { it.trim() }.filter { it.isNotBlank() }
            val ingredientsLower = ingredientsText.lowercase(Locale.getDefault())
            val cookTime = c.getIntOrDefault(Database.COLUMN_COOK_TIME, prefs.cookingTime)
            val calories = c.getIntOrDefault(Database.COLUMN_CALORIES, 0)
            val isVegetarian = c.getIntOrDefault(Database.COLUMN_IS_VEGETARIAN, 0) == 1
            val isVegan = c.getIntOrDefault(Database.COLUMN_IS_VEGAN, 0) == 1
            val isGlutenFree = c.getIntOrDefault(Database.COLUMN_IS_GLUTEN_FREE, 0) == 1
            val name = c.getStringOrDefault(Database.COLUMN_RECIPE_NAME, "Блюдо")
            val description = c.getStringOrDefault(Database.COLUMN_DESCRIPTION, "")
            val haystack = listOf(name, description, ingredientsText).joinToString(" ").lowercase(Locale.getDefault())

            val profileAllowed = !containsAllergenTerms(ingredientsLower, allergyTerms) &&
                    recipeMatchesDiet(ingredientsLower, calories, isVegetarian, isVegan, isGlutenFree, prefs.diet) &&
                    recipeMatchesBudget(ingredients.size, prefs.budget) &&
                    recipeMatchesSkill(cookTime, prefs.skillLevel)

            if (profileAllowed) {
                val overlap = if (selectedLower.isEmpty()) 0 else selectedLower.count { selectedItem ->
                    ingredients.any { ingredient ->
                        val ingredientLower = ingredient.lowercase(Locale.getDefault())
                        ingredientLower.contains(selectedItem) || selectedItem.contains(ingredientLower)
                    }
                }
                val score = overlap * 20 + cuisineScore(haystack, prefs.favoriteCuisines) + skillScore(cookTime, prefs.skillLevel) + budgetScore(ingredients.size, prefs.budget)
                val recipe = Recipe(
                    id = c.getInt(c.getColumnIndexOrThrow(Database.COLUMN_RECIPE_ID)),
                    name = name,
                    cookTime = cookTime,
                    ingredients = ingredients,
                    instructions = c.getStringOrDefault(Database.COLUMN_INSTRUCTIONS, "Приготовьте ингредиенты до готовности и подавайте горячим.")
                )
                result.add(score to recipe)
            }
        }
        result.sortedWith(compareByDescending<Pair<Int, Recipe>> { it.first }.thenBy { it.second.cookTime ?: 999 })
            .map { it.second }
    }

    return recipes.take(limit).ifEmpty {
        generateLocalRecipesFromIngredients(selected, prefs).take(limit)
    }
}

fun generateLocalRecipesFromIngredients(selected: List<String>, prefs: UserPreferences): List<Recipe> {
    val clean = selected.filter { it.isNotBlank() }.distinct()
    val base = if (clean.isEmpty()) listOf("Рис", "Морковь", "Лук") else clean
    val templates = listOf(
        Triple("Быстрый боул", listOf("Рис", "Морковь", "Огурец", "Курица"), "Отварите основу, нарежьте овощи, соедините всё в миске и заправьте специями."),
        Triple("Домашняя сковорода", listOf("Картофель", "Лук", "Курица", "Морковь"), "Нарежьте ингредиенты, обжарьте сначала основу, затем добавьте овощи и доведите до готовности."),
        Triple("Тёплый салат", listOf("Помидор", "Огурец", "Яйца", "Сыр"), "Нарежьте овощи, добавьте белковый ингредиент, перемешайте и подавайте тёплым."),
        Triple("Каша с добавками", listOf("Гречка", "Морковь", "Лук", "Сыр"), "Сварите крупу, отдельно припустите овощи и соедините с готовой основой."),
        Triple("Лёгкий суп", listOf("Картофель", "Морковь", "Лук", "Курица"), "Сварите бульон или воду, добавьте нарезанные ингредиенты и готовьте до мягкости."),
        Triple("Паста из холодильника", listOf("Паста", "Помидор", "Сыр", "Лук"), "Отварите пасту, приготовьте быстрый соус на сковороде и смешайте."),
        Triple("Овощное карри", listOf("Рис", "Морковь", "Лук", "Специи"), "Потушите овощи со специями и подайте с рисом.")
    )
    val generated = templates.mapIndexedNotNull { idx, tpl ->
        val needed = tpl.second
        val missing = needed.filterNot { need -> base.any { it.equals(need, true) } }.take(3)
        val available = needed.filter { need -> base.any { it.equals(need, true) } }
        if (available.isEmpty() && clean.isNotEmpty()) null else Recipe(
            id = 0,
            name = "${tpl.first}: ${available.take(2).joinToString(" + ").ifBlank { base.take(2).joinToString(" + ") }}",
            cookTime = (15 + idx * 5).coerceAtMost(maxCookTimeForProfile(prefs).takeIf { it > 0 } ?: (prefs.cookingTime + 15)),
            ingredients = (available + missing).distinct(),
            instructions = tpl.third + if (missing.isNotEmpty()) "\n\nМинимальная докупка: ${missing.joinToString(", ")}." else "\n\nРецепт можно приготовить из выбранных продуктов."
        )
    }
        .filter { generatedRecipeMatchesProfile(it, prefs) }
        .take(7)

    return generated.ifEmpty {
        val safeBase = safeFallbackIngredients(prefs, base)
        listOf(Recipe(id = 0, name = "Простой ужин из выбранных продуктов", cookTime = maxCookTimeForProfile(prefs).takeIf { it > 0 } ?: prefs.cookingTime, ingredients = safeBase, instructions = "Нарежьте продукты, приготовьте на сковороде или отварите основу, приправьте по вкусу."))
    }
}

fun buildShoppingList(recipe: Recipe, selected: List<String>): List<String> {
    val available = selected.map { it.lowercase(Locale.getDefault()) }
    return recipe.ingredients.orEmpty().filterNot { available.contains(it.lowercase(Locale.getDefault())) }.take(3)
}

fun getDictionaryValues(dbHelper: Database, type: String): List<String> {
    val cursor = dbHelper.readableDatabase.query(Database.TABLE_DICTIONARIES, arrayOf(Database.COLUMN_DICT_VALUE), "${Database.COLUMN_DICT_TYPE}=?", arrayOf(type), null, null, Database.COLUMN_DICT_VALUE)
    return cursor.use { c ->
        val result = mutableListOf<String>()
        while (c.moveToNext()) result.add(c.getString(0))
        result
    }
}

fun addDictionaryValue(dbHelper: Database, type: String, value: String) {
    if (value.isBlank()) return
    dbHelper.writableDatabase.insertWithOnConflict(Database.TABLE_DICTIONARIES, null, contentValuesOf(Database.COLUMN_DICT_TYPE to type, Database.COLUMN_DICT_VALUE to value.trim()), android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE)
}

fun deleteDictionaryValue(dbHelper: Database, type: String, value: String) {
    dbHelper.writableDatabase.delete(Database.TABLE_DICTIONARIES, "${Database.COLUMN_DICT_TYPE}=? AND ${Database.COLUMN_DICT_VALUE}=?", arrayOf(type, value))
}

fun getReportStats(dbHelper: Database): ReportStats {
    return ReportStats(
        users = countRows(dbHelper, Database.TABLE_USERS),
        restaurants = countRows(dbHelper, Database.TABLE_RESTAURANTS),
        recipes = countRows(dbHelper, Database.TABLE_RECIPES),
        queries = countRows(dbHelper, Database.TABLE_USER_QUERIES),
        history = countRows(dbHelper, Database.TABLE_USER_HISTORY),
        popularQueries = topValues(dbHelper, Database.TABLE_USER_QUERIES, Database.COLUMN_QUERY_TEXT),
        popularRestaurants = topValues(dbHelper, Database.TABLE_USER_HISTORY, Database.COLUMN_TITLE, "${Database.COLUMN_TYPE}='restaurant'"),
        popularRecipes = topValues(dbHelper, Database.TABLE_USER_HISTORY, Database.COLUMN_TITLE, "${Database.COLUMN_TYPE}='recipe'")
    )
}

private fun countRows(dbHelper: Database, table: String): Int {
    val cursor = dbHelper.readableDatabase.rawQuery("SELECT COUNT(*) FROM $table", null)
    return cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
}

private fun topValues(dbHelper: Database, table: String, column: String, where: String? = null): List<String> {
    val sql = "SELECT $column, COUNT(*) c FROM $table ${if (where != null) "WHERE $where" else ""} GROUP BY $column ORDER BY c DESC LIMIT 5"
    val cursor = dbHelper.readableDatabase.rawQuery(sql, null)
    return cursor.use { c ->
        val result = mutableListOf<String>()
        while (c.moveToNext()) result.add("${c.getString(0)} — ${c.getInt(1)}")
        result
    }
}

private fun Cursor.toRestaurant(): Restaurant = Restaurant(
    id = getInt(getColumnIndexOrThrow(Database.COLUMN_RESTAURANT_ID)),
    name = getStringOrDefault(Database.COLUMN_REST_NAME, ""),
    location = getStringOrDefault(Database.COLUMN_LOCATION, ""),
    cuisine = getStringOrDefault(Database.COLUMN_CUISINE, ""),
    rating = getDouble(getColumnIndexOrThrow(Database.COLUMN_RATING)),
    priceRange = getStringOrDefault(Database.COLUMN_PRICE_RANGE, ""),
    avgCheck = getIntOrDefault(Database.COLUMN_AVG_CHECK, 0),
    distanceMin = getIntOrDefault(Database.COLUMN_DISTANCE_MIN, 0),
    description = getStringOrDefault(Database.COLUMN_DESCRIPTION, ""),
    contact = getStringOrDefault(Database.COLUMN_CONTACT_INFO, ""),
    openingHours = getStringOrDefault(Database.COLUMN_OPENING_HOURS, ""),
    hasDelivery = getIntOrDefault(Database.COLUMN_HAS_DELIVERY, 0) == 1,
    hasBooking = getIntOrDefault(Database.COLUMN_HAS_BOOKING, 0) == 1
)

private fun Cursor.toMenuItem(): MenuItem = MenuItem(
    id = getInt(getColumnIndexOrThrow(Database.COLUMN_MENU_ID)),
    restaurantId = getInt(getColumnIndexOrThrow(Database.COLUMN_RESTAURANT_ID)),
    dishName = getStringOrDefault(Database.COLUMN_DISH_NAME, ""),
    description = getStringOrDefault(Database.COLUMN_DESCRIPTION, ""),
    price = getDouble(getColumnIndexOrThrow(Database.COLUMN_PRICE)),
    isVegetarian = getIntOrDefault(Database.COLUMN_IS_VEGETARIAN, 0) == 1,
    isVegan = getIntOrDefault(Database.COLUMN_IS_VEGAN, 0) == 1,
    isGlutenFree = getIntOrDefault(Database.COLUMN_IS_GLUTEN_FREE, 0) == 1,
    calories = getIntOrDefault(Database.COLUMN_CALORIES, 0),
    ingredients = getStringOrDefault(Database.COLUMN_INGREDIENTS_TEXT, ""),
    allergens = getStringOrDefault(Database.COLUMN_ALLERGENS_TEXT, "")
)

private fun matchesDiet(item: MenuItem, diet: String): Boolean {
    val ingredientsLower = item.ingredients.lowercase(Locale.getDefault())
    return when {
        diet.contains("Веган", true) -> item.isVegan
        diet.contains("Вегетари", true) -> item.isVegetarian
        diet.contains("Без глютена", true) -> item.isGlutenFree
        diet.contains("Без лактозы", true) -> !containsAllergenTerms(ingredientsLower, expandedTermsForAllergen("лактоза"))
        diet.contains("Кето", true) -> !containsAnyTerm(ingredientsLower, listOf("паста", "лапша", "рис", "греч", "булгур", "киноа", "картофель", "хлеб"))
        diet.contains("Низкокалорий", true) -> item.calories <= 520
        diet.contains("Высокобелков", true) -> containsAnyTerm(ingredientsLower, listOf("курица", "индейка", "говядина", "рыба", "яйца", "тофу", "нут", "фасоль", "сыр"))
        else -> true
    }
}

private fun normalizeAllergies(allergies: String): String {
    val normalized = allergies.trim().lowercase(Locale.getDefault())
    return if (normalized.isBlank() || normalized == "нет" || normalized == "нет аллергенов") "" else normalized
}

private fun allergyTermsForProfile(allergies: String): List<String> {
    val normalized = normalizeAllergies(allergies)
    if (normalized.isBlank()) return emptyList()
    return normalized
        .split(',', ';', '[', ']', '\n')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .flatMap { expandedTermsForAllergen(it) }
        .map { it.lowercase(Locale.getDefault()) }
        .distinct()
}

private fun expandedTermsForAllergen(allergen: String): List<String> {
    val a = allergen.lowercase(Locale.getDefault()).trim()
    return when {
        a.contains("молоч") || a.contains("лактоз") -> listOf("молоко", "сыр", "моцарелла", "творог", "сливки", "сметана", "йогурт", "лактоза", "молоч")
        a.contains("яйц") -> listOf("яйцо", "яйца", "желток", "белок")
        a.contains("сое") || a.contains("соя") -> listOf("соя", "соевый", "тофу")
        a.contains("морепр") -> listOf("кревет", "кальмар", "мидии", "краб", "морепр", "рыба", "лосось", "тунец")
        a.contains("рыб") -> listOf("рыба", "лосось", "тунец")
        a.contains("глютен") || a.contains("пшениц") -> listOf("глютен", "пшениц", "паста", "лапша", "булгур", "хлеб", "мука", "соевый соус")
        a.contains("орех") -> listOf("орех", "арахис", "миндаль", "фундук", "кешью", "грец")
        a.contains("кунжут") -> listOf("кунжут", "тахини")
        a.contains("горч") -> listOf("горчица", "горч")
        a.contains("мёд") || a.contains("мед") -> listOf("мёд", "мед")
        else -> listOf(a)
    }
}

private fun containsAllergen(item: MenuItem, allergiesLower: String): Boolean {
    return containsAllergenTerms(
        listOf(item.allergens, item.ingredients).joinToString(" ").lowercase(Locale.getDefault()),
        allergyTermsForProfile(allergiesLower)
    )
}

private fun containsAllergenTerms(textLower: String, terms: List<String>): Boolean {
    if (terms.isEmpty() || textLower.isBlank()) return false
    return terms.any { term -> textLower.contains(term) }
}

private fun containsAnyTerm(textLower: String, terms: List<String>): Boolean {
    return terms.any { textLower.contains(it.lowercase(Locale.getDefault())) }
}

private fun recipeMatchesDiet(
    ingredientsLower: String,
    calories: Int,
    isVegetarian: Boolean,
    isVegan: Boolean,
    isGlutenFree: Boolean,
    diet: String
): Boolean {
    return when {
        diet.contains("Веган", true) -> isVegan
        diet.contains("Вегетари", true) -> isVegetarian
        diet.contains("Без глютена", true) -> isGlutenFree && !containsAnyTerm(ingredientsLower, listOf("паста", "лапша", "булгур", "хлеб", "пшениц", "соевый соус"))
        diet.contains("Без лактозы", true) -> !containsAllergenTerms(ingredientsLower, expandedTermsForAllergen("лактоза"))
        diet.contains("Кето", true) -> !containsAnyTerm(ingredientsLower, listOf("паста", "лапша", "рис", "греч", "булгур", "киноа", "картофель", "хлеб", "нут", "фасоль"))
        diet.contains("Низкокалорий", true) -> calories == 0 || calories <= 520
        diet.contains("Высокобелков", true) -> containsAnyTerm(ingredientsLower, listOf("курица", "индейка", "говядина", "рыба", "яйца", "тофу", "нут", "фасоль", "сыр"))
        else -> true
    }
}

private fun recipeMatchesBudget(ingredientsCount: Int, budget: Int): Boolean {
    if (budget <= 0) return true
    val estimatedCost = ingredientsCount.coerceAtLeast(3) * 55
    return estimatedCost <= budget.coerceAtLeast(250) + 150
}

private fun maxCookTimeForProfile(prefs: UserPreferences): Int {
    val userTime = prefs.cookingTime.takeIf { it > 0 } ?: 30
    val skillCap = when (prefs.skillLevel) {
        1 -> 35
        2 -> 50
        else -> 90
    }
    return minOf(userTime + 10, skillCap)
}

private fun recipeMatchesSkill(cookTime: Int, skillLevel: Int): Boolean {
    return when (skillLevel) {
        1 -> cookTime <= 35
        2 -> cookTime <= 50
        else -> true
    }
}

private fun skillScore(cookTime: Int, skillLevel: Int): Int {
    return when (skillLevel) {
        1 -> (40 - cookTime).coerceAtLeast(0)
        2 -> (55 - cookTime).coerceAtLeast(0) / 2
        else -> cookTime.coerceAtMost(60) / 3
    }
}

private fun budgetScore(ingredientsCount: Int, budget: Int): Int {
    if (budget <= 0) return 0
    val estimatedCost = ingredientsCount.coerceAtLeast(3) * 55
    return ((budget - estimatedCost) / 50).coerceIn(0, 12)
}

private fun cuisineScore(haystack: String, cuisines: String): Int {
    val terms = cuisines
        .split(',', ';', '[', ']')
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .flatMap { termsForCuisine(it) }
        .distinct()
    return terms.count { haystack.contains(it.lowercase(Locale.getDefault())) } * 8
}

private fun termsForCuisine(cuisine: String): List<String> {
    val c = cuisine.lowercase(Locale.getDefault())
    return when {
        c.contains("итальян") -> listOf("паста", "томат", "базилик", "сыр")
        c.contains("япон") -> listOf("рис", "лапша", "соевый соус", "рыба", "ролл")
        c.contains("рус") -> listOf("картофель", "гречка", "сметана", "лук")
        c.contains("груз") -> listOf("фасоль", "сыр", "зелень", "чеснок")
        c.contains("китай") || c.contains("корей") -> listOf("лапша", "рис", "соевый соус", "перец", "чеснок")
        c.contains("индий") -> listOf("карри", "рис", "нут", "фасоль")
        c.contains("мекс") -> listOf("фасоль", "перец", "авокадо", "томат")
        c.contains("веган") || c.contains("здоров") -> listOf("овощи", "киноа", "нут", "зелень", "авокадо")
        c.contains("мор") -> listOf("рыба", "лосось", "тунец", "кревет")
        else -> listOf(c)
    }
}

private fun generatedRecipeMatchesProfile(recipe: Recipe, prefs: UserPreferences): Boolean {
    val ingredients = recipe.ingredients.orEmpty()
    val ingredientsLower = ingredients.joinToString(" ").lowercase(Locale.getDefault())
    val allergyTerms = allergyTermsForProfile(prefs.allergies)
    val cookTime = recipe.cookTime ?: prefs.cookingTime
    return !containsAllergenTerms(ingredientsLower, allergyTerms) &&
            recipeMatchesDiet(
                ingredientsLower,
                0,
                isVegetarian = !containsAnyTerm(ingredientsLower, listOf("курица", "индейка", "говядина", "рыба")),
                isVegan = !containsAnyTerm(ingredientsLower, listOf("курица", "индейка", "говядина", "рыба", "яйца", "сыр", "молоко", "сметана", "йогурт")),
                isGlutenFree = !containsAnyTerm(ingredientsLower, listOf("паста", "лапша", "булгур", "хлеб", "пшениц", "соевый соус")),
                diet = prefs.diet
            ) &&
            recipeMatchesBudget(ingredients.size, prefs.budget) &&
            recipeMatchesSkill(cookTime, prefs.skillLevel)
}

private fun safeFallbackIngredients(prefs: UserPreferences, selected: List<String>): List<String> {
    val allergyTerms = allergyTermsForProfile(prefs.allergies)
    val defaultCandidates = when {
        prefs.diet.contains("Веган", true) -> listOf("Рис", "Нут", "Морковь", "Огурец", "Зелень", "Авокадо", "Тофу")
        prefs.diet.contains("Вегетари", true) -> listOf("Рис", "Гречка", "Морковь", "Огурец", "Яйца", "Сыр", "Зелень")
        prefs.diet.contains("Кето", true) -> listOf("Курица", "Огурец", "Авокадо", "Зелень", "Перец болгарский")
        prefs.diet.contains("Высокобелков", true) -> listOf("Курица", "Индейка", "Рыба", "Яйца", "Тофу", "Нут", "Фасоль")
        prefs.diet.contains("Без глютена", true) -> listOf("Рис", "Курица", "Морковь", "Огурец", "Зелень")
        prefs.diet.contains("Без лактозы", true) -> listOf("Рис", "Курица", "Морковь", "Огурец", "Зелень")
        else -> listOf("Рис", "Морковь", "Лук", "Курица", "Огурец")
    }
    val combined = (selected + defaultCandidates).distinct()
    val filtered = combined.filterNot { containsAllergenTerms(it.lowercase(Locale.getDefault()), allergyTerms) }
    val dietFiltered = filtered.filter { ingredient ->
        val lower = ingredient.lowercase(Locale.getDefault())
        recipeMatchesDiet(
            lower,
            0,
            isVegetarian = !containsAnyTerm(lower, listOf("курица", "индейка", "говядина", "рыба")),
            isVegan = !containsAnyTerm(lower, listOf("курица", "индейка", "говядина", "рыба", "яйца", "сыр", "молоко", "сметана", "йогурт")),
            isGlutenFree = !containsAnyTerm(lower, listOf("паста", "лапша", "булгур", "хлеб", "пшениц", "соевый соус")),
            diet = prefs.diet
        )
    }
    return dietFiltered.take(5).ifEmpty { listOf("Морковь", "Огурец", "Зелень") }
}

private fun Cursor.getStringOrDefault(column: String, default: String): String {
    val idx = getColumnIndex(column)
    if (idx < 0 || isNull(idx)) return default
    return getString(idx) ?: default
}

private fun Cursor.getIntOrDefault(column: String, default: Int): Int {
    val idx = getColumnIndex(column)
    if (idx < 0 || isNull(idx)) return default
    return getInt(idx)
}
