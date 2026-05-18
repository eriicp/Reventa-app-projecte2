package com.example.reventa.ui.login

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}