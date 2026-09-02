package com.example.climatrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.models.Orden

class OrdenAdapter(
    private var ordenes: List<Orden>,
    private val onItemClick: (Orden) -> Unit
) : RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder>() {

    class OrdenViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumero: TextView = view.findViewById(R.id.tvNumeroOrden)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvCliente: TextView = view.findViewById(R.id.tvCliente)
        val tvEquipo: TextView = view.findViewById(R.id.tvEquipo)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoServicio)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orden_trabajo, parent, false)
        return OrdenViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdenViewHolder, position: Int) {
        val orden = ordenes[position]
        holder.tvNumero.text = orden.numero
        holder.tvEstado.text = orden.estado
        holder.tvCliente.text = "Cliente: ${orden.clienteNombre}"
        holder.tvEquipo.text = "Equipo: ${orden.equipoNombre}"
        holder.tvTipo.text = orden.tipoServicio
        holder.tvFecha.text = orden.fecha

        // Color del estado
        val colorRes = when (orden.estado) {
            "PENDIENTE" -> R.color.status_pendiente
            "EN PROCESO" -> R.color.status_en_proceso
            "FINALIZADA" -> R.color.status_finalizada
            else -> R.color.status_cancelada
        }
        holder.tvEstado.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, colorRes)

        holder.itemView.setOnClickListener { onItemClick(orden) }
    }

    override fun getItemCount() = ordenes.size

    fun updateList(newList: List<Orden>) {
        ordenes = newList
        notifyDataSetChanged()
    }
}