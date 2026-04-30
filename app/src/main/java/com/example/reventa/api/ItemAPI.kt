package com.example.reventa.api

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
        fun API(): ApiService {
            if (ItemAPI == null) {

                val gsondateformat = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create()

                // Client HTTP insegur (només per desenvolupament)
                val unsafeOkHttpClient = getUnsafeOkHttpClient()

                ItemAPI = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsondateformat))
                    .baseUrl("http://10.0.2.2:8081/")
                    .client(unsafeOkHttpClient) // Afegeix el client
                    .build()
                    .create(ApiService::class.java)
            }
            return ItemAPI!!
        }
        private fun getUnsafeOkHttpClient(): OkHttpClient {
            try {
                // Crea un trust manager que NO valida certificats
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                )

                // Instal·la el trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier { _, _ -> true } // Accepta qualsevol hostname
                    .build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}