package com.example.reventa.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.PaymentRequest
import kotlinx.coroutines.launch

class PaymentViewModel(private val apiService: ApiService) : ViewModel() {

    // Variable para enviar el secreto de Stripe al Fragmento
    private val _clientSecret = MutableLiveData<String>()
    val clientSecret: LiveData<String> get() = _clientSecret

    // Variable para enviar errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Variable para saber si está cargando (y bloquear el botón)
    private val _cargando = MutableLiveData<Boolean>()
    val cargando: LiveData<Boolean> get() = _cargando

    fun solicitarIntentoDePago(idEntrada: Long, idComprador: Long) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val peticion = PaymentRequest(idEntrada, idComprador)
                val respuesta = apiService.iniciarCompra(peticion)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    _clientSecret.value = respuesta.body()!!.clientSecret
                } else {
                    _error.value = "Error del servidor: ${respuesta.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Fallo de conexión: ${e.localizedMessage}"
            } finally {
                _cargando.value = false
            }
        }
    }
}