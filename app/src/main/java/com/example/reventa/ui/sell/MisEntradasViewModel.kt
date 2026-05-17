package com.example.reventa.ui.sell

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.Entrada
import com.example.reventa.model.Ticket
import kotlinx.coroutines.launch

class MisEntradasViewModel(private val apiService: ApiService) : ViewModel() {

    private val _misEntradas = MutableLiveData<List<Entrada>>()
    val misEntradas: LiveData<List<Entrada>> get() = _misEntradas

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun cargarMisEntradas(idUsuario: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.obtenerMisVentas(idUsuario) // Pasamos el ID aquí
                if (response.isSuccessful) {
                    _misEntradas.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar las entradas: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Fallo de conexión: ${e.localizedMessage}"
            }
        }
    }


}