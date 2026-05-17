package com.example.reventa.api

import com.example.reventa.model.Entrada
import retrofit2.Response
import retrofit2.http.*
import com.example.reventa.model.Evento
import com.example.reventa.model.LoginRequest
import com.example.reventa.model.LoginResponse
import com.example.reventa.model.PaymentRequest
import com.example.reventa.model.PaymentResponse
import com.example.reventa.model.Ticket
import com.example.reventa.model.UsuarioDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    @GET("api/entradas/evento/{idEvento}")
    suspend fun getTicketsPorEvento(
        @Path("idEvento") idEvento: Long
    ): Response<List<Ticket>>

    @Multipart
    @POST("api/entradas/vender")
    suspend fun publicarEntrada(
        @Part("idEvento") idEvento: RequestBody,
        @Part("idVendedor") idVendedor: RequestBody,
        @Part("precio") precio: RequestBody,
        @Part("zona") zona: RequestBody,
        @Part("fila") fila: RequestBody,
        @Part("asiento") asiento: RequestBody,
        @Part pdf: MultipartBody.Part?
    ): Response<Void>

    @GET("api/entradas/mis-ventas/{idUsuario}")
    suspend fun obtenerMisVentas(@Path("idUsuario") idUsuario: Long): Response<List<Entrada>>
}