package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.AuthViewModel
import com.example.myapplication.R
import com.example.myapplication.db.Database
import com.example.myapplication.db.User
import com.example.myapplication.db.Recipe
import com.example.myapplication.db.dbHelper
import com.example.myapplication.db.getRecipeCookTimeByID
import com.example.myapplication.db.getRecipeTitleByID
import com.example.myapplication.db.getRecipeCookTimeByID
import com.example.myapplication.db.getRecipeIngredientsByID
import com.example.myapplication.db.getRecipeInstructionsByID
import com.example.myapplication.db.getRecipeTitleByID
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.getValue

class RecipeDetailActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        dbHelper = Database(this)

        val tvRecipeName = findViewById<TextView>(R.id.tvRecipeName)
        val tvRecipeCookTime = findViewById<TextView>(R.id.tvCookTime)
        val tvRecipeIngredients = findViewById<TextView>(R.id.tvIngredients)
        val tvRecipeInstructions = findViewById<TextView>(R.id.tvInstructions)

        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)

        val user: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }

        if (user?.email != null) {
            authViewModel.email = user.email
            if (authViewModel.email == "") {
                authViewModel.logout()
            }
            else {
                authViewModel.login()
            }
        }


        val recipe: Recipe? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("RECIPE", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("RECIPE")
        }

        if (recipe?.id != null) {
            tvRecipeName.text = getRecipeTitleByID(dbHelper, recipe.id)
            tvRecipeCookTime.text = getRecipeCookTimeByID(dbHelper, recipe.id).toString()

            tvRecipeInstructions.text = getRecipeInstructionsByID(dbHelper, recipe.id)

            val ingredientsText = getRecipeIngredientsByID(dbHelper, recipe.id).replace(",", "\n")
            tvRecipeIngredients.text = ingredientsText
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    // переход на главный экран
                    if (user?.active == true) {
                        val item = User(email = authViewModel.email, active = authViewModel.isLoggedIn.value)

                        val intent = Intent(this, MainActivity::class.java).apply {
                            putExtra("USER", item)
                        }
                        startActivity(intent) }
                    true
                }
                R.id.btnProfile -> {
                    if (user?.active == true) {
                        val user = User(
                            email = authViewModel.email,
                            active = authViewModel.isLoggedIn.value
                        )
                        val intent = Intent(this, ProfileActivity::class.java).apply {
                            putExtra("USER", user)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Войдите в аккаунт", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.btnBack -> {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }
                else -> false
            }
        }


    }
}