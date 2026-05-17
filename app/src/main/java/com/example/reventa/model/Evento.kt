package com.example.reventa.model

// El objeto Evento anidado
data class Evento(
    val idEvento: Long,
    val nombre: String,
    val categoria: String,
    val fechaEvento: String,
    val ubicacion: String
)


data class Vendedor(
    val idUsuario: Long,
    val nombreCompleto: String,
    val email: String,
    val estadoVerificacion: String
)


data class Entrada(
    val idEntrada: Long,
    val precioReventa: Float,
    val asiento: String,
    val fila: String,
    val tipoAsiento: String?,
    val estado: String, // Viene como "vendida" o "disponible"
    val codDigitalUnico: String?,
    val esDestacada: Boolean,
    val evento: Evento,     // <-- ¡RETROFIT MAPEA ESTO AUTOMÁTICAMENTE!
    val vendedor: Vendedor  // <-- Y ESTO TAMBIÉN
)