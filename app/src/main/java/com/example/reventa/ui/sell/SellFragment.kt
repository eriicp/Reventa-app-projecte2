package com.example.reventa.ui.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reventa.databinding.FragmentSellBinding

class SellFragment : Fragment() {

    private var _binding: FragmentSellBinding? = null
    // Esta propiedad solo es válida entre onCreateView y onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflamos el XML que me acabas de pasar (asegúrate de que se llame fragment_sell.xml)
        _binding = FragmentSellBinding.inflate(inflater, container, false)

        // Aquí luego le daremos vida a los botones, por ejemplo:
        // binding.cvTickets.setOnClickListener { ... }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}