import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Esto soluciona el "unresolved reference" de dataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        // 2. Esto soluciona el "unresolved reference" de stringPreferencesKey
        private val USER_TOKEN = stringPreferencesKey("user_token")
    }

    // Guardar el token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    // Leer el token
    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN]
        }
}