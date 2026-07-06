package com.example.myapplication.activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class RegisterActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        dbHelper = Database(this)

        val nickname = findViewById<EditText>(R.id.etNickname)
        val email = findViewById<EditText>(R.id.etEmail)
        val pass = findViewById<EditText>(R.id.etPassword)
        val pass2 = findViewById<EditText>(R.id.etRepeatPassword)
        val spDiet = findViewById<Spinner>(R.id.spDiet)
        val spAllergies = findViewById<Spinner>(R.id.spAllergies)
        val chipGroupCuisines = findViewById<ChipGroup>(R.id.chipGroupCuisines)
        val spSkillLevel = findViewById<Spinner>(R.id.spSkillLevel)
        val createBtn = findViewById<Button>(R.id.btnCreateAccount)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        setupSpinner(spDiet, R.array.diet_options)
        setupSpinner(spAllergies, R.array.allergy_options)
        setupSpinner(spSkillLevel, R.array.skill_levels)

        listOf("Итальянская", "Японская", "Русская", "Грузинская", "Китайская", "Индийская", "Мексиканская", "Корейская").forEach { cuisine ->
            chipGroupCuisines.addView(Chip(this).apply {
                text = cuisine
                isCheckable = true
                isClickable = true
            })
        }

        createBtn.setOnClickListener {
            val nickStr = nickname.text.toString().trim()
            val emailStr = email.text.toString().trim()
            val p1 = pass.text.toString()
            val p2 = pass2.text.toString()

            if (nickStr.isBlank() || emailStr.isBlank() || p1.isBlank() || p2.isBlank()) {
                Toast.makeText(this, "Заполните обязательные поля (*)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (getIsEmailExists(dbHelper, emailStr) != 0) {
                Toast.makeText(this, "Пользователь с такой почтой уже зарегистрирован", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p1 != p2) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newUserId = dbHelper.writableDatabase.insertWithOnConflict(
                Database.TABLE_USERS,
                null,
                contentValuesOf(
                    Database.COLUMN_USERNAME to nickStr,
                    Database.COLUMN_EMAIL to emailStr,
                    Database.COLUMN_PASSWORD to p1
                ),
                SQLiteDatabase.CONFLICT_ABORT
            ).toInt()

            if (newUserId <= 0) {
                Toast.makeText(this, "Не удалось создать аккаунт", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCuisines = (0 until chipGroupCuisines.childCount)
                .map { chipGroupCuisines.getChildAt(it) as Chip }
                .filter { it.isChecked }
                .joinToString(", ") { it.text.toString() }
                .ifBlank { "Итальянская" }

            upsertPreferences(
                dbHelper,
                newUserId,
                UserPreferences(
                    favoriteCuisines = selectedCuisines,
                    diet = spDiet.selectedItem.toString(),
                    allergies = spAllergies.selectedItem.toString(),
                    budget = 1000,
                    cookingTime = 30,
                    skillLevel = when (spSkillLevel.selectedItem.toString()) {
                        "Средний" -> 2
                        "Продвинутый" -> 3
                        else -> 1
                    }
                )
            )

            saveSession(this, emailStr, true)
            Toast.makeText(this, "Регистрация успешна ✅", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = emailStr, active = true)))
            finish()
        }

        bottomNav.menu.findItem(R.id.btnHome)?.isVisible = false
        bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, resId: Int) {
        ArrayAdapter.createFromResource(this, resId, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }
}
