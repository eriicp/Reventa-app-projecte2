package com.example.reventa.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.api.auth.AuthRepository
import com.example.reventa.model.UsuarioDto
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _perfil = MutableLiveData<UsuarioDto?>()
    val perfil: LiveData<UsuarioDto?> = _perfil

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _logoutExitoso = MutableLiveData<Boolean>()
    val logoutExitoso: LiveData<Boolean> = _logoutExitoso

    // Carga los datos desde Spring Boot usando el ID del usuario logueado
    fun cargarDatosPerfil(idUsuario: Long) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getPerfilUsuario(idUsuario)
                if (response.isSuccessful && response.body() != null) {
                    _perfil.value = response.body()
                } else {
                    _error.value = "Error al obtener datos del servidor"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Borra los tokens locales mediante el AuthRepository
    fun cerrarSesion() {
        viewModelScope.launch {
            authRepository.logout() // Limpia el JWT en SharedPreferences
            _logoutExitoso.value = true
        }
    }

    fun cargarPerfilUsuarioLogueado() {
        _loading.value = true
        viewModelScope.launch {
            // Obtenemos el ID de forma reactiva desde el repositorio/preferences
            authRepository.getUserId().collect { id ->
                if (id != null && id != 0L) {
                    try {
                        val response = apiService.getPerfilUsuario(id)
                        if (response.isSuccessful && response.body() != null) {
                            _perfil.value = response.body()
                        } else {
                            _error.value = "Error al obtener datos: Código ${response.code()}"
                        }
                    } catch (e: Exception) {
                        _error.value = "Error de red: ${e.message}"
                    } finally {
                        _loading.value = false
                    }
                } else {
                    _loading.value = false
                    _error.value = "No se ha encontrado una sesión de usuario válida."
                }
            }
        }
    }
}