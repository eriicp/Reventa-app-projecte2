package com.example.reventa.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.model.Evento
import com.example.reventa.R

// 1. Añadimos el onClick al constructor (y borramos el import falso de arriba)
class ExploreAdapter(
    private val onClick: (Evento) -> Unit
) : RecyclerView.Adapter<ExploreViewHolder>() {

    private var eventos = listOf<Evento>()

    fun submitList(newList: List<Evento>) {
        eventos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.explore_events_recycler, parent, false)
        return ExploreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val evento = eventos[position]
        holder.bind(evento)

        // 2. Le decimos a toda la fila (tarjeta) que escuche el clic
        holder.itemView.setOnClickListener {
            onClick(evento) // Ejecutamos la acción pasándole el evento
        }
    }

    override fun getItemCount() = eventos.size
}