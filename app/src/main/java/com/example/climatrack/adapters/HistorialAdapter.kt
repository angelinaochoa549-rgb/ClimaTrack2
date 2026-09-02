package com.example.climatrack.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.databinding.ItemMantenimientoBinding
import com.example.climatrack.models.Mantenimiento

class HistorialAdapter(
    private var lista: List<Mantenimiento>
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMantenimientoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMantenimientoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvFecha.text = item.fecha
            tvHora.text = item.hora
            tvOrden.text = "Orden: ${item.orden}"
            tvTecnico.text = "Técnico: ${item.tecnico}"
            tvDescripcion.text = item.descripcion
            tvTipoBadge.text = item.tipo

            if (position == lista.size - 1) {
                viewLine.visibility = View.GONE
            } else {
                viewLine.visibility = View.VISIBLE
            }

            when (item.tipo) {
                "PREVENTIVO" -> {
                    ivIconoTipo.setImageResource(android.R.drawable.checkbox_on_background)
                    tvTipoBadge.setBackgroundResource(R.drawable.bg_status_green)
                    tvTipoBadge.setTextColor(Color.parseColor("#2E7D32"))
                }
                "CORRECTIVO" -> {
                    ivIconoTipo.setImageResource(android.R.drawable.ic_menu_manage)
                    tvTipoBadge.setBackgroundResource(R.drawable.bg_status_orange)
                    tvTipoBadge.setTextColor(Color.parseColor("#E65100"))
                }
                else -> {
                    ivIconoTipo.setImageResource(android.R.drawable.checkbox_on_background)
                    tvTipoBadge.setBackgroundResource(R.drawable.bg_status_chip)
                    tvTipoBadge.setTextColor(Color.parseColor("#1565C0"))
                }
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Mantenimiento>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}