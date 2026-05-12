package com.example.reventa.ui.home

import HomeViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reventa.R
import com.example.reventa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Nuestro nuevo adaptador
    private lateinit var homeEventsAdapter: HomeEventsAdapter

    // Instanciamos el ViewModel
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observarViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Los clics de las categorías que arreglamos antes
        binding.cvMusica.setOnClickListener { irACategoria("Música") }
        binding.cvDeportes.setOnClickListener { irACategoria("Deportes") }
        binding.cvTeatro.setOnClickListener { irACategoria("Teatro") }
        binding.cvFestivales.setOnClickListener { irACategoria("Festivales") }
    }

    private fun setupRecyclerView() {
        homeEventsAdapter = HomeEventsAdapter()
        binding.rvHomeNextEvents.apply {
            adapter = homeEventsAdapter
            // En el Home lo queremos horizontal, como un carrusel
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observarViewModel() {
        homeViewModel.eventosProximos.observe(viewLifecycleOwner) { listaEventos ->
            // Le pasamos la lista de eventos (máximo de 6 meses) al adaptador
            homeEventsAdapter.submitList(listaEventos)
        }

        homeViewModel.error.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun irACategoria(categoria: String) {
        val bundle = Bundle().apply {
            putString("CATEGORIA", categoria)
        }
        val navOptions = androidx.navigation.NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.navigation_home, inclusive = false)
            .build()

        findNavController().navigate(R.id.navigation_explore, bundle, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}