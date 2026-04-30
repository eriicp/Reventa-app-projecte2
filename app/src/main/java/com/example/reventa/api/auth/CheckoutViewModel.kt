package com.example.reventa.api.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class CheckoutViewModel(private val repository: AuthRepository) : ViewModel() {

    // UI observa esto para saber si mostrar el botón de "Comprar" o el de "Login"
    val loggedInStatus = repository.isLoggedIn.asLiveData()

    fun processPurchase() {
        if (loggedInStatus.value == true) {
            // Ejecutar lógica de compra enviando el Token al API
        } else {
            // Redirigir a pantalla de Login
        }
    }
}