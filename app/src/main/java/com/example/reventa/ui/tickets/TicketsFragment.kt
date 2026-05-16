package com.example.reventa.ui.tickets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController // <-- IMPORTANTE
import androidx.recyclerview.widget.LinearLayoutManager // <-- IMPORTANTE
import com.example.reventa.R // <-- IMPORTANTE
import com.example.reventa.databinding.FragmentTicketsBinding // <-- IMPORTANTE

class TicketsFragment : Fragment() {

    // 1. Configuración del ViewBinding igual que en tus otros fragmentos
    private var _binding: FragmentTicketsBinding? = null
    private val binding get() = _binding!!

    private lateinit var ticketsAdapter: TicketsAdapter

    // Instanciamos el ViewModel usando la Factory
    private val ticketsViewModel: TicketsViewModel by viewModels {
        TicketsViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. ¡LLAMADA VITAL! Si no llamamos a la función, la lista nunca se configura
        setupRecyclerView()
        setupObservers()

        // 3. Recuperamos el ID del evento que enviamos desde ExploreFragment
        val idEvento = arguments?.getLong("idEvento") ?: -1L
        val nombreEvento = arguments?.getString("nombreEvento") ?: "Entradas"

        // Ponemos el nombre del evento dinámicamente en el título que creamos en el XML
        binding.tvEventTitle.text = "Entradas: $nombreEvento"

        // 4. Mandamos a cargar los datos de Spring Boot si el ID es válido
        if (idEvento != -1L) {
            ticketsViewModel.cargarTicketsDelEvento(idEvento)
        } else {
            Toast.makeText(requireContext(), "Error: No se pudo identificar el evento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        // Escuchamos cuando el ViewModel recibe la lista de entradas de Spring Boot
        ticketsViewModel.tickets.observe(viewLifecycleOwner) { listaTickets ->
            // Se las pasamos al adaptador para que se pinten en la pantalla
            ticketsAdapter.submitList(listaTickets)
        }

        ticketsViewModel.error.observe(viewLifecycleOwner) { mensajeError ->
            Toast.makeText(requireContext(), mensajeError, Toast.LENGTH_SHORT).show()
        }

        ticketsViewModel.cargando.observe(viewLifecycleOwner) { estaCargando ->
            // Aquí podrás mostrar un ProgressBar si decides añadirlo más adelante
        }
    }

    private fun setupRecyclerView() {
// Dentro de tu TicketsFragment.kt en setupRecyclerView()
        val nombreDelEvento = arguments?.getString("nombreEvento") ?: "Entrada"

        ticketsAdapter = TicketsAdapter { ticketClicado ->
            val paquetePago = Bundle().apply {
                // Los datos obligatorios para Stripe que ya tenías
                putLong("idTicket", ticketClicado.idEntrada)
                putFloat("precioTicket", ticketClicado.precioReventa.toFloat())

                // ¡NUEVO! Textos para mostrar en la pantalla de pago
                putString("nombreEvento", nombreDelEvento)
                putString("zonaTicket", ticketClicado.tipoAsiento)
                putString("filaTicket", ticketClicado.fila)
                putString("asientoTicket", ticketClicado.asiento)
            }

            findNavController().navigate(R.id.paymentFragment, paquetePago)
        }

        // Ahora binding.rvTickets funcionará a la perfección
        binding.rvTickets.apply {
            adapter = ticketsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evitamos fugas de memoria limpiando el binding
    }
}