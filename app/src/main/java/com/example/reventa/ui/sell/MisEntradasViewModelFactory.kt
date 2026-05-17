package com.example.reventa.ui.sell

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI

class MisEntradasViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MisEntradasViewModel::class.java)) {
            val apiService = ItemAPI.API(context)
            return MisEntradasViewModel(apiService) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}