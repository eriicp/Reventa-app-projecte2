package com.example.reventa.ui.tickets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reventa.R
import com.example.reventa.model.Ticket

class TicketsAdapter(
    private val onBuyClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketsAdapter.TicketViewHolder>() {

    private var tickets = listOf<Ticket>()

    fun submitList(newList: List<Ticket>) {
        tickets = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tickets_recycler_item, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)

        holder.itemView.findViewById<View>(R.id.btnBuyTicket).setOnClickListener {
            onBuyClick(ticket)
        }
    }

    override fun getItemCount() = tickets.size

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSection: TextView = itemView.findViewById(R.id.tvTicketSection)
        private val tvRow: TextView = itemView.findViewById(R.id.tvTicketRow)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvTicketPrice)

        fun bind(ticket: Ticket) {
            // Sincronizado exactamente con los campos de tu JSON:
            tvSection.text = "Zona: ${ticket.tipoAsiento}"
            tvRow.text = "Fila: ${ticket.fila} - Asiento: ${ticket.asiento}"
            tvPrice.text = String.format("%.2f€", ticket.precioReventa)
        }
    }
}