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

class WhatToCookFromFridgeActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var email: String = ""
    private var active = false
    private var lastRecipes: List<Recipe> = emptyList()
    private var lastMissingByRecipe: List<List<String>> = emptyList()
    private val lastSavedRecipeIds: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_what_to_cook_from_fridge)
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
        val spMeat = findViewById<Spinner>(R.id.spMeat)
        val spVegetable = findViewById<Spinner>(R.id.spVegetable)
        val spGrain = findViewById<Spinner>(R.id.spGrain)
        val etCookTime = findViewById<EditText>(R.id.etCookTime)
        val btnShowRecipe = findViewById<Button>(R.id.btnShowRecipe)
        val progBar = findViewById<ProgressBar>(R.id.progBar)
        val cardRecipe = findViewById<CardView>(R.id.cardRecipe)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        setupSpinner(spMeat, listOf("Курица", "Говядина", "Рыба", "Яйца", "Без мяса"))
        setupSpinner(spVegetable, listOf("Помидор", "Огурец", "Морковь", "Картофель", "Лук"))
        setupSpinner(spGrain, listOf("Рис", "Гречка", "Паста", "Булгур", "Без крупы"))

        val uid = getUserIdByEmail(dbHelper, email)
        val prefs = if (uid > 0) getPreferencesByUserId(dbHelper, uid) else UserPreferences()
        etCookTime.setText(prefs.cookingTime.toString())
        btnShowRecipe.text = "Показать 3 блюда"

        btnShowRecipe.setOnClickListener {
            progBar.visibility = View.VISIBLE
            cardRecipe.visibility = View.GONE
            val selected = listOf(spMeat.selectedItem.toString(), spVegetable.selectedItem.toString(), spGrain.selectedItem.toString())
                .filterNot { it.startsWith("Без ") }
            val requestPrefs = prefs.copy(cookingTime = etCookTime.text.toString().toIntOrNull() ?: prefs.cookingTime)
            val recipes = getRecommendedRecipesFromDatabase(dbHelper, selected, requestPrefs, 3).ifEmpty {
                listOf(Recipe(id = 0, name = "Блюдо из холодильника", cookTime = requestPrefs.cookingTime, ingredients = selected, instructions = "Нарежьте продукты и приготовьте до готовности."))
            }
            lastRecipes = recipes
            lastMissingByRecipe = recipes.map { buildShoppingList(it, selected).take(3) }
            lastSavedRecipeIds.clear()
            displayRecipes(recipes, lastMissingByRecipe, selected, requestPrefs)
            autoSaveCurrentRecipes()
            progBar.visibility = View.GONE
            cardRecipe.visibility = View.VISIBLE
        }

        cardRecipe.setOnClickListener {
            saveCurrentRecipes()
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

    private fun displayRecipes(recipes: List<Recipe>, missingByRecipe: List<List<String>>, selected: List<String>, prefs: UserPreferences) {
        val tvRecipeTitle = findViewById<TextView>(R.id.tvRecipeTitle)
        val tvRecipeIngredients = findViewById<TextView>(R.id.tvRecipeIngredients)
        val tvRecipeInstructions = findViewById<TextView>(R.id.tvRecipeInstructions)
        tvRecipeTitle.text = "Рекомендованные блюда"
        tvRecipeIngredients.text = recipes.mapIndexed { index, recipe ->
            "${index + 1}. Название: ${recipe.name ?: "Рецепт"}\n" +
                    "Ингредиенты:\n" +
                    recipe.ingredients.orEmpty().joinToString("\n") { "- $it" } +
                    "\nАлгоритм приготовления:\n${recipe.instructions ?: "Приготовьте ингредиенты до готовности и подавайте."}"
        }.joinToString("\n\n")
        tvRecipeInstructions.text = "Нажмите на карточку, чтобы сохранить все 3 блюда в историю."
    }

    private fun saveCurrentRecipes() {
        val uid = getUserIdByEmail(dbHelper, email)
        if (uid <= 0 || lastRecipes.isEmpty()) {
            Toast.makeText(this, "Войдите, чтобы сохранить рецепты", Toast.LENGTH_SHORT).show()
            return
        }
        if (lastSavedRecipeIds.isNotEmpty()) {
            Toast.makeText(this, "Рецепты уже сохранены в историю", Toast.LENGTH_SHORT).show()
            return
        }
        lastRecipes.forEachIndexed { index, recipe ->
            val savedId = saveRecipeAndHistory(uid, recipe, lastMissingByRecipe.getOrElse(index) { emptyList() })
            if (savedId > 0) lastSavedRecipeIds.add(savedId)
        }
        Toast.makeText(this, "3 рецепта сохранены в историю", Toast.LENGTH_SHORT).show()
    }

    private fun autoSaveCurrentRecipes() {
        val uid = getUserIdByEmail(dbHelper, email)
        if (uid > 0 && lastSavedRecipeIds.isEmpty()) {
            lastRecipes.forEachIndexed { index, recipe ->
                val savedId = saveRecipeAndHistory(uid, recipe, lastMissingByRecipe.getOrElse(index) { emptyList() })
                if (savedId > 0) lastSavedRecipeIds.add(savedId)
            }
        }
    }

    private fun saveRecipeAndHistory(uid: Int, recipe: Recipe, missing: List<String>): Int {
        val recipeId = saveRecipeReturningId(
            this,
            dbHelper,
            recipe.name ?: "Рецепт из холодильника",
            recipe.cookTime ?: 30,
            recipe.ingredients.orEmpty().size * 110,
            recipe.ingredients.orEmpty(),
            missing,
            recipe.instructions ?: "",
            uid,
            "fridge"
        )
        addHistory(dbHelper, uid, "recipe", recipe.name ?: "Рецепт", "Режим холодильника. Докупка: ${missing.joinToString(", ").ifBlank { "нет" }}", recipeId = recipeId)
        addToFavoriteRecipes(dbHelper, uid, recipeId)
        return recipeId
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
    }
}
