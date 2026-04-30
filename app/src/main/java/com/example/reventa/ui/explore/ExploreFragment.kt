package com.example.reventa.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reventa.databinding.FragmentExploreBinding
import com.example.reventa.model.CategoriaEvento

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var exploreAdapter: ExploreAdapter
    private lateinit var exploreViewModel: ExploreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        exploreViewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupChips()

        // Observar cambios en la lista de eventos
        exploreViewModel.eventos.observe(viewLifecycleOwner) { listaEventos ->
            exploreAdapter.submitList(listaEventos)
        }

        exploreViewModel.error.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun setupChips() {
        // Chip "Todas"
        binding.chipAll.setOnClickListener {
            exploreViewModel.fetchEvents()
        }

        // Chip "Música" -> CONCIERTO o FESTIVAL (ajusta según tu lógica)
        binding.chipMusic.setOnClickListener {
            exploreViewModel.fetchEventsByCategory("concierto")
        }

        // Chip "Deportes" -> DEPORTE
        binding.chipSports.setOnClickListener {
            exploreViewModel.fetchEventsByCategory("deporte")
        }

        // Chip "Festivales" -> FESTIVAL
        binding.chipFestival.setOnClickListener {
            exploreViewModel.fetchEventsByCategory("festival")
        }
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