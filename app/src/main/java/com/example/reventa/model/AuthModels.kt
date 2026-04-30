package com.example.reventa.model

// Lo que enviamos a la API
data class LoginRequest(
    val username: String, // Ojo: en tu backend, este campo recibe el email
    val password: String
)

// Lo que recibimos de la API
data class JwtResponse(
    val token: String
)

