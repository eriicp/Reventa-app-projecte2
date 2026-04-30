package com.example.reventa.ui.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.model.Evento
import com.example.reventa.R

class ExploreAdapter : RecyclerView.Adapter<ExploreViewHolder>() {

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
        holder.bind(eventos[position])
    }

    override fun getItemCount() = eventos.size
}
