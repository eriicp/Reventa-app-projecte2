package com.example.reventa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.api.auth.AuthRepository
import com.example.reventa.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun iniciarSesion(email: String, contrasena: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                // 1. Llamamos a TU Retrofit (ApiService)
                val peticion = LoginRequest(email = email, password = contrasena)
                val response = apiService.login(peticion)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    authRepository.saveAuthData(loginResponse.token, loginResponse.idUsuario)
                    _loginState.value = LoginState.Success(loginResponse.token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ ERROR DE LOGIN: Código ${response.code()} - $errorBody")
                    _loginState.value = LoginState.Error("Fallo al iniciar sesión: Código ${response.code()}")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de red: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}