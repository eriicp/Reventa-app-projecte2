package com.example.reventa.ui.login

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    // AÑADIMOS EL TOKEN AQUÍ:
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}