package com.example.reventa.api

import retrofit2.Response
import retrofit2.http.*
import com.example.reventa.model.Evento
import com.example.reventa.model.LoginRequest
import com.example.reventa.model.LoginResponse
import com.example.reventa.model.PaymentRequest
import com.example.reventa.model.PaymentResponse
import com.example.reventa.model.UsuarioDto

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
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/eventos/proximos")
    suspend fun getProximosEventos(): Response<List<Evento>>

    @POST("api/pagos/comprar")
    suspend fun iniciarCompra(@Body request: PaymentRequest): Response<PaymentResponse>

    @GET("api/usuarios/{id}")
    suspend fun getPerfilUsuario(@Path("id") idUsuario: Long): Response<UsuarioDto>
}