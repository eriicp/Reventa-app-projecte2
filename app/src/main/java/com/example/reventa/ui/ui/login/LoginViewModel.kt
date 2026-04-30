package com.example.reventa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.api.ItemAPI
import com.example.reventa.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun iniciarSesion(email: String, contrasena: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // 1. Preparamos los datos tal y como los espera tu Spring Boot
                val peticion = LoginRequest(username = email, password = contrasena)

                // 2. Hacemos la llamada real a tu API
                val response = ItemAPI.API().login(peticion)

                // 3. Evaluamos la respuesta HTTP
                if (response.isSuccessful && response.body() != null) {
                    // ¡HTTP 200 OK! El servidor nos dio el Token
                    val token = response.body()!!.token

                    // TODO: Aquí le pasaremos el 'token' a Jetpack DataStore para guardarlo
                    // Ejemplo: authRepository.guardarToken(token)

                    _loginState.value = LoginState.Success
                } else {
                    // HTTP 401, 403, etc. (Ej. contraseña incorrecta)
                    // Puedes afinar el mensaje dependiendo del response.code()
                    _loginState.value = LoginState.Error("Credenciales incorrectas")
                }

            } catch (e: IOException) {
                // Error GRAVE de red: El móvil no tiene internet, o tu Spring Boot (localhost) está apagado.
                _loginState.value = LoginState.Error("No se pudo conectar al servidor. Comprueba tu conexión.")
            } catch (e: Exception) {
                // Cualquier otro error inesperado de la app
                _loginState.value = LoginState.Error("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}