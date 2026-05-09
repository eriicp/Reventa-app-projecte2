package com.example.reventa

import UserPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.reventa.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isUserLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. LEEMOS EL TOKEN
        val userPreferences = UserPreferences(this)
        lifecycleScope.launch {
            userPreferences.userToken.collect { token ->
                // Por si acaso Retrofit guardó la palabra "null" por error
                isUserLoggedIn = !token.isNullOrEmpty() && token != "null"
            }
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // 2. Android gestiona los clics e iconos automáticamente (sin conflictos)
        navView.setupWithNavController(navController)

        // 3. EL VIGILANTE: Intercepta cada navegación
        navController.addOnDestinationChangedListener { _, destination, _ ->

            // Ocultar el menú de abajo si estamos en la pantalla de Login
            if (destination.id == R.id.loginFragment) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }

            // SEGURIDAD: Proteger la pestaña de Vender
            if (destination.id == R.id.navigation_sell) {
                if (!isUserLoggedIn) {
                    // Si no está logueado, lo mandamos al Login rebotado
                    navController.navigate(
                        R.id.loginFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_home, false) // Si le da "Atrás", va al Home
                            .build()
                    )
                }
            }
        }
    }
}