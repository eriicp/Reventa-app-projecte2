package com.example.reventa.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.databinding.HomeEventsRecyclerBinding
import com.example.reventa.model.Evento
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeEventsAdapter : ListAdapter<Evento, HomeEventsAdapter.EventoViewHolder>(EventoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoViewHolder {
        val binding = HomeEventsRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventoViewHolder(private val binding: HomeEventsRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(evento: Evento) {
            binding.tvNombre.text = evento.nombre
            binding.tvUbicacion.text = evento.ubicacion
            binding.tvMonthLabel.text = formatearFechaCorta(evento.fechaEvento)
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

    class EventoDiffCallback : DiffUtil.ItemCallback<Evento>() {
        override fun areItemsTheSame(oldItem: Evento, newItem: Evento) = oldItem.idEvento == newItem.idEvento
        override fun areContentsTheSame(oldItem: Evento, newItem: Evento) = oldItem == newItem
    }
}