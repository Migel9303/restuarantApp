package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.db.User
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManageSystemActivity : AppCompatActivity() {
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_managenent)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }

        findViewById<Button>(R.id.btnUsers).setOnClickListener {
            startActivity(Intent(this, UsersActivity::class.java).putExtra("USER", User(email = "", active = true)))
        }
        findViewById<Button>(R.id.btnRests).setOnClickListener {
            startActivity(Intent(this, RestaurantAdminActivity::class.java))
        }
        findViewById<Button>(R.id.btnRecipes).setOnClickListener {
            startActivity(Intent(this, RecipesActivity::class.java).putExtra("USER", User(email = "", active = false)))
        }
        findViewById<Button>(R.id.btnLists).setOnClickListener {
            startActivity(Intent(this, DictionariesActivity::class.java))
        }
        findViewById<Button>(R.id.btnReports4).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("USER", user ?: User(email = "", active = false)))
                    true
                }
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }
}
