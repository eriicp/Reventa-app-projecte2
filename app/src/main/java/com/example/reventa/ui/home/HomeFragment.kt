package com.example.reventa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import com.example.reventa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Asignamos los clics a cada CardView
        binding.cvMusica.setOnClickListener { irACategoria("Música") }
        binding.cvDeportes.setOnClickListener { irACategoria("Deportes") }
        binding.cvTeatro.setOnClickListener { irACategoria("Teatro") }
        binding.cvFestivales.setOnClickListener { irACategoria("Festivales") }
    }

    private fun irACategoria(nombreCategoria: String) {
        // 2. Metemos la categoría en la "mochila" (Bundle)
        val bundle = Bundle().apply {
            putString("CATEGORIA", nombreCategoria)
        }

        // 3. Viajamos al fragmento de destino llevando la mochila
        // OJO: Cambia 'R.id.exploreFragment' por el ID real del fragmento que vayas a usar
        findNavController().navigate(R.id.ExploreFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}