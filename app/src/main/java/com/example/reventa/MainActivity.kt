package com.example.reventa

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.reventa.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // 1. Esto es para que los iconos se iluminen correctamente
        navView.setupWithNavController(navController)

        // 2. NUEVO GESTOR DE CLICS (Más robusto)
        navView.setOnItemSelectedListener { item ->
            val usuarioLogueado = false // Cambia a true para probar cuando ya tengas el login hecho

            when (item.itemId) {
                R.id.navigation_home -> {
                    // Navegamos al Home limpiando el historial para que no se "pille"
                    navController.navigate(R.id.navigation_home, null,
                        NavOptions.Builder().setPopUpTo(R.id.mobile_navigation, true).build())
                    true
                }
                R.id.navigation_explore -> {
                    navController.navigate(R.id.navigation_explore, null,
                        NavOptions.Builder().setLaunchSingleTop(true).build())
                    true
                }
                R.id.navigation_sell -> {
                    if (!usuarioLogueado) {
                        // Si no está logueado, vamos al Login
                        navController.navigate(R.id.loginFragment)
                        // IMPORTANTE: Devolvemos FALSE para que el icono de "Sell"
                        // NO se quede marcado en azul, ya que realmente estamos en Login.
                        false
                    } else {
                        // Si está logueado, vamos a Sell normal
                        navController.navigate(R.id.navigation_sell)
                        true
                    }
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.loginFragment) {
                navView.visibility = View.GONE // Esconde el menú en el Login
            } else {
                navView.visibility = View.VISIBLE // Lo muestra en el resto
            }
        }
    }
}