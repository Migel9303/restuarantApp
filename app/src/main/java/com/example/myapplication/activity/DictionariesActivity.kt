package com.example.myapplication.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class DictionariesActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private val types = linkedMapOf("Кухни" to "cuisine", "Диеты" to "diet", "Аллергены" to "allergen", "Категории продуктов" to "product_category")
    private var selectedType = "cuisine"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionaries)
        dbHelper = Database(this)
        setupSpinner()
        setupBottomNavigation()
        findViewById<Button>(R.id.btnAddDictionaryValue).setOnClickListener {
            val valueField = findViewById<EditText>(R.id.etDictionaryValue)
            addDictionaryValue(dbHelper, selectedType, valueField.text.toString())
            valueField.setText("")
            Toast.makeText(this, "Значение добавлено", Toast.LENGTH_SHORT).show()
            renderValues()
        }
        renderValues()
    }

    private fun setupSpinner() {
        val spinner = findViewById<Spinner>(R.id.spDictionaryType)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types.keys.toList())
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedType = types.values.toList()[position]
                renderValues()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun renderValues() {
        if (!::dbHelper.isInitialized) return
        val list = findViewById<LinearLayout>(R.id.llDictionaryValues)
        list.removeAllViews()
        val values = getDictionaryValues(dbHelper, selectedType)
        if (values.isEmpty()) {
            list.addView(infoText("Справочник пока пуст."))
            return
        }
        values.forEach { value ->
            val card = baseCard()
            val body = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(16), dp(14), dp(16), dp(14))
            }
            body.addView(TextView(this).apply {
                text = value
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })
            body.addView(TextView(this).apply {
                text = "Нажмите, чтобы удалить"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, dp(6), 0, 0)
            })
            card.addView(body)
            card.setOnClickListener {
                deleteDictionaryValue(dbHelper, selectedType, value)
                Toast.makeText(this, "Значение удалено", Toast.LENGTH_SHORT).show()
                renderValues()
            }
            list.addView(card)
        }
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
