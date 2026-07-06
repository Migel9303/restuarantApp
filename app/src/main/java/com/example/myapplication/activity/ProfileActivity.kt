package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class ProfileActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var email: String = ""
    private var active = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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

        setupProfile()
    }

    private fun setupProfile() {
        val usernameField = findViewById<EditText>(R.id.etProfNickname)
        val emailField = findViewById<EditText>(R.id.etProfEmail)
        val passField = findViewById<EditText>(R.id.etProfPassword)
        val dietField = findViewById<Spinner>(R.id.spDiet)
        val allergiesField = findViewById<Spinner>(R.id.spProfAllergies)
        val cuisinesField = findViewById<ChipGroup>(R.id.chipGroupCuisines)
        val skillsField = findViewById<Spinner>(R.id.spProfSkillLevel)
        val saveBtn = findViewById<Button>(R.id.btnSave)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        setupSpinner(dietField, R.array.diet_options)
        setupSpinner(allergiesField, R.array.allergy_options)
        setupSpinner(skillsField, R.array.skill_levels)
        cuisinesField.removeAllViews()

        if (!active) {
            Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = getUserIdByEmail(dbHelper, email)
        val prefs = getPreferencesByUserId(dbHelper, userId)
        usernameField.setText(getNameByEmail(dbHelper, email) ?: "")
        emailField.setText(email)
        passField.hint = "Пароль для подтверждения"
        passField.visibility = View.VISIBLE
        saveBtn.visibility = View.VISIBLE

        (dietField.adapter as? ArrayAdapter<String>)?.getPosition(prefs.diet)?.takeIf { it >= 0 }?.let { dietField.setSelection(it) }
        (allergiesField.adapter as? ArrayAdapter<String>)?.getPosition(prefs.allergies)?.takeIf { it >= 0 }?.let { allergiesField.setSelection(it) }
        val skillIndex = (prefs.skillLevel - 1).coerceIn(0, 2)
        skillsField.setSelection(skillIndex)

        listOf("Итальянская", "Японская", "Русская", "Грузинская", "Китайская", "Индийская", "Мексиканская", "Корейская").forEach { cuisine ->
            cuisinesField.addView(Chip(this).apply {
                text = cuisine
                isCheckable = true
                isClickable = true
                isChecked = prefs.favoriteCuisines.contains(cuisine, ignoreCase = true)
            })
        }

        saveBtn.setOnClickListener {
            val enteredPassword = passField.text.toString()
            if (enteredPassword != getPassByEmail(dbHelper, email)) {
                Toast.makeText(this, "Введите текущий пароль для сохранения", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newEmail = emailField.text.toString().trim()
            val newName = usernameField.text.toString().trim()
            if (newEmail.isBlank() || newName.isBlank()) {
                Toast.makeText(this, "Имя и email не должны быть пустыми", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCuisines = (0 until cuisinesField.childCount)
                .map { cuisinesField.getChildAt(it) as Chip }
                .filter { it.isChecked }
                .joinToString(", ") { it.text.toString() }
                .ifBlank { "Итальянская" }

            updateUser(dbHelper, userId, newName, newEmail, null)
            upsertPreferences(
                dbHelper,
                userId,
                UserPreferences(
                    favoriteCuisines = selectedCuisines,
                    diet = dietField.selectedItem.toString(),
                    allergies = allergiesField.selectedItem.toString(),
                    budget = prefs.budget,
                    cookingTime = prefs.cookingTime,
                    skillLevel = skillsField.selectedItemPosition + 1
                )
            )
            saveSession(this, newEmail, true)
            email = newEmail
            Toast.makeText(this, "Профиль обновлён ✅", Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = email, active = true)))
                    true
                }
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
