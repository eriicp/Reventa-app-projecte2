package com.example.reventa.api

import UserPreferences
import android.content.Context
import com.example.reventa.api.auth.AuthInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ItemAPI {
    companion object {
        private var ItemAPI: ApiService? = null

        @Synchronized
        // AÑADIMOS EL CONTEXTO AQUÍ
        fun API(context: Context): ApiService {
            if (ItemAPI == null) {

                val gsondateformat = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                // Le pasamos el contexto a la función que crea el cliente
                val unsafeOkHttpClient = getUnsafeOkHttpClient(context)

                ItemAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsondateformat))
                    .baseUrl("http://10.0.2.2:8081/")
                    .client(unsafeOkHttpClient)
                    .build()
                    .create(ApiService::class.java)
            }
            return ItemAPI!!
        }

        private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
            try {
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                // CREAMOS LA INSTANCIA DE UserPreferences CON EL CONTEXTO
                val userPreferences = UserPreferences(context)

                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true }
                    // AHORA SÍ: Le pasamos la instancia correcta al interceptor
                    .addInterceptor(AuthInterceptor(userPreferences))
                    .build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}