package com.example.reventa.ui.login

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ItemAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class RegisterViewModel(private val context: Context) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }

    fun registrarUsuario(nombre: String, email: String, pass: String, dni: String, fileUri: Uri?) {
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || dni.isEmpty() || fileUri == null) {
            _registerState.value = RegisterState.Error("Faltan completar datos o subir el DNI")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _registerState.value = RegisterState.Loading
            try {
                // 1. Convertir datos primitivos a RequestBody
                val rbNombre = nombre.toRequestBody("text/plain".toMediaTypeOrNull())
                val rbEmail = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val rbPassword = pass.toRequestBody("text/plain".toMediaTypeOrNull())
                val rbDni = dni.toRequestBody("text/plain".toMediaTypeOrNull())

                // 2. Procesar el Stream del archivo y guardarlo temporalmente en la caché local
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(fileUri) ?: "application/octet-stream"
                val inputStream = contentResolver.openInputStream(fileUri)
                val tempFile = File(context.cacheDir, "temp_dni_upload")

                FileOutputStream(tempFile).use { output ->
                    inputStream?.copyTo(output)
                }

                // 3. Crear el MultipartBody.Part para el archivo
                val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val dniPart = MultipartBody.Part.createFormData("archivoDni", tempFile.name, requestFile)

                // 4. Invocación segura de la API (Sustituye por tu instancia de ApiService)
                val response = ItemAPI.API(context).registerUser(rbNombre, rbEmail, rbPassword, rbDni, dniPart)

                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    _registerState.value = RegisterState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

// Factory para inyectar de manera correcta el contexto de la aplicación al ViewModel
class RegisterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("ViewModel Class Desconocida")
    }
}