package com.example.reventa.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import com.example.reventa.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Instanciamos el ViewModel usando la Factory que acabamos de crear
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        // ¡ADIÓS AL ID FIJO! El ViewModel ahora sabe solito quién está logueado
        profileViewModel.cargarPerfilUsuarioLogueado()

        // Acción de cerrar sesión
        binding.cvLogout.setOnClickListener {
            profileViewModel.cerrarSesion()
        }

        binding.ibProfileSettings.setOnClickListener {
            Toast.makeText(requireContext(), "Ajustes próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // Escuchamos cuando llegan los datos del servidor
        profileViewModel.perfil.observe(viewLifecycleOwner) { usuario ->
            usuario?.let {
                binding.tvUserName.text = it.nombreCompleto
                binding.tvUserEmail.text = it.email
                binding.tvReputationScore.text = String.format("%.1f", it.reputacionMedia)
                binding.tvReviewsCount.text = it.totalResenas.toString()

                // Gestionar el texto de verificación según el ENUM del backend
                when (it.estadoVerificacion) {
                    "verificado" -> {
                        binding.tvUserVerification.text = "✓ Cuenta verificada"
                        binding.tvUserVerification.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
                    }
                    "pendiente" -> {
                        binding.tvUserVerification.text = "⏳ Verificación pendiente"
                        binding.tvUserVerification.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
                    }
                    else -> {
                        binding.tvUserVerification.text = "❌ Cuenta no verificada"
                        binding.tvUserVerification.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                    }
                }
            }
        }

        // Manejo de errores de conexión
        profileViewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_LONG).show()
        }

        // Si el logout es correcto, limpiamos la pila y mandamos al Login o Home
        profileViewModel.logoutExitoso.observe(viewLifecycleOwner) { exitoso ->
            if (exitoso) {
                Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
                // Te redirige a la pantalla de Login para destruir el estado anterior
                findNavController().navigate(R.id.navigation_profile)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}