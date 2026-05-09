package com.example.reventa.ui.explore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI

class ExploreViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            // Aquí le pasamos el contexto a tu ItemAPI
            val apiService = ItemAPI.API(context)
            return ExploreViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}