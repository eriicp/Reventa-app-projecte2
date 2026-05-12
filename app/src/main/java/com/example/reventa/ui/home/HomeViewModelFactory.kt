import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reventa.api.ItemAPI
import com.example.reventa.ui.home.HomeViewModel

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Sustituye RetrofitClient por la clase que uses tú para instanciar la API
            val apiService = ItemAPI.API(context)
            return HomeViewModel(apiService) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}