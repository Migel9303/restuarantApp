package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class RestaurantsActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var user: User? = null
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurants)
        dbHelper = Database(this)
        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }
        if (user?.active == true && !user?.email.isNullOrBlank()) saveSession(this, user?.email ?: "", true)
        email = sessionEmail(this).ifBlank { user?.email ?: "" }

        findViewById<Button>(R.id.btnRestaurantSearch).setOnClickListener {
            renderRestaurants(findViewById<EditText>(R.id.etRestaurantSearch).text.toString())
        }
        setupBottomNavigation()
        renderRestaurants()
    }

    private fun renderRestaurants(query: String = "") {
        val userId = currentUserId(this, dbHelper)
        val prefs = if (userId > 0) getPreferencesByUserId(dbHelper, userId) else UserPreferences()
        val restaurants = if (query.isBlank()) searchRestaurants(dbHelper, prefs) else searchRestaurants(dbHelper, prefs, query)
        val list = findViewById<LinearLayout>(R.id.llRestaurantList)
        val summary = findViewById<TextView>(R.id.tvRestaurantSummary)
        list.removeAllViews()
        summary.text = "Найдено: ${restaurants.size}. Учитываются бюджет, кухня, диета и аллергии из профиля."

        if (restaurants.isEmpty()) {
            list.addView(infoText("Подходящих ресторанов не найдено. Попробуйте изменить запрос или увеличить бюджет в профиле."))
            return
        }

        restaurants.forEach { rest ->
            val bestDish = getBestMenuItem(dbHelper, rest.id, prefs)
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
                text = "${rest.cuisine} • ${rest.rating}★ • ${rest.distanceMin} мин • ${rest.priceRange}"
                textSize = 15f
                setPadding(0, dp(6), 0, 0)
            })
            body.addView(TextView(this).apply {
                text = "Лучшее совпадение: ${bestDish?.dishName ?: "нет подходящего блюда"}${bestDish?.price?.toInt()?.let { " — $it ₽" } ?: ""}"
                textSize = 15f
                setPadding(0, dp(6), 0, 0)
            })
            body.addView(TextView(this).apply {
                text = rest.description + "\nНажмите для меню и отзывов"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, dp(8), 0, 0)
            })
            card.addView(body)
            card.setOnClickListener { showRestaurant(rest, bestDish) }
            list.addView(card)
        }
    }

    private fun showRestaurant(rest: Restaurant, bestDish: MenuItem?) {
        val menu = getMenuForRestaurant(dbHelper, rest.id).joinToString("\n\n") { item ->
            "${item.dishName} — ${item.price.toInt()} ₽, ${item.calories} ккал\n${item.description}\nИнгредиенты: ${item.ingredients}\nАллергены: ${item.allergens.ifBlank { "нет" }}"
        }
        val reviews = getReviews(dbHelper, rest.id).joinToString("\n") { "${it.rating}★ — ${it.text}" }.ifBlank { "Отзывов пока нет" }
        val message = "Адрес: ${rest.location}\nТелефон: ${rest.contact}\nВремя работы: ${rest.openingHours}\nДоставка: ${if (rest.hasDelivery) "есть" else "нет"}; бронь: ${if (rest.hasBooking) "есть" else "нет"}\n\nМЕНЮ:\n$menu\n\nОТЗЫВЫ:\n$reviews"
        AlertDialog.Builder(this)
            .setTitle(rest.name)
            .setMessage(message)
            .setPositiveButton("В избранное") { _, _ ->
                val userId = currentUserId(this, dbHelper)
                if (userId <= 0) Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show() else {
                    addToFavoriteRestaurants(dbHelper, userId, rest.id)
                    addHistory(dbHelper, userId, "restaurant", rest.name, "Выбран ресторан. Блюдо: ${bestDish?.dishName ?: "не выбрано"}", restaurantId = rest.id)
                    Toast.makeText(this, "Ресторан добавлен в избранное", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = email.isNotBlank()
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = email, active = email.isNotBlank())))
                    true
                }
                R.id.btnProfile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).putExtra("USER", User(email = email, active = email.isNotBlank())))
                    true
                }
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }

    private fun infoText(message: String): TextView = TextView(this).apply {
        text = message
        textSize = 16f
        setPadding(dp(8), dp(20), dp(8), dp(20))
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
