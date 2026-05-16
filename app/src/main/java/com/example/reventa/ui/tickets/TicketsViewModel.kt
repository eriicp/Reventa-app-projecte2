package com.example.reventa.ui.tickets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.Ticket
import kotlinx.coroutines.launch

class TicketsViewModel(private val apiService: ApiService) : ViewModel() {

    private val _tickets = MutableLiveData<List<Ticket>>()
    val tickets: LiveData<List<Ticket>> get() = _tickets

    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun cargarTicketsDelEvento(idEvento: Long) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val respuesta = apiService.getTicketsPorEvento(idEvento)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    _tickets.value = respuesta.body()
                } else {
                    _error.value = "Error al obtener las entradas: ${respuesta.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }
}