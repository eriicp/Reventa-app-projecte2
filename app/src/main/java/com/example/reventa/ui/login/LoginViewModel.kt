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
                val peticion = LoginRequest(username = email, password = contrasena)
                val response = apiService.login(peticion)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    authRepository.guardarToken(token)

                    // AHORA LE PASAMOS EL TOKEN A LA VISTA
                    _loginState.value = LoginState.Success(token)
                } else {
                    _loginState.value = LoginState.Error("Credenciales incorrectas")
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