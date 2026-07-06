package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapters.TableUsers
import com.example.myapplication.db.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class UsersActivity : AppCompatActivity() {
    private lateinit var dbHelper: Database
    private lateinit var adapter: TableUsers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        dbHelper = Database(this)

        val recycler = findViewById<RecyclerView>(R.id.resUsers)
        recycler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        adapter = TableUsers { user -> showActions(user) }
        recycler.adapter = adapter
        adapter.submitList(getAllNames(dbHelper))

        val bottomNav = findViewById<BottomNavigationView>(R.id.navView)
        bottomNav.menu.findItem(R.id.btnProfile)?.isVisible = false
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btnHome -> {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("USER", User(email = "", active = false)))
                    true
                }
                R.id.btnBack -> { onBackPressedDispatcher.onBackPressed(); true }
                else -> false
            }
        }
    }

    private fun showActions(user: User) {
        val userId = getUserIdByEmail(dbHelper, user.email)
        val email = user.email ?: return
        val isActive = !isUserBlocked(dbHelper, email)
        AlertDialog.Builder(this)
            .setTitle(user.name ?: email)
            .setItems(arrayOf("Профиль", if (isActive) "Заблокировать" else "Разблокировать", "Удалить")) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, ProfileActivity::class.java).putExtra("USER", User(email = email, active = false)))
                    1 -> {
                        setUserBlocked(dbHelper, userId, isActive)
                        adapter.submitList(getAllNames(dbHelper))
                        Toast.makeText(this, "Статус изменён", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        deleteUser(dbHelper, userId)
                        adapter.submitList(getAllNames(dbHelper))
                        Toast.makeText(this, "Пользователь удалён", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }
}
