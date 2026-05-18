package com.example.reventa.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(requireContext())
    }

    private var selectedFileUri: Uri? = null
    private lateinit var tvNombreArchivoDni: TextView

    // Contrato nativo para lanzar el explorador de archivos del dispositivo móvil
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedFileUri = result.data?.data
            if (selectedFileUri != null) {
                tvNombreArchivoDni.text = "Documento cargado con éxito"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNombreArchivoDni = view.findViewById(R.id.tvNombreArchivoDni)
        val btnSeleccionarDni = view.findViewById<View>(R.id.btnSeleccionarDni)
        val btnRegistrar = view.findViewById<Button>(R.id.btnRegistrar)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarReg)

        val etNombre = view.findViewById<EditText>(R.id.etRegNombre)
        val etDni = view.findViewById<EditText>(R.id.etRegDni)
        val etEmail = view.findViewById<EditText>(R.id.etRegEmail)
        val etPassword = view.findViewById<EditText>(R.id.etRegPassword)

        // 1. SELECCIÓN DEL ARCHIVO
        btnSeleccionarDni.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/jpeg", "image/png"))
            }
            filePickerLauncher.launch(intent)
        }

        // 2. ACCIÓN DEL BOTÓN REGISTRAR (Delega en el ViewModel)
        btnRegistrar.setOnClickListener {
            viewModel.registrarUsuario(
                nombre = etNombre.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                pass = etPassword.text.toString().trim(),
                dni = etDni.text.toString().trim(),
                fileUri = selectedFileUri
            )
        }

        // 3. SUSCRIPCIÓN REACTIVA AL ESTADO DEL VIEWMODEL (UI State)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { estado ->
                when (estado) {
                    is RegisterState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnRegistrar.isEnabled = true
                    }
                    is RegisterState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnRegistrar.isEnabled = false
                    }
                    is RegisterState.Success -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Iniciando configuración de pagos...", Toast.LENGTH_LONG).show()

                        // 1. Abrimos el navegador con el link oficial de Stripe
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(estado.stripeUrl))
                        startActivity(browserIntent)

                        // 2. Limpiamos y volvemos al login
                        viewModel.resetState()
                        findNavController().popBackStack()
                    }
                    is RegisterState.Error -> {
                        progressBar.visibility = View.GONE
                        btnRegistrar.isEnabled = true
                        Toast.makeText(requireContext(), estado.message, Toast.LENGTH_LONG).show()
                        viewModel.resetState()
                    }
                }
            }
        }
    }
}