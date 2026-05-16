package com.example.reventa.model

data class Ticket(
    val idEntrada: Long,           // En tu JSON es idEntrada
    val precioReventa: Double,     // En tu JSON es precioReventa
    val estado: String,            // 'disponible', 'vendido', etc.
    val esDestacada: Boolean,
    val tipoAsiento: String,       // En tu JSON es tipoAsiento (equivale a sección)
    val fila: String,              // ¡OJO! En tu JSON viene como String
    val asiento: String            // ¡OJO! En tu JSON viene como String
)