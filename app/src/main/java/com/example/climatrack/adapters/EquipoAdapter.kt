package com.example.climatrack.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.databinding.ItemEquipoBinding
import com.example.climatrack.models.Equipo

class EquipoAdapter(
    private var listaOriginal: List<Equipo>
) : RecyclerView.Adapter<EquipoAdapter.ViewHolder>() {

    private var listaFiltrada: List<Equipo> = listaOriginal

    class ViewHolder(val binding: ItemEquipoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEquipoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listaFiltrada[position]
        with(holder.binding) {
            tvCodigo.text = item.codigo
            tvTipo.text = "Tipo: ${item.tipo}"
            tvMarca.text = "Marca: ${item.marca}"
            tvModelo.text = "Modelo: ${item.modelo}"
            tvSerie.text = "Serie: ${item.serie}"
            tvCliente.text = "Cliente: ${item.cliente}"
            tvEstadoBadge.text = item.estado

            when (item.estado) {
                "OPERATIVO" -> {
                    tvEstadoBadge.setBackgroundResource(R.drawable.bg_status_green)
                    tvEstadoBadge.setTextColor(Color.parseColor("#2E7D32"))
                }
                "EN MANTENIMIENTO" -> {
                    tvEstadoBadge.setBackgroundResource(R.drawable.bg_status_orange)
                    tvEstadoBadge.setTextColor(Color.parseColor("#E65100"))
                }
                else -> { // FUERA DE SERVICIO
                    tvEstadoBadge.setBackgroundResource(R.drawable.bg_status_red)
                    tvEstadoBadge.setTextColor(Color.parseColor("#C62828"))
                }
            }
        }
    }

    override fun getItemCount(): Int = listaFiltrada.size

    fun filtrar(texto: String) {
        listaFiltrada = if (texto.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.codigo.contains(texto, ignoreCase = true) ||
                        it.tipo.contains(texto, ignoreCase = true) ||
                        it.serie.contains(texto, ignoreCase = true) ||
                        it.cliente.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}