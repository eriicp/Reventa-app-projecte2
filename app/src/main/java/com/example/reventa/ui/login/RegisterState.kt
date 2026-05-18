package com.example.reventa.ui.login

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    data class Success(val stripeUrl: String) : RegisterState // Recibe la URL
    data class Error(val message: String) : RegisterState
}