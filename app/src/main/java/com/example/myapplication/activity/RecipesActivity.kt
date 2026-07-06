package com.example.myapplication.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.AuthViewModel
import com.example.myapplication.R
import com.example.myapplication.adapters.TableRecipes
import com.example.myapplication.adapters.TableUsers
import com.example.myapplication.db.Database
import com.example.myapplication.db.Recipe
import com.example.myapplication.db.User
import com.example.myapplication.db.getAllNames
import com.example.myapplication.db.getAllRecipe
import com.example.myapplication.db.getUserIdByEmail
import com.example.myapplication.db.getUserRecipes
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.getValue

class RecipesActivity : AppCompatActivity() {
    lateinit var dbHelper: Database
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        dbHelper = Database(this)

        val user: User? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("USER", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("USER")
        }
        if (user?.active != true) {
            authViewModel.email = ""
            authViewModel.logout()
            bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        }
        else {
            authViewModel.email = user.email.toString()
            authViewModel.login()
        }


    val recycler = findViewById<RecyclerView>(R.id.resRecepes)
    recycler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        val adapter = TableRecipes { recipe ->
            val item = User(email = authViewModel.email, active = authViewModel.isLoggedIn.value)
            val recipe = Recipe(id = recipe.id)
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("USER", item)
                putExtra("RECIPE", recipe)}
            startActivity(intent)
        }

        recycler.adapter = adapter

        if (authViewModel.email == "") {
            adapter.submitList(getAllRecipe(dbHelper))
        }
        else {
            adapter.submitList(getUserRecipes(dbHelper, getUserIdByEmail(dbHelper, authViewModel.email)))
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    // переход на главный экран
                    val item = User(email = authViewModel.email, active = authViewModel.isLoggedIn.value)

                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("USER", item)
                    }
                    startActivity(intent)
                    true
                }
                R.id.btnBack -> {
                    onBackPressedDispatcher.onBackPressed()
                    true
                }
                else -> false
            }
        }
} }