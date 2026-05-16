package com.example.reventa.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.PaymentRequest
import kotlinx.coroutines.launch

class PaymentViewModel(private val apiService: ApiService) : ViewModel() {

    private val _clientSecret = MutableLiveData<String>()
    val clientSecret: LiveData<String> = _clientSecret

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun prepararPago(idEntrada: Long, idComprador: Long) {
        viewModelScope.launch {
            try {
                val request = PaymentRequest(idEntrada, idComprador)
                val response = apiService.iniciarCompra(request)

                if (response.isSuccessful && response.body() != null) {
                    _clientSecret.value = response.body()!!.clientSecret
                } else {
                    _error.value = "Error al conectar con el servidor"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }
}
