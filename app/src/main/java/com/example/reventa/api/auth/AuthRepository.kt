package com.example.reventa.api.auth

import UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val userPreferences: UserPreferences) {

    // Observa si hay un usuario logueado o no
    val isLoggedIn: Flow<Boolean> = userPreferences.userToken.map { token ->
        !token.isNullOrEmpty()
    }

    suspend fun guardarToken(token: String) {
        userPreferences.saveToken(token)
    }

    // Borra el token para cerrar sesión
    suspend fun logout() {
        userPreferences.saveToken("")
    }
}