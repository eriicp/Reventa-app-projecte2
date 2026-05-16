package com.example.reventa.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.reventa.databinding.FragmentPaymentBinding
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    // El componente de Stripe
    private lateinit var paymentSheet: PaymentSheet

    private val paymentViewModel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Inicializamos el PaymentSheet AQUÍ (Es obligatorio hacerlo en onCreate)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observarViewModel()

        // 1. Extraemos el precio base enviado desde el adaptador
        val precioBase = arguments?.getFloat("precioTicket") ?: 0.0f
        val nombreEvento = arguments?.getString("nombreEvento") ?: "Evento Desconocido"
        val zona = arguments?.getString("zonaTicket") ?: "General"
        val fila = arguments?.getString("filaTicket") ?: "-"
        val asiento = arguments?.getString("asientoTicket") ?: "-"

        // 2. CALCULAMOS LAS COMISIONES
        // Ejemplo: 10% de comisión de tu app + Tasa Stripe (2.9% del precio + 0.30€ fijos)
        val comisionPlataforma = precioBase * 0.10f

        // El precio real que se cobrará finalmente en la pasarela de Stripe
        val precioFinalReal = precioBase + comisionPlataforma

        // 3. PINTAMOS TODO DESGLOSADO DE INMEDIATO EN EL CARDVIEW
        binding.tvPayEventName.text = nombreEvento
        binding.tvPayTicketDetails.text = "Zona: $zona | Fila: $fila | Asiento: $asiento"

        // Asignamos cada valor a su respectivo TextView nuevo
        binding.tvPayBasePrice.text = String.format("%.2f€", precioBase)
        binding.tvPayCommission.text = String.format("%.2f€", comisionPlataforma)
        binding.tvPayTotalPrice.text = String.format("%.2f€", precioFinalReal)
        binding.btnPagar.setOnClickListener {

            // Desactivamos el botón para que no le den dos veces
            binding.btnPagar.isEnabled = false

            // Aquí le pasarías los IDs reales de tu entrada y tu usuario (ej: de SharedPreferences o Args)
            val idEntradaFalsa = 1L
            val idUsuarioFalso = 1L

            paymentViewModel.prepararPago(idEntradaFalsa, idUsuarioFalso)
        }
    }

    private fun observarViewModel() {
        // Cuando Spring Boot nos devuelve el secreto, lanzamos Stripe
        paymentViewModel.clientSecret.observe(viewLifecycleOwner) { secret ->
            binding.btnPagar.isEnabled = true
            presentarStripe(secret)
        }

        paymentViewModel.error.observe(viewLifecycleOwner) { error ->
            binding.btnPagar.isEnabled = true
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }
    }

    private fun presentarStripe(clientSecret: String) {
        // Configuración estética (puedes personalizar colores)
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = "Reventa App"
        )
        // 2. ¡Abrimos la pantalla de la tarjeta!
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration)
    }

    // 3. Aquí nos dice Stripe si el usuario ha pagado o ha cancelado
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(requireContext(), "Pago cancelado", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(requireContext(), "Error: ${paymentSheetResult.error.message}", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Completed -> {
                Toast.makeText(requireContext(), "¡Pago completado con éxito!", Toast.LENGTH_LONG).show()
                // Aquí podrías navegar a una pantalla de "Entrada Comprada"
                // findNavController().navigate(...)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}