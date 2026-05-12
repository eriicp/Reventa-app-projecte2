package com.example.reventa.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.api.ItemAPI
import com.example.reventa.model.Evento
import com.example.reventa.ui.explore.ExploreViewModel
// import com.example.reventa.api.RetrofitClient <- Importa tu cliente Retrofit aquí
import kotlinx.coroutines.launch

class HomeViewModel(private val apiService: ApiService) : ViewModel() {

    private val _eventosProximos = MutableLiveData<List<Evento>>()
    val eventosProximos: LiveData<List<Evento>> = _eventosProximos

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        cargarProximosEventos()
    }

    private fun cargarProximosEventos() {
        viewModelScope.launch {
            try {
                val response = apiService.getProximosEventos()
                if (response.isSuccessful) {
                    _eventosProximos.value = response.body() ?: emptyList()
                } else {
                    _error.postValue("Error al cargar próximos eventos")
                }
            } catch (e: Exception) {
                _error.postValue("Error de red: ${e.message}")
            }
        }
    }
}


