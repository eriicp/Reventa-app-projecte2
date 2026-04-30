package com.example.reventa.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reventa.api.auth.AuthRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val isUserLoggedIn: Flow<Boolean> = authRepository.isLoggedIn

    // Función para cuando el usuario pulse el botón de "Cerrar sesión"
    suspend fun cerrarSesion() {
        authRepository.logout()
    }


}