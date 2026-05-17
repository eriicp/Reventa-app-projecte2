package com.example.reventa.ui.sell

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI

class SellTicketViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellTicketViewModel::class.java)) {
            // Obtenemos tu servicio de API (¡que ya incluye el Token JWT!)
            val apiService = ItemAPI.API(context)
            return SellTicketViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}