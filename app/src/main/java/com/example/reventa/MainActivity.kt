package com.example.reventa

import UserPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
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
                        false // Devolvemos false para que el icono de Vender NO se quede iluminado
                    } else {
                        navController.navigate(R.id.navigation_sell, null, opcionesNavegacion)
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
            }
        }
    }
}