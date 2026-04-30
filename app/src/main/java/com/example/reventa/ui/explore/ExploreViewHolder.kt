package com.example.reventa.ui.explore

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.R
import com.example.reventa.model.Evento
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
class ExploreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvTitle = view.findViewById<TextView>(R.id.tvEventTitle)
    val tvUbication = view.findViewById<TextView>(R.id.tvUbication)
    val tvDate = view.findViewById<TextView>(R.id.tvMonthLabel)

    fun bind(evento: Evento) {
        tvTitle.text = evento.nombre
        tvUbication.text = evento.ubicacion
        tvDate.text = formatearFechaCorta(evento.fechaEvento)
    }

    fun formatearFechaCorta(fechaString: String): String {
        return try {
            val fecha = LocalDateTime.parse(fechaString)
            val formatoSalida = DateTimeFormatter.ofPattern("dd MMM", Locale("es", "ES"))
            fecha.format(formatoSalida).uppercase()

        } catch (e: Exception) {
            "ERROR"
        }
    }
}