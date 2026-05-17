package com.example.reventa.ui.sell

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.R
import com.example.reventa.model.Entrada
import com.example.reventa.model.Ticket

class MisEntradasAdapter : RecyclerView.Adapter<MisEntradasAdapter.EntradaViewHolder>() {

    private var listaEntradas = emptyList<Entrada>()

    fun submitList(lista: List<Entrada>) {
        listaEntradas = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntradaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mi_entrada, parent, false)
        return EntradaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntradaViewHolder, position: Int) {
        val entrada = listaEntradas[position]
        holder.bind(entrada)
    }

    override fun getItemCount(): Int = listaEntradas.size

    inner class EntradaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvEventoNombre: TextView = itemView.findViewById(R.id.tvEventoNombre)
        private val tvZona: TextView = itemView.findViewById(R.id.tvZona)
        private val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        private val tvEstadoTexto: TextView = itemView.findViewById(R.id.tvEstadoTexto)
        private val cvEstadoBadge: CardView = itemView.findViewById(R.id.cvEstadoBadge)


        fun bind(entrada: Entrada) {
            // 1. Accedemos directamente al nombre del evento anidado
            tvEventoNombre.text = entrada.evento.nombre

            // Mostramos la zona/tipo de asiento y el precio
            tvZona.text = "Zona: ${entrada.tipoAsiento ?: "General"}"
            tvPrecio.text = "${entrada.precioReventa} €"

            // Pasamos el texto a mayúsculas solo para que quede más estético en la UI
            val estadoActual = entrada.estado.lowercase()
            tvEstadoTexto.text = estadoActual.replaceFirstChar { it.uppercase() }

            // 2. LÓGICA VISUAL DE COLORES (Comparando en minúsculas según tu JSON)
            when (estadoActual) {
                "disponible" -> {
                    // Verde para las que siguen a la venta
                    cvEstadoBadge.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
                    tvEstadoTexto.setTextColor(Color.parseColor("#2E7D32"))
                }
                "vendida" -> {
                    // Gris oscuro para las que ya se vendieron exitosamente
                    cvEstadoBadge.setCardBackgroundColor(Color.parseColor("#E0E0E0"))
                    tvEstadoTexto.setTextColor(Color.parseColor("#616161"))
                }
                else -> {
                    cvEstadoBadge.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                    tvEstadoTexto.setTextColor(Color.parseColor("#9E9E9E"))
                }
            }
        }
    }
}