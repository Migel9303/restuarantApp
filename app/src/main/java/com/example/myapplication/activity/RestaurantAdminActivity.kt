package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.contentValuesOf
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class RestaurantAdminActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_admin)
        dbHelper = Database(this)
        findViewById<Button>(R.id.btnAddRestaurant).setOnClickListener { showRestaurantForm(null) }
        setupBottomNavigation()
        renderRestaurants()
    }

    private fun renderRestaurants() {
        val list = findViewById<LinearLayout>(R.id.llRestaurantAdminList)
        list.removeAllViews()
        getAllRestaurants(dbHelper).forEach { rest ->
            val card = baseCard()
            val body = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(14), dp(16), dp(14))
            }
            body.addView(TextView(this).apply {
                text = rest.name
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })
            body.addView(TextView(this).apply {
                text = "${rest.cuisine} • ${rest.rating}★ • ${rest.priceRange}"
                textSize = 15f
                setPadding(0, dp(6), 0, 0)
            })
            body.addView(TextView(this).apply {
                text = "Меню: ${getMenuForRestaurant(dbHelper, rest.id).size} поз.\nНажмите для редактирования"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, dp(8), 0, 0)
            })
            card.addView(body)
            card.setOnClickListener { showActions(rest) }
            list.addView(card)
        }
    }

    private fun showActions(rest: Restaurant) {
        AlertDialog.Builder(this)
            .setTitle(rest.name)
            .setItems(arrayOf("Редактировать ресторан", "Добавить блюдо", "Удалить ресторан", "Показать меню")) { _, which ->
                when (which) {
                    0 -> showRestaurantForm(rest)
                    1 -> showMenuForm(rest.id)
                    2 -> { deleteRestaurant(dbHelper, rest.id); renderRestaurants() }
                    3 -> showMenu(rest)
                }
            }.show()
    }

    private fun showRestaurantForm(rest: Restaurant?) {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(8)) }
        fun field(hint: String, value: String = "", number: Boolean = false) = EditText(this).apply {
            this.hint = hint
            setText(value)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            if (number) inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val name = field("Название", rest?.name ?: "")
        val loc = field("Адрес", rest?.location ?: "")
        val cuisine = field("Кухня", rest?.cuisine ?: "")
        val rating = field("Рейтинг", rest?.rating?.toString() ?: "4.5", true)
        val avg = field("Средний чек", rest?.avgCheck?.toString() ?: "900", true)
        val dist = field("Минут от пользователя", rest?.distanceMin?.toString() ?: "10", true)
        val desc = field("Описание", rest?.description ?: "")
        listOf(name, loc, cuisine, rating, avg, dist, desc).forEach { layout.addView(it) }
        AlertDialog.Builder(this)
            .setTitle(if (rest == null) "Добавить ресторан" else "Редактировать ресторан")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                val avgCheck = avg.text.toString().toIntOrNull() ?: 900
                addOrUpdateRestaurant(dbHelper, Restaurant(
                    id = rest?.id ?: 0,
                    name = name.text.toString().ifBlank { "Новый ресторан" },
                    location = loc.text.toString(),
                    cuisine = cuisine.text.toString().ifBlank { "Русская" },
                    rating = rating.text.toString().toDoubleOrNull() ?: 4.5,
                    priceRange = "${avgCheck - 300}–${avgCheck + 300} ₽",
                    avgCheck = avgCheck,
                    distanceMin = dist.text.toString().toIntOrNull() ?: 10,
                    description = desc.text.toString(),
                    contact = rest?.contact ?: "+7 000 000-00-00",
                    openingHours = rest?.openingHours ?: "10:00–22:00",
                    hasDelivery = rest?.hasDelivery ?: true,
                    hasBooking = rest?.hasBooking ?: true
                ))
                renderRestaurants()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showMenuForm(restaurantId: Int) {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(8)) }
        fun field(hint: String, value: String = "", number: Boolean = false) = EditText(this).apply {
            this.hint = hint
            setText(value)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            if (number) inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val dish = field("Название блюда")
        val price = field("Цена", "450", true)
        val cal = field("Калорийность", "500", true)
        val ingredients = field("Ингредиенты")
        val allergens = field("Аллергены")
        listOf(dish, price, cal, ingredients, allergens).forEach { layout.addView(it) }
        AlertDialog.Builder(this)
            .setTitle("Добавить блюдо")
            .setView(layout)
            .setPositiveButton("Сохранить") { _, _ ->
                dbHelper.writableDatabase.insert(Database.TABLE_RESTAURANT_MENU, null, contentValuesOf(
                    Database.COLUMN_RESTAURANT_ID to restaurantId,
                    Database.COLUMN_DISH_NAME to dish.text.toString().ifBlank { "Новое блюдо" },
                    Database.COLUMN_DESCRIPTION to "Добавлено администратором",
                    Database.COLUMN_PRICE to (price.text.toString().toDoubleOrNull() ?: 450.0),
                    Database.COLUMN_CALORIES to (cal.text.toString().toIntOrNull() ?: 500),
                    Database.COLUMN_INGREDIENTS_TEXT to ingredients.text.toString(),
                    Database.COLUMN_ALLERGENS_TEXT to allergens.text.toString(),
                    Database.COLUMN_IS_VEGETARIAN to 0,
                    Database.COLUMN_IS_VEGAN to 0,
                    Database.COLUMN_IS_GLUTEN_FREE to if (allergens.text.toString().contains("глютен", true)) 0 else 1
                ))
                renderRestaurants()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showMenu(rest: Restaurant) {
        AlertDialog.Builder(this)
            .setTitle("Меню: ${rest.name}")
            .setMessage(getMenuForRestaurant(dbHelper, rest.id).joinToString("\n\n") { "${it.dishName} — ${it.price.toInt()} ₽\n${it.ingredients}" }.ifBlank { "Меню пустое" })
            .setPositiveButton("Ок", null)
            .show()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> { startActivity(Intent(this, MainActivity::class.java)); true }
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }

    private fun baseCard(): CardView = CardView(this).apply {
        radius = dp(8).toFloat()
        cardElevation = dp(4).toFloat()
        useCompatPadding = true
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, dp(8), 0, dp(8))
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
