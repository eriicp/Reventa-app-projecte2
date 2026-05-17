package com.example.reventa.api.auth

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Leemos el token
        val token = runBlocking { userPreferences.userToken.first() }

        val request = chain.request().newBuilder()

        // CHIVATO: Imprimimos en consola lo que estamos haciendo
        println("--- INTERCEPTOR REVENTA ---")
        println("URL de la petición: ${chain.request().url}")
        println("Token sacado del DataStore: $token")

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
            println("Cabecera añadida: Bearer $token")
        } else {
            println("¡CUIDADO! El token está vacío o es null. Se envía sin cabecera.")
        }
        println("---------------------------")

        return chain.proceed(request.build())
    }
}