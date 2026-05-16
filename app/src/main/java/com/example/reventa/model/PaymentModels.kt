package com.example.reventa.model

data class PaymentRequest(
    val idEntrada: Long,
    val idComprador: Long
)

data class PaymentResponse(
    val clientSecret: String
)