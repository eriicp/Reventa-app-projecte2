package com.example.reventa.ui.sell

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.reventa.R
import com.example.reventa.api.auth.UserPreferences
import com.example.reventa.databinding.FragmentSellTicketBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class SellTicketFragment : Fragment(R.layout.fragment_sell_ticket) {

    private var _binding: FragmentSellTicketBinding? = null
    private val binding get() = _binding!!

    // Variables de estado
    private var uriPdfSeleccionado: Uri? = null
    private var listaEventosActual: List<com.example.reventa.model.Evento> = emptyList() // <-- Ajusta a tu modelo Evento
    private var idEventoSeleccionado: Long = -1L

    // Inyección del ViewModel con su Factory
    private val viewModel: SellTicketViewModel by viewModels {
        SellTicketViewModelFactory(requireContext())
    }

    // Lanzador para abrir el explorador de archivos y buscar el PDF
    private val selectPdfLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == Activity.RESULT_OK && resultado.data != null) {
            uriPdfSeleccionado = resultado.data!!.data
            binding.tvNombreArchivo.text = obtenerNombreArchivo(uriPdfSeleccionado!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSellTicketBinding.bind(view)

        // 1. Pedimos los eventos al servidor nada más abrir la pantalla
        viewModel.cargarEventosDisponibles()

        // 2. Escuchamos la selección del Spinner (Desplegable)
        binding.spinnerEventos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (listaEventosActual.isNotEmpty()) {
                    idEventoSeleccionado = listaEventosActual[position].idEvento
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                idEventoSeleccionado = -1L
            }
        }

        // 3. Botón para seleccionar el PDF
        binding.btnSeleccionarPdf.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            selectPdfLauncher.launch(intent)
        }

        // 4. Botón final para publicar la entrada
        binding.btnPublicarVenta.setOnClickListener {
            val precioText = binding.etPrecioVenta.text.toString()
            val zonaText = binding.etZona.text.toString()
            val filaText = binding.etFila.text.toString()
            val asientoText = binding.etAsiento.text.toString()

            // Validaciones
            if (idEventoSeleccionado == -1L) {
                Toast.makeText(context, "Por favor, espera a que carguen los eventos y selecciona uno", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. Quitamos la validación del PDF
            if (precioText.isEmpty() || zonaText.isEmpty()) {
                Toast.makeText(context, "Rellena al menos el precio y la zona", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val userPreferences = UserPreferences(requireContext())
                val idUsuarioReal = userPreferences.userId.firstOrNull() ?: -1L

                if (idUsuarioReal != -1L) {
                    // 2. Solo convertimos el PDF si el usuario seleccionó uno (gracias al ?.)
                    val multipartPart = uriPdfSeleccionado?.let { convertirUriAMultipart(it) }

                    // 3. Lo enviamos tal cual (puede ser null y no pasará nada)
                    viewModel.publicarEntrada(
                        idEvento = idEventoSeleccionado,
                        idVendedor = idUsuarioReal,
                        precio = precioText.toFloat(),
                        zona = zonaText,
                        fila = filaText.ifEmpty { "-" },
                        asiento = asientoText.ifEmpty { "-" },
                        archivoPdfPart = multipartPart
                    )
                }
                else {
                    Toast.makeText(context, "Error de sesión. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 5. Activamos los observadores del ViewModel
        setupObservers()
    }

    private fun setupObservers() {
        // Observador de los Eventos para el Spinner
        viewModel.eventos.observe(viewLifecycleOwner) { eventos ->
            listaEventosActual = eventos
            val nombresEventos = eventos.map { it.nombre }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombresEventos
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerEventos.adapter = adapter
        }

        // Observador del Estado de la subida a AWS S3
        viewModel.status.observe(viewLifecycleOwner) { estado ->
            when (estado) {
                is SellStatus.Loading -> {
                    binding.btnPublicarVenta.isEnabled = false
                    binding.btnPublicarVenta.text = "Subiendo archivo..."
                }
                is SellStatus.Success -> {
                    Toast.makeText(context, "¡Entrada subida correctamente!", Toast.LENGTH_LONG).show()
                    // Volvemos a la pantalla anterior (El menú principal)
                    findNavController().popBackStack()
                }
                is SellStatus.Error -> {
                    binding.btnPublicarVenta.isEnabled = true
                    binding.btnPublicarVenta.text = "Publicar Entrada"
                    Toast.makeText(context, estado.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- FUNCIONES AUXILIARES PARA EL PDF ---

    private fun convertirUriAMultipart(uri: Uri): MultipartBody.Part? {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null
        inputStream.close()

        val requestFile = bytes.toRequestBody("application/pdf".toMediaTypeOrNull(), 0, bytes.size)
        return MultipartBody.Part.createFormData("pdf", obtenerNombreArchivo(uri), requestFile)
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        var resultado = "entrada.pdf"
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) resultado = it.getString(index)
                }
            }
        }
        return resultado
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}