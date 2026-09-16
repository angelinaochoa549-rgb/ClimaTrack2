package com.example.climatrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.databinding.ItemHistorialBinding
import com.example.climatrack.models.Mantenimiento

class HistorialAdapter(
    private var lista: List<Mantenimiento>
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistorialBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistorialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            // Asigna los valores del modelo Mantenimiento.kt
            tvDate.text = item.fecha
            tvTime.text = item.hora
            tvOrderNumber.text = "Orden: ${item.orden}"
            tvTechnicianName.text = "Técnico: ${item.tecnico}"
            tvMaintenanceBadge.text = item.tipo
            tvDescription.text = item.descripcion

            // Mantiene visible o esconde la línea separadora
            if (position == lista.size - 1) {
                viewDivider.visibility = View.GONE
            } else {
                viewDivider.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Mantenimiento>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}