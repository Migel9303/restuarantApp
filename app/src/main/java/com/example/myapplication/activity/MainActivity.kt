package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private var currentEmail: String = ""
    private var isActiveUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = Database(this)

        val user: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }

        if (user?.active == true && !user.email.isNullOrBlank()) {
            saveSession(this, user.email, true)
        }

        currentEmail = sessionEmail(this)
        isActiveUser = currentEmail.isNotBlank()
        setupInitialDesign()
    }

    override fun onResume() {
        super.onResume()
        if (::dbHelper.isInitialized) {
            currentEmail = sessionEmail(this)
            isActiveUser = currentEmail.isNotBlank()
            setupInitialDesign()
        }
    }

    private fun setupInitialDesign() {
        val btnWhatToEat = findViewById<Button>(R.id.btnWhatToEat)
        val btnWhatToCookFromFridge = findViewById<Button>(R.id.btnWhatToCookFromFridge)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val linkReg = findViewById<TextView>(R.id.linkReg)
        val switchAdminMode = findViewById<Switch>(R.id.swAdminMode)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        val btnReport = findViewById<Button>(R.id.btnReports)
        val btnManage = findViewById<Button>(R.id.btnReports2)
        val btnRecipes = findViewById<Button>(R.id.Recipes)
        val btnRestaurants = findViewById<Button>(R.id.btnRestaurants)
        val btnHistory = findViewById<Button>(R.id.btnHistory)
        val welcome = findViewById<TextView>(R.id.textView2)
        val username = findViewById<TextView>(R.id.userName)

        btnReport.visibility = View.GONE
        btnManage.visibility = View.GONE
        btnRecipes.visibility = if (isActiveUser) View.VISIBLE else View.GONE
        btnRestaurants.visibility = if (isActiveUser) View.VISIBLE else View.GONE
        btnHistory.visibility = if (isActiveUser) View.VISIBLE else View.GONE
        bottomNav.visibility = if (isActiveUser) View.VISIBLE else View.GONE
        bottomNav.menu.findItem(R.id.btnHome)?.isVisible = false
        bottomNav.menu.findItem(R.id.btnBack)?.isVisible = false

        if (isActiveUser) {
            linkReg.visibility = View.GONE
            btnLogin.text = "Выйти"
            switchAdminMode.visibility = View.GONE
            welcome.visibility = View.VISIBLE
            username.visibility = View.VISIBLE
            username.text = getNameByEmail(dbHelper, currentEmail) ?: currentEmail
        } else {
            linkReg.visibility = View.VISIBLE
            btnLogin.text = "Войти"
            switchAdminMode.visibility = View.VISIBLE
            welcome.visibility = View.GONE
            username.visibility = View.GONE
        }

        switchAdminMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                btnLogin.visibility = View.GONE
                linkReg.visibility = View.GONE
                btnReport.visibility = View.VISIBLE
                btnManage.visibility = View.VISIBLE
                btnRecipes.visibility = View.GONE
                btnRestaurants.visibility = View.GONE
                btnHistory.visibility = View.GONE
            } else {
                btnLogin.visibility = View.VISIBLE
                linkReg.visibility = View.VISIBLE
                btnReport.visibility = View.GONE
                btnManage.visibility = View.GONE
            }
        }

        btnWhatToEat.setOnClickListener {
            startActivity(Intent(this, WhatToEatActivity::class.java).putExtra("USER", activeUserExtra()))
        }

        btnWhatToCookFromFridge.setOnClickListener {
            startActivity(Intent(this, WhatToCookFromFridgeActivity::class.java).putExtra("USER", activeUserExtra()))
        }

        btnRestaurants.setOnClickListener {
            startActivity(Intent(this, RestaurantsActivity::class.java).putExtra("USER", activeUserExtra()))
        }

        btnRecipes.setOnClickListener {
            startActivity(Intent(this, RecipesActivity::class.java).putExtra("USER", activeUserExtra()))
        }

        btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).putExtra("USER", activeUserExtra()))
        }

        btnReport.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        btnManage.setOnClickListener {
            startActivity(Intent(this, ManageSystemActivity::class.java).putExtra("USER", User(email = "", active = true)))
        }

        linkReg.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        btnLogin.setOnClickListener {
            if (isActiveUser) {
                clearSession(this)
                currentEmail = ""
                isActiveUser = false
                Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
                setupInitialDesign()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnProfile -> {
                    if (isActiveUser) {
                        startActivity(Intent(this, ProfileActivity::class.java).putExtra("USER", activeUserExtra()))
                    } else {
                        Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun activeUserExtra(): User = User(email = currentEmail, active = isActiveUser)
}
