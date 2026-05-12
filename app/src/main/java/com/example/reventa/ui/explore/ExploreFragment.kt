package com.example.reventa.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reventa.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var exploreAdapter: ExploreAdapter

    private val exploreViewModel: ExploreViewModel by viewModels {
        ExploreViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupChips()

        exploreViewModel.eventos.observe(viewLifecycleOwner) { listaEventos ->
            exploreAdapter.submitList(listaEventos)
        }

        exploreViewModel.error.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Miramos si traemos alguna categoría
        val categoriaSeleccionada = arguments?.getString("CATEGORIA")

        // 2. ¡EL TRUCO ANTIFANTASMAS! Borramos el dato inmediatamente después de leerlo.
        // Así, si el usuario vuelve a entrar usando el menú de abajo, la mochila estará vacía.
        arguments?.remove("CATEGORIA")

        // 3. Lógica visual
        when (categoriaSeleccionada) {
            "Música" -> binding.chipMusic.isChecked = true
            "Deportes" -> binding.chipSports.isChecked = true
            "Festivales" -> binding.chipFestival.isChecked = true
            null -> binding.chipAll.isChecked = true
        }

        // 4. Llamada a la API
        exploreViewModel.cargarEventosIniciales(categoriaSeleccionada)
    }

    private fun setupChips() {
        binding.chipAll.setOnClickListener { exploreViewModel.fetchEvents() }
        binding.chipMusic.setOnClickListener { exploreViewModel.fetchEventsByCategory("concierto") }
        binding.chipSports.setOnClickListener { exploreViewModel.fetchEventsByCategory("deporte") }
        binding.chipFestival.setOnClickListener { exploreViewModel.fetchEventsByCategory("festival") }
    }

    private fun setupRecyclerView() {
        exploreAdapter = ExploreAdapter()
        binding.rvExplore.apply {
            adapter = exploreAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}