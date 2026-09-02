package com.example.climatrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.models.Repuesto

class RepuestoAdapter(
    private var repuestos: List<Repuesto>
) : RecyclerView.Adapter<RepuestoAdapter.RepuestoViewHolder>() {

    class RepuestoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreRepuesto)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigoRepuesto)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidad)
        val tvUnidad: TextView = view.findViewById(R.id.tvUnidad)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepuestoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repuesto, parent, false)
        return RepuestoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepuestoViewHolder, position: Int) {
        val repuesto = repuestos[position]
        holder.tvNombre.text = repuesto.nombre
        holder.tvCodigo.text = repuesto.codigo
        holder.tvCantidad.text = "x${repuesto.cantidad}"
        holder.tvUnidad.text = repuesto.unidad
    }

    override fun getItemCount() = repuestos.size

    fun updateList(newList: List<Repuesto>) {
        repuestos = newList
        notifyDataSetChanged()
    }
}