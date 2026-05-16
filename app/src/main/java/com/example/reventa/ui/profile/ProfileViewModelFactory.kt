package com.example.reventa.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.auth.AuthRepository
import com.example.reventa.api.ItemAPI // Ajusta según donde inicialices tu Retrofit
import com.example.reventa.api.auth.UserPreferences

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Inicializamos las dependencias apuntando a tus singletons/clientes reales
            val apiService = ItemAPI.API(context)
            val userPreferences = UserPreferences(context)
            val authRepository = AuthRepository(userPreferences)

            return ProfileViewModel(apiService, authRepository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}