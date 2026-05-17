package com.example.reventa.ui.payment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import com.example.reventa.api.auth.UserPreferences
import com.example.reventa.databinding.FragmentPaymentBinding
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentSheet: PaymentSheet
    private var idTicket: Long = -1L

    private val paymentViewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(requireContext(), "pk_test_51TOg6YH3agIzzdNNyr5uqUzP1ucPdULMNo1jkVNcc8cTzxL44oDjCKbUOOFtFrhLM7lbDqAXd24Mnn6MjqRJRmIe00thB5OM9j")
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Extraemos los datos del Ticket seleccionando
        idTicket = arguments?.getLong("idTicket") ?: -1L
        val precioBase = arguments?.getFloat("precioTicket") ?: 0.0f
        val nombreEvento = arguments?.getString("nombreEvento") ?: "Evento"
        val zona = arguments?.getString("zonaTicket") ?: "General"
        val fila = arguments?.getString("filaTicket") ?: "-"
        val asiento = arguments?.getString("asientoTicket") ?: "-"

        // 2. Cálculos y UI
        val comision = precioBase * 0.10f
        val precioFinal = precioBase + comision

        binding.tvPayEventName.text = nombreEvento
        binding.tvPayTicketDetails.text = "Zona: $zona | Fila: $fila | Asiento: $asiento"
        binding.tvPayBasePrice.text = String.format("%.2f€", precioBase)
        binding.tvPayCommission.text = String.format("%.2f€", comision)
        binding.tvPayTotalPrice.text = String.format("%.2f€", precioFinal)

        // 3. Configuramos los observadores del ViewModel
        setupObservers()

        // 4. Acción del botón de pagar (Llama al ViewModel con datos REALES)
        binding.btnPagar.setOnClickListener {
            // Como usamos DataStore (Flow), abrimos una corrutina para leer los datos de fondo
            viewLifecycleOwner.lifecycleScope.launch {

                // 1. Instanciamos tu clase UserPreferences
                val userPreferences = UserPreferences(requireContext())

                // 2. Leemos el ID del usuario (firstOrNull toma el valor actual del Flow y termina)
                val idUsuarioReal = userPreferences.userId.firstOrNull() ?: -1L

                // 3. Comprobamos que existan ambos IDs y llamamos a Spring Boot
                if (idUsuarioReal != -1L && idTicket != -1L) {
                    paymentViewModel.solicitarIntentoDePago(idTicket, idUsuarioReal)
                } else {
                    Toast.makeText(requireContext(), "Error: Inicia sesión para comprar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupObservers() {
        // Cuando el servidor devuelve el Secreto, abrimos Stripe
        paymentViewModel.clientSecret.observe(viewLifecycleOwner) { clientSecret ->
            if (clientSecret.isNotEmpty()) {
                paymentSheet.presentWithPaymentIntent(clientSecret)
            }
        }

        // Si hay error en el servidor, lo mostramos
        paymentViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

        // Bloqueamos/desbloqueamos el botón mientras carga para evitar doble clic
        paymentViewModel.cargando.observe(viewLifecycleOwner) { isLoading ->
            binding.btnPagar.isEnabled = !isLoading
            binding.btnPagar.text = if (isLoading) "Procesando..." else "Pagar con Stripe"
        }
    }

    // 5. El resultado nativo de Stripe (Como vimos antes, no llama a ninguna API)
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(context, "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack(R.id.navigation_explore, false)
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(context, "Pago cancelado", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(context, "Fallo en el pago: ${paymentSheetResult.error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}