package com.example.reventa.model

data class Ticket(
    val idEntrada: Long,
    val precioReventa: Double,
    val estado: String,
    val esDestacada: Boolean,
    val tipoAsiento: String,
    val fila: String,
    val asiento: String
)