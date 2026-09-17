package com.example.climatrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.climatrack.R
import com.example.climatrack.models.Equipo

class EquipoAdapter(
    private var listaEquipos: List<Equipo>,
    private val onItemClick: (Equipo) -> Unit
) : RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder>() {

    class EquipoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgEquipo: ImageView = itemView.findViewById(R.id.imgEquipo)
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigoEquipo)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoBadge)
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        val tvMarca: TextView = itemView.findViewById(R.id.tvMarca)
        val tvModelo: TextView = itemView.findViewById(R.id.tvModelo)
        val tvSerie: TextView = itemView.findViewById(R.id.tvSerie)
        val tvCliente: TextView = itemView.findViewById(R.id.tvCliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equipo, parent, false)
        return EquipoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        val equipo = listaEquipos[position]
        val context = holder.itemView.context

        holder.tvCodigo.text = equipo.codigo
        holder.tvTipo.text = context.getString(R.string.label_tipo, equipo.tipo)
        holder.tvMarca.text = context.getString(R.string.label_marca, equipo.marca)
        holder.tvModelo.text = context.getString(R.string.label_modelo, equipo.modelo)
        holder.tvSerie.text = context.getString(R.string.label_serie, equipo.serie)
        holder.tvCliente.text = context.getString(R.string.label_cliente, equipo.cliente)
        holder.tvEstado.text = equipo.estado

        // Color según estado del equipo
        val colorEstado = when (equipo.estado.uppercase()) {
            "OPERATIVO" -> ContextCompat.getColor(context, R.color.status_operativo)
            "EN MANTENIMIENTO" -> ContextCompat.getColor(context, R.color.status_en_proceso)
            "FUERA DE SERVICIO" -> ContextCompat.getColor(context, R.color.status_cancelada)
            else -> ContextCompat.getColor(context, R.color.status_pendiente)
        }
        holder.tvEstado.setTextColor(colorEstado)

        // Carga de la imagen (Prioridad: Ruta guardada > Icono por tipo)
        val placeholderIcon = obtenerIconoPorTipo(equipo.tipo)
        if (!equipo.rutaImagen.isNullOrEmpty()) {
            Glide.with(context)
                .load(equipo.rutaImagen)
                .placeholder(placeholderIcon)
                .error(placeholderIcon)
                .into(holder.imgEquipo)
        } else {
            holder.imgEquipo.setImageResource(placeholderIcon)
        }

        holder.itemView.setOnClickListener { onItemClick(equipo) }
    }

    override fun getItemCount(): Int = listaEquipos.size

    fun actualizarLista(nuevaLista: List<Equipo>) {
        listaEquipos = nuevaLista
        notifyDataSetChanged()
    }

    private var listaOriginal: List<Equipo> = ArrayList(listaEquipos)

    fun filtrar(texto: String) {
        if (listaOriginal.isEmpty() && listaEquipos.isNotEmpty()) {
            listaOriginal = ArrayList(listaEquipos)
        }
        
        val listaFiltrada = if (texto.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.codigo.lowercase().contains(texto.lowercase()) ||
                        it.cliente.lowercase().contains(texto.lowercase()) ||
                        it.tipo.lowercase().contains(texto.lowercase())
            }
        }
        listaEquipos = listaFiltrada
        notifyDataSetChanged()
    }

    private fun obtenerIconoPorTipo(tipo: String): Int {
        return when (tipo.lowercase().trim()) {
            "split pared" -> R.drawable.img_split
            "mini split" -> R.drawable.img_minisplit
            "cassette" -> R.drawable.img_cassette
            "chiller" -> R.drawable.img_chiller
            else -> R.drawable.ic_snowflake // Icono por defecto
        }
    }
}