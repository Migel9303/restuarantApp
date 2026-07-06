package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReportsActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        dbHelper = Database(this)
        setupBottomNavigation()
        renderReports()
    }

    private fun renderReports() {
        val stats = getReportStats(dbHelper)
        val container = findViewById<LinearLayout>(R.id.llReportsContainer)
        container.removeAllViews()
        container.addView(card("Общая статистика", "Пользователей: ${stats.users}\nРесторанов: ${stats.restaurants}\nСохранённых рецептов: ${stats.recipes}\nЗапросов ‘Что поесть’: ${stats.queries}\nЗаписей истории: ${stats.history}"))
        container.addView(card("Популярные запросы", stats.popularQueries.ifEmpty { listOf("нет данных") }.joinToString("\n")))
        container.addView(card("Популярные рестораны", stats.popularRestaurants.ifEmpty { listOf("нет данных") }.joinToString("\n")))
        container.addView(card("Популярные рецепты", stats.popularRecipes.ifEmpty { listOf("нет данных") }.joinToString("\n")))
        container.addView(card("Контроль качества", "Рекомендации фильтруются по диете и аллергенам. Для ответов в промышленной версии нужна отдельная пост-валидация ингредиентов."))
    }

    private fun card(title: String, details: String): CardView {
        val card = baseCard()
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
        body.addView(TextView(this).apply {
            text = title
            textSize = 18f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        body.addView(TextView(this).apply {
            text = details
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(8), 0, 0)
        })
        card.addView(body)
        return card
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
