package com.example.reventa.ui.tickets

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI

class TicketsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    // Añadimos la anotación de supresión correctamente escrita
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketsViewModel::class.java)) {
            // Usamos tu objeto ItemAPI para obtener la instancia del ApiService configurada
            val apiService = ItemAPI.API(context)

            // Hacemos el caspeo limpio directamente al retornar
            return TicketsViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}