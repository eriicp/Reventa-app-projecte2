package com.example.reventa.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI
import com.example.reventa.api.auth.AuthRepository
import com.example.reventa.api.auth.UserPreferences

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

            // 1. Instanciamos tus preferencias y repositorio
            val userPreferences = UserPreferences(context)
            val repository = AuthRepository(userPreferences)

            // 2. Instanciamos tu API (le pasamos el contexto, ver el punto 3 abajo)
            val apiService = ItemAPI.API(context)

            return LoginViewModel(apiService, repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}