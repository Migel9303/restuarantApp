package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.contentValuesOf
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        dbHelper = Database(this)

        val emailField = findViewById<EditText>(R.id.etMail)
        val passField = findViewById<EditText>(R.id.etPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin2)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val pass = passField.text.toString()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (getIsEmailExists(dbHelper, email) == 0) {
                Toast.makeText(this, "Пользователя с такой почтой не существует", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isUserBlocked(dbHelper, email)) {
                Toast.makeText(this, "Аккаунт заблокирован администратором", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (getPassByEmail(dbHelper, email) != pass) {
                Toast.makeText(this, "Неверный логин и/или пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveSession(this, email, true)
            Toast.makeText(this, "С возвращением!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = email, active = true)))
            finish()
        }

        // Долгое нажатие на кнопку входа — восстановление пароля без изменения исходного макета.
        btnLogin.setOnLongClickListener {
            val email = emailField.text.toString().trim()
            if (email.isBlank() || getIsEmailExists(dbHelper, email) == 0) {
                Toast.makeText(this, "Введите зарегистрированный email", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.writableDatabase.update(
                    Database.TABLE_USERS,
                    contentValuesOf(Database.COLUMN_PASSWORD to "123456"),
                    "${Database.COLUMN_EMAIL}=?",
                    arrayOf(email)
                )
                Toast.makeText(this, "Временный пароль: 123456", Toast.LENGTH_LONG).show()
            }
            true
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
}
