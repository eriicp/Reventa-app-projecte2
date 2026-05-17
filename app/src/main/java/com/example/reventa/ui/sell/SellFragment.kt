package com.example.reventa.ui.sell

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import com.example.reventa.databinding.FragmentSellBinding // ¡Ajusta este nombre según el nombre real de tu XML!

class SellFragment : Fragment(R.layout.fragment_sell) {

    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSellBinding.bind(view)

        // 1. ACCIÓN PARA EL BOTÓN "VENDER"
        binding.cvSell.setOnClickListener {
            findNavController().navigate(R.id.sellTicketFragment)
        }

        // 2. ACCIÓN PARA "MIS ENTRADAS"
        binding.cvTickets.setOnClickListener {
            findNavController().navigate(R.id.misEntradasFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Evitamos fugas de memoria limpiando el binding
        _binding = null
    }
}