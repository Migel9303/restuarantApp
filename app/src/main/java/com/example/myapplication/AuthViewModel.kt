package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.db.Database


class AuthViewModel : ViewModel() {
    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn
    var email = ""

    fun login() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}