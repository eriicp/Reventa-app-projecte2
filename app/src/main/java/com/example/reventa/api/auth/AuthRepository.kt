package com.example.reventa.api.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val userPreferences: UserPreferences) {

    // 1. Observa si hay un usuario logueado o no
    val isLoggedIn: Flow<Boolean> = userPreferences.userToken.map { token ->
        !token.isNullOrEmpty()
    }

    // 2.Obtener el ID del usuario de forma reactiva (Para el Perfil)
    fun getUserId(): Flow<Long?> {
        return userPreferences.userId
    }

    // 3. Guarda ambos datos al hacer Login con éxito
    suspend fun saveAuthData(token: String, userId: Long) {
        userPreferences.saveAuthData(token, userId)
    }

    // 4. Limpia todo al cerrar sesión (El que usa el ProfileViewModel)
    suspend fun clearAuthToken() {
        userPreferences.clearAuthToken()
    }

    // Mantenemos este por si lo estabas usando en alguna otra pantalla con ese nombre
    suspend fun logout() {
        userPreferences.clearAuthToken()
    }
}