package com.example.reventa.ui.login

// Los 4 estados posibles de tu pantalla de login
sealed class LoginState {
    object Idle : LoginState() // Esperando a que el usuario haga algo
    object Loading : LoginState() // Ruedita dando vueltas
    object Success : LoginState() // ¡Login correcto!
    data class Error(val message: String) : LoginState() // Falló la contraseña o el internet
}