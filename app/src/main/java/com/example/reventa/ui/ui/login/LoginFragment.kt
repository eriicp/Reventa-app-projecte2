package com.example.reventa.ui.login

import LoginViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    // Conectamos el ViewModel al Fragmento
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        // 1. ESCUCHAR LOS CAMBIOS DEL VIEWMODEL
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect { estado ->
                when (estado) {
                    is LoginState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                    }
                    is LoginState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnLogin.isEnabled = false
                    }
                    is LoginState.Success -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "¡Bienvenido!", Toast.LENGTH_SHORT).show()

                        // Reiniciamos el estado por si volvemos a esta pantalla en el futuro
                        viewModel.resetState()

                        // Navegamos al Home
                        findNavController().navigate(R.id.navigation_home)
                    }
                    is LoginState.Error -> {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                        Toast.makeText(requireContext(), estado.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                }
            }
        }

        // 2. DETECTAR EL CLIC Y AVISAR AL VIEWMODEL
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Faltan datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ya no hacemos lógica aquí, le pasamos la pelota al cerebro
            viewModel.iniciarSesion(email, password)
        }
    }
}