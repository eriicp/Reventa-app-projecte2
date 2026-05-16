package com.example.reventa.model

data class UsuarioDto(
    val idUsuario: Long,
    val nombreCompleto: String,
    val email: String,
    val estadoVerificacion: String, // 'pendiente', 'verificado', 'rechazado'
    val reputacionMedia: Double,
    val totalResenas: Int
)