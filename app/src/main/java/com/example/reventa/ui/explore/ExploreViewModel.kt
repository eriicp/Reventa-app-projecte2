package com.example.reventa.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.Evento
import kotlinx.coroutines.launch

class ExploreViewModel(private val apiService: ApiService) : ViewModel() {

    private val _eventos = MutableLiveData<List<Evento>>()
    val eventos: LiveData<List<Evento>> = _eventos

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // ¡ELIMINADO EL init{} PARA NO HACER DOBLE LLAMADA!

    // NUEVA FUNCIÓN: Decide qué datos cargar al abrir la pantalla
    fun cargarEventosIniciales(categoriaHome: String?) {
        when (categoriaHome) {
            "Música" -> fetchEventsByCategory("concierto")
            "Deportes" -> fetchEventsByCategory("deporte")
            "Festivales" -> fetchEventsByCategory("festival")
            "Teatro" -> fetchEventsByCategory("teatro")
            else -> fetchEvents() // Si es nulo (venimos del menú normal), carga todas
        }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            try {
                val response = apiService.findEvents()
                if (response.isSuccessful) {
                    _eventos.value = response.body() ?: emptyList()
                } else {
                    _error.postValue("Error del servidor: Código ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Fallo de red: ${e.message}")
            }
        }
    }

    fun fetchEventsByCategory(categoria: String) {
        viewModelScope.launch {
            try {
                val response = apiService.findEventsByCategory(categoria)
                if (response.isSuccessful) {
                    _eventos.value = response.body() ?: emptyList()
                } else {
                    _error.postValue("No se encontraron eventos")
                }
            } catch (e: Exception) {
                _error.postValue("Error al filtrar: ${e.message}")
            }
        }
    }
}