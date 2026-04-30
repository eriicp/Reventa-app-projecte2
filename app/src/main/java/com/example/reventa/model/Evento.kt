package com.example.reventa.model

data class Evento (
    val idEvento: Long,
    val nombre: String,
    val fechaEvento: String,
    val ubicacion: String,
    val categoria: CategoriaEvento
)