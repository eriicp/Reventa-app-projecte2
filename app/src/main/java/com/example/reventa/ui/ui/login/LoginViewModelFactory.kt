import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
// Asegúrate de poner el import de tu RetrofitClient
import com.example.reventa.api.ItemAPI
import com.example.reventa.ui.login.LoginViewModel

class LoginViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Comprobamos que Android nos está pidiendo el ViewModel correcto
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Le inyectamos la API de Retrofit
            return LoginViewModel(ItemAPI.API()) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}