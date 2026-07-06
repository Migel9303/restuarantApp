package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        dbHelper = Database(this)

        val user: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }
        if (user?.active == true && !user.email.isNullOrBlank()) saveSession(this, user.email, true)
        email = sessionEmail(this).ifBlank { user?.email ?: "" }

        setupBottomNavigation()
        renderHistory()
    }

    private fun renderHistory() {
        val userId = getUserIdByEmail(dbHelper, email)
        val list = findViewById<LinearLayout>(R.id.llHistoryList)
        val clearButton = findViewById<Button>(R.id.btnClearHistory)
        list.removeAllViews()

        if (userId <= 0) {
            list.addView(infoText("Войдите, чтобы видеть историю запросов, ресторанов и рецептов."))
            clearButton.visibility = View.GONE
            return
        }

        val items = getHistory(dbHelper, userId)
        clearButton.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        clearButton.setOnClickListener {
            clearHistory(dbHelper, userId)
            Toast.makeText(this, "История очищена", Toast.LENGTH_SHORT).show()
            renderHistory()
        }

        if (items.isEmpty()) {
            list.addView(infoText("История пока пуста. Откройте ‘Что поесть сегодня?’ или ‘Что приготовить из холодильника?’ и выберите вариант."))
            return
        }

        items.forEach { item ->
            val card = baseCard()
            val body = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(14), dp(16), dp(14))
            }
            body.addView(TextView(this).apply {
                text = "${item.typeLabel()}: ${item.title}"
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })
            body.addView(TextView(this).apply {
                text = item.details.ifBlank { "Без дополнительных деталей" }
                textSize = 15f
                setPadding(0, dp(8), 0, 0)
            })
            body.addView(TextView(this).apply {
                text = "${item.createdAt}\nНажмите, чтобы повторить вариант"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, dp(8), 0, 0)
            })
            card.addView(body)
            card.setOnClickListener {
                addHistory(dbHelper, userId, item.type, item.title, "Повтор ранее выбранного варианта: ${item.details}")
                Toast.makeText(this, "Повтор сохранён", Toast.LENGTH_SHORT).show()
                renderHistory()
            }
            list.addView(card)
        }
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

    private fun HistoryItem.typeLabel(): String = when (type) {
        "restaurant" -> "Ресторан"
        "recipe" -> "Рецепт"
        "query" -> "Запрос"
        else -> type
    }
}
