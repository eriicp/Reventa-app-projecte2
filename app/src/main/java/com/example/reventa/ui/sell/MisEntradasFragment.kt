package com.example.reventa.ui.sell

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reventa.R
import com.example.reventa.api.auth.UserPreferences
import com.example.reventa.databinding.FragmentMisEntradasBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MisEntradasFragment : Fragment(R.layout.fragment_mis_entradas) {

    private var _binding: FragmentMisEntradasBinding? = null
    private val binding get() = _binding!!

    // Inyectamos el ViewModel
    private val viewModel: MisEntradasViewModel by viewModels {
        MisEntradasViewModelFactory(requireContext())
    }

    // 1. ¡DESCOMENTADO! Declaramos el adaptador
    private lateinit var adapter: MisEntradasAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMisEntradasBinding.bind(view)

        // 2. ¡DESCOMENTADO! Configuramos el RecyclerView y le enchufamos el adaptador
        adapter = MisEntradasAdapter()
        binding.rvMisEntradas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMisEntradas.adapter = adapter

        // Pedimos los datos al ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            val userPreferences = UserPreferences(requireContext())
            val idUsuarioReal = userPreferences.userId.firstOrNull() ?: -1L

            if (idUsuarioReal != -1L) {
                viewModel.cargarMisEntradas(idUsuarioReal)
            } else {
                Toast.makeText(context, "Error: No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
            }
        }

        // Observamos los resultados
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.misEntradas.observe(viewLifecycleOwner) { lista ->
            if (lista.isNullOrEmpty()) {
                // Si no hay entradas, mostramos el mensaje vacío
                binding.rvMisEntradas.visibility = View.GONE
                binding.layoutEmptyState.visibility = View.VISIBLE
            } else {
                // Si hay entradas, las dibujamos en la lista
                binding.rvMisEntradas.visibility = View.VISIBLE
                binding.layoutEmptyState.visibility = View.GONE

                adapter.submitList(lista)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            Toast.makeText(context, mensajeError, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}