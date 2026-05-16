package com.example.reventa.model

// Lo que enviamos a la API
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val idUsuario: Long
)

