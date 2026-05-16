package com.example.reventa

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.reventa.api.auth.UserPreferences
import com.example.reventa.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stripe.android.PaymentConfiguration
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Stripe con tu Clave Pública (¡NO LA SECRETA!)
        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51TOg6YH3agIzzdNNyr5uqUzP1ucPdULMNo1jkVNcc8cTzxL44oDjCKbUOOFtFrhLM7lbDqAXd24Mnn6MjqRJRmIe00thB5OM9j"
        )

        val userPreferences = UserPreferences(this)
        lifecycleScope.launch {
            userPreferences.userToken.collect { token ->
                isUserLoggedIn = !token.isNullOrEmpty() && token != "null"
            }
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // LAS REGLAS DE ORO (Evitan el efecto cebolla y duplicar pantallas)
        val opcionesNavegacion = NavOptions.Builder()
            .setLaunchSingleTop(true) // Si ya estoy en la pantalla, NO abras otra
            .setPopUpTo(R.id.navigation_home, false) // El Home siempre es la base
            .build()

        // 1. GESTIONAMOS LOS CLICS DEL MENÚ MANUALMENTE (Adiós al piloto automático)
        // 1. GESTIONAMOS LOS CLICS DEL MENÚ MANUALMENTE (Adiós al piloto automático)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home, null, opcionesNavegacion)
                    true
                }
                R.id.navigation_explore -> {
                    navController.navigate(R.id.navigation_explore, null, opcionesNavegacion)
                    true
                }
                R.id.navigation_sell -> {
                    if (!isUserLoggedIn) {
                        navController.navigate(R.id.loginFragment)
                        false // Devolvemos false para que el icono NO se quede iluminado
                    } else {
                        navController.navigate(R.id.navigation_sell, null, opcionesNavegacion)
                        true
                    }
                }
                R.id.navigation_profile -> {
                    if (!isUserLoggedIn) {
                        // Si no hay sesión, al login
                        navController.navigate(R.id.loginFragment)
                        false
                    } else {
                        // Si hay sesión, entramos al perfil
                        navController.navigate(R.id.navigation_profile, null, opcionesNavegacion)
                        true
                    }
                }
                else -> false
            }
        }

        // 2. EL VIGILANTE: Se asegura de ocultar el menú en el login y de iluminar el icono correcto
        navController.addOnDestinationChangedListener { _, destination, _ ->

            // Mostrar/Ocultar el menú inferior
            if (destination.id == R.id.loginFragment) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }

            // MAGIA: Si viajamos a Explore desde una tarjeta del Home, esto hace que
            // el icono de Explore de abajo se ilumine automáticamente.
            when (destination.id) {
                R.id.navigation_home -> navView.menu.findItem(R.id.navigation_home).isChecked = true
                R.id.navigation_explore -> navView.menu.findItem(R.id.navigation_explore).isChecked = true
                R.id.navigation_sell -> navView.menu.findItem(R.id.navigation_sell).isChecked = true
                R.id.navigation_profile -> navView.menu.findItem(R.id.navigation_profile).isChecked = true
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                // Pantallas donde NO queremos menú inferior
                R.id.paymentFragment, R.id.loginFragment -> {
                    binding.navView.visibility = View.GONE
                }
                // En el resto de pantallas (Home, Explore, Sell, Profile, Tickets) SÍ lo mostramos
                else -> {
                    binding.navView.visibility = View.VISIBLE
                }
            }
        }
    }
}