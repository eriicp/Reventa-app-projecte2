package com.example.reventa.ui.sell
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reventa.api.ApiService
import com.example.reventa.model.Evento
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
class SellTicketViewModel(private val apiService: ApiService) : ViewModel() {
    private val _status = MutableLiveData<SellStatus>()
    val status: LiveData<SellStatus> get() = _status
    private val _eventos = MutableLiveData<List<Evento>>()
    val eventos: LiveData<List<Evento>> get() = _eventos
    fun publicarEntrada(idEvento: Long, idVendedor: Long, precio: Float,
                        zona: String, fila: String, asiento: String,
                        archivoPdfPart: MultipartBody.Part?) {
        _status.value = SellStatus.Loading

        viewModelScope.launch {
            try {
                // FORMATO MODERNO KOTLIN (Usando Extension Functions)
                val idEventoBody = idEvento.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val idVendedorBody = idVendedor.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val precioBody = precio.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val zonaBody = zona.toRequestBody("text/plain".toMediaTypeOrNull())
                val filaBody = fila.toRequestBody("text/plain".toMediaTypeOrNull())
                val asientoBody = asiento.toRequestBody("text/plain".toMediaTypeOrNull())

                val respuesta = apiService.publicarEntrada(
                    idEventoBody, idVendedorBody, precioBody,
                    zonaBody, filaBody, asientoBody, archivoPdfPart
                )

                if (respuesta.isSuccessful) {
                    _status.value = SellStatus.Success
                } else {
                    _status.value = SellStatus.Error("Error: ${respuesta.code()}")
                }
            } catch (e: Exception) {
                _status.value = SellStatus.Error("Fallo: ${e.localizedMessage}")
            }
        }
    }
    fun cargarEventosDisponibles() {
        viewModelScope.launch {
            try {
                val respuesta = apiService.findEvents()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    _eventos.value = respuesta.body()
                } else {
                    _status.value = SellStatus.Error("No se pudieron cargar los eventos")
                }
            } catch (e: Exception) {
                _status.value = SellStatus.Error("Error de conexión al buscar eventos")
            }
        }
    }
}
sealed class SellStatus {
    object Loading : SellStatus()
    object Success : SellStatus()
    data class Error(val message: String) : SellStatus()
}