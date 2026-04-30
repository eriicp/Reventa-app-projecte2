package com.example.reventa.api

import retrofit2.Response
import retrofit2.http.*
import com.example.reventa.model.CategoriaEvento
import com.example.reventa.model.Evento
import com.example.reventa.model.JwtResponse
import com.example.reventa.model.LoginRequest

interface ApiService {

    // LOGIN
    @GET("api/eventos")
    suspend fun findEvents(): Response<List<Evento>>

    // MATERIALS
    @GET("api/eventos/search/categoria")
    suspend fun findEventsByCategory(@Query("cat") categoria: String): Response<List<Evento>>

    @GET("api/eventos/search")
    suspend fun searchEventos(@Query("nombre") query: String): Response<List<Evento>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<JwtResponse>}
