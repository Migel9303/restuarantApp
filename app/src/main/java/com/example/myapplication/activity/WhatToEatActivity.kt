package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class WhatToEatActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var email: String = ""
    private var active = false
    private var lastRestaurant: Restaurant? = null
    private var lastDish: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_what_to_eat)
        dbHelper = Database(this)

        val user: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }
        if (user?.active == true && !user.email.isNullOrBlank()) saveSession(this, user.email, true)
        email = sessionEmail(this).ifBlank { user?.email ?: "" }
        active = email.isNotBlank()

        setupScreen()
    }

    private fun setupScreen() {
        val spCuisine = findViewById<Spinner>(R.id.spCuisine)
        val spDiet = findViewById<Spinner>(R.id.spDiet)
        val etCookTime = findViewById<EditText>(R.id.etCookTime)
        val btnGetRecipe = findViewById<Button>(R.id.btnGetRecipe)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val cardRecipe = findViewById<CardView>(R.id.cardRecipe)
        val cardRest = findViewById<CardView>(R.id.cardRestaurant)
        val btnToRest = findViewById<Button>(R.id.btnToRest)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        val userId = getUserIdByEmail(dbHelper, email)
        val prefs = if (userId > 0) getPreferencesByUserId(dbHelper, userId) else UserPreferences()

        val cuisines = getDictionaryValues(dbHelper, "cuisine").ifEmpty { listOf("Итальянская", "Японская", "Русская", "Грузинская") }
        spCuisine.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cuisines)
        cuisines.indexOfFirst { prefs.favoriteCuisines.contains(it, true) }.takeIf { it >= 0 }?.let { spCuisine.setSelection(it) }

        val diets = getDictionaryValues(dbHelper, "diet").ifEmpty { listOf("Без ограничений", "Вегетарианство", "Веганство", "Кето", "Без глютена") }
        spDiet.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, diets)
        diets.indexOfFirst { it.equals(prefs.diet, true) }.takeIf { it >= 0 }?.let { spDiet.setSelection(it) }
        etCookTime.setText(prefs.cookingTime.toString())
        btnGetRecipe.text = "Получить рекомендации"

        btnGetRecipe.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            cardRecipe.visibility = View.GONE
            cardRest.visibility = View.GONE

            val requestPrefs = prefs.copy(
                favoriteCuisines = spCuisine.selectedItem.toString(),
                diet = spDiet.selectedItem.toString(),
                cookingTime = etCookTime.text.toString().toIntOrNull() ?: prefs.cookingTime
            )
            showRecommendations(requestPrefs)
            progressBar.visibility = View.GONE
            cardRecipe.visibility = View.VISIBLE
            cardRest.visibility = View.VISIBLE
        }

        btnToRest.setOnClickListener {
            val uid = getUserIdByEmail(dbHelper, email)
            val rest = lastRestaurant
            val dish = lastDish
            if (uid <= 0 || rest == null) {
                Toast.makeText(this, "Войдите в аккаунт, чтобы сохранять историю", Toast.LENGTH_SHORT).show()
            } else {
                addHistory(dbHelper, uid, "restaurant", rest.name, "Выбран ресторан. Блюдо: ${dish?.dishName ?: "не выбрано"}", restaurantId = rest.id)
                addToFavoriteRestaurants(dbHelper, uid, rest.id)
                Toast.makeText(this, "Ресторан сохранён в историю", Toast.LENGTH_SHORT).show()
            }
        }

        if (!active) {
            bottomNav.menu.findItem(R.id.btnHome)?.isVisible = false
            bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        }
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = email, active = active)))
                    true
                }
                R.id.btnProfile -> {
                    if (active) startActivity(Intent(this, ProfileActivity::class.java).putExtra("USER", User(email = email, active = true)))
                    else Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }

    private fun showRecommendations(prefs: UserPreferences) {
        val tvRecipeTitle = findViewById<TextView>(R.id.tvRecipeTitle)
        val tvRecipeIngredients = findViewById<TextView>(R.id.tvRecipeIngredients)
        val tvRecipeInstructions = findViewById<TextView>(R.id.tvRecipeInstructions)
        val tvDishTitle = findViewById<TextView>(R.id.tvDishTitle)
        val tvDishIngredients = findViewById<TextView>(R.id.tvDishIngredients)
        val tvRestInfo = findViewById<TextView>(R.id.tvRestInfo)
        val cardRecipe = findViewById<CardView>(R.id.cardRecipe)

        val userId = getUserIdByEmail(dbHelper, email)
        val restaurants = searchRestaurants(dbHelper, prefs).take(3)
        val restaurant = restaurants.firstOrNull()
        val dish = restaurant?.let { getBestMenuItem(dbHelper, it.id, prefs) }
        lastRestaurant = restaurant
        lastDish = dish

        val ingredients = defaultIngredientsForPrefs(prefs)
        val recipes = getRecommendedRecipesFromDatabase(dbHelper, ingredients, prefs, 3).ifEmpty {
            listOf(Recipe(id = 0, name = "Быстрый домашний ужин", cookTime = prefs.cookingTime, ingredients = ingredients, instructions = "Нарежьте продукты и приготовьте на сковороде до готовности."))
        }
        val primaryRecipe = recipes.first()
        val missingByRecipe = recipes.map { buildShoppingList(it, ingredients).take(3) }

        tvRecipeTitle.text = "Рекомендованные блюда"
        tvRecipeIngredients.text = recipes.mapIndexed { index, item ->
            "${index + 1}. Название: ${item.name ?: "Блюдо"}\n" +
                    "Ингредиенты:\n" +
                    item.ingredients.orEmpty().joinToString("\n") { "- $it" } +
                    "\nАлгоритм приготовления:\n${item.instructions ?: "Приготовьте ингредиенты до готовности и подавайте."}"
        }.joinToString("\n\n")
        tvRecipeInstructions.text = "Нажмите на карточку, чтобы сохранить все 3 блюда в историю."

        cardRecipe.setOnClickListener {
            val uid = getUserIdByEmail(dbHelper, email)
            if (uid <= 0) {
                Toast.makeText(this, "Войдите, чтобы сохранить рецепты", Toast.LENGTH_SHORT).show()
            } else {
                recipes.forEachIndexed { index, item ->
                    val missing = missingByRecipe.getOrElse(index) { emptyList() }
                    val calories = estimateCalories(item.ingredients.orEmpty())
                    val recipeId = saveRecipeReturningId(this, dbHelper, item.name ?: "Рецепт", item.cookTime ?: prefs.cookingTime, calories, item.ingredients.orEmpty(), missing, item.instructions ?: "", uid, "what_to_eat")
                    addHistory(dbHelper, uid, "recipe", item.name ?: "Рецепт", "Рекомендация 'Что поесть сегодня?'", recipeId = recipeId)
                    addToFavoriteRecipes(dbHelper, uid, recipeId)
                }
                Toast.makeText(this, "3 рецепта сохранены в историю", Toast.LENGTH_SHORT).show()
            }
        }

        if (restaurant != null && dish != null) {
            tvDishTitle.text = "${dish.dishName} — ${dish.price.toInt()} ₽"
            tvDishIngredients.text = "Ингредиенты: ${dish.ingredients}\nКалорийность: ${dish.calories} ккал"
            tvRestInfo.text = "${restaurant.name}\n${restaurant.cuisine}, ${restaurant.rating}★, ${restaurant.distanceMin} мин от вас\nСредний чек: ${restaurant.avgCheck} ₽\nДоставка: ${if (restaurant.hasDelivery) "есть" else "нет"}\nПочему: блюдо подходит по диете, бюджету и ограничениям.\n\nСравнение:\nДом: ~${estimateHomeCost(primaryRecipe.ingredients.orEmpty())} ₽, ${primaryRecipe.cookTime ?: prefs.cookingTime} мин.\nРесторан: ~${dish.price.toInt()} ₽, ${restaurant.distanceMin + 20} мин."
        } else {
            tvDishTitle.text = "Ресторан не найден"
            tvDishIngredients.text = "Нет подходящих блюд по профилю."
            tvRestInfo.text = "Попробуйте увеличить бюджет или изменить ограничения."
        }

        if (userId > 0) {
            val summary = "Рецепты: ${recipes.joinToString(", ") { it.name ?: "Блюдо" }}; ресторан: ${restaurant?.name ?: "нет"}"
            saveQuery(dbHelper, userId, "Что поесть сегодня?", summary)
            recipes.forEachIndexed { index, item ->
                val missing = missingByRecipe.getOrElse(index) { emptyList() }
                val calories = estimateCalories(item.ingredients.orEmpty())
                val autoRecipeId = saveRecipeReturningId(this, dbHelper, item.name ?: "Рецепт", item.cookTime ?: prefs.cookingTime, calories, item.ingredients.orEmpty(), missing, item.instructions ?: "", userId, "what_to_eat_auto")
                addHistory(dbHelper, userId, "recipe", item.name ?: "Рецепт", "Автоматически сохранено из рекомендации. $summary", recipeId = autoRecipeId)
            }
        }
    }

    private fun defaultIngredientsForPrefs(prefs: UserPreferences): List<String> = when {
        prefs.diet.contains("Веган", true) -> listOf("Рис", "Морковь", "Лук", "Помидор", "Огурец")
        prefs.diet.contains("Вегетари", true) -> listOf("Рис", "Морковь", "Сыр", "Яйца", "Помидор")
        prefs.favoriteCuisines.contains("Япон", true) -> listOf("Рис", "Курица", "Огурец", "Морковь")
        prefs.favoriteCuisines.contains("Итальян", true) -> listOf("Паста", "Помидор", "Сыр", "Лук")
        else -> listOf("Картофель", "Курица", "Морковь", "Лук")
    }

    private fun estimateHomeCost(ingredients: List<String>): Int = (ingredients.size * 55).coerceAtLeast(120)
    private fun estimateCalories(ingredients: List<String>): Int = (ingredients.size * 110).coerceAtLeast(300)
}
