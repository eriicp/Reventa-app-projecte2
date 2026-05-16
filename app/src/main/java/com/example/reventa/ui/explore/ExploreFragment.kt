package com.example.reventa.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController // <-- IMPORTANTE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reventa.R // <-- IMPORTANTE
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
        return binding.root
    }

    // AQUI ES DONDE DEBE IR TODA LA LÓGICA
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Configuramos la vista
        setupRecyclerView()
        setupChips()
        setupSearchView()
        setupObservers()

        // 2. Miramos si traemos alguna categoría
        val categoriaSeleccionada = arguments?.getString("CATEGORIA")

        // 3. ¡EL TRUCO ANTIFANTASMAS! Borramos el dato
        arguments?.remove("CATEGORIA")

        // 4. Lógica visual de los chips
        when (categoriaSeleccionada) {
            "Música" -> binding.chipMusic.isChecked = true
            "Deportes" -> binding.chipSports.isChecked = true
            "Festivales" -> binding.chipFestival.isChecked = true
            null -> binding.chipAll.isChecked = true
        }

        // 5. Llamada inicial a la API
        exploreViewModel.cargarEventosIniciales(categoriaSeleccionada)
    }

    private fun setupObservers() {
        // Observador limpio (sin duplicar)
        exploreViewModel.eventos.observe(viewLifecycleOwner) { listaEventos ->
            // Si usas el adaptador tradicional, es actualizarLista.
            // Si usas ListAdapter, cámbialo de nuevo a submitList.
            exploreAdapter.submitList(listaEventos)
        }

        exploreViewModel.error.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    exploreViewModel.buscarEventosPorNombre(query)
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    exploreViewModel.buscarEventosPorNombre(newText)
                } else {
                    exploreViewModel.cargarEventosIniciales(null)
                }
                return true
            }
        })
    }

    private fun setupChips() {
        binding.chipAll.setOnClickListener { exploreViewModel.fetchEvents() }
        binding.chipMusic.setOnClickListener { exploreViewModel.fetchEventsByCategory("concierto") }
        binding.chipSports.setOnClickListener { exploreViewModel.fetchEventsByCategory("deporte") }
        binding.chipFestival.setOnClickListener { exploreViewModel.fetchEventsByCategory("festival") }
    }

    private fun setupRecyclerView() {
        exploreAdapter = ExploreAdapter { eventoClicado ->
            val paquete = Bundle().apply {
                putLong("idEvento", eventoClicado.idEvento)
                putString("nombreEvento", eventoClicado.nombre)
            }
            findNavController().navigate(R.id.ticketsFragment, paquete)
        }

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