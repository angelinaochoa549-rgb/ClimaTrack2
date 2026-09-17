package com.example.climatrack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.models.Repuesto
import java.text.NumberFormat
import java.util.Locale

class RepuestoAdapter(
    private var repuestos: List<Repuesto>,
    private val onOpcionesClick: ((Repuesto, View) -> Unit)? = null
) : RecyclerView.Adapter<RepuestoAdapter.RepuestoViewHolder>() {

    class RepuestoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRepuesto: ImageView = view.findViewById(R.id.imgRepuesto)
        val tvCodigo: TextView = view.findViewById(R.id.tvCodigoRepuesto)
        val tvDescripcion: TextView = view.findViewById(R.id.tvNombreRepuesto)
        val tvUnidad: TextView = view.findViewById(R.id.tvUnidad)
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidadRepuesto)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioItem)
        val btnOpciones: ImageView = view.findViewById(R.id.btnMenuOpciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepuestoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repuesto, parent, false)
        return RepuestoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepuestoViewHolder, position: Int) {
        val repuesto = repuestos[position]
        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }

        holder.tvCodigo.text = repuesto.codigo
        holder.tvDescripcion.text = repuesto.nombre
        holder.tvUnidad.text = repuesto.unidad
        holder.tvCantidad.text = holder.itemView.context.getString(R.string.label_cantidad, repuesto.cantidad)
        holder.tvPrecio.text = formatoMoneda.format(repuesto.total)

        // Asignación de icono según el código del repuesto
        holder.imgRepuesto.setImageResource(obtenerIconoRepuesto(repuesto.codigo))

        holder.btnOpciones.setOnClickListener { view ->
            onOpcionesClick?.invoke(repuesto, view)
        }
    }

    override fun getItemCount() = repuestos.size

    fun updateList(newList: List<Repuesto>) {
        repuestos = newList
        notifyDataSetChanged()
    }

    private fun obtenerIconoRepuesto(codigo: String): Int {
        return when (codigo.uppercase().trim()) {
            "RPT-001", "RPT-0007" -> R.drawable.ic_filter
            "RPT-002", "RPT-0012" -> R.drawable.ic_capacitor
            "RPT-0021" -> R.drawable.ic_contactor
            "RPT-0030" -> R.drawable.ic_gas
            else -> R.drawable.ic_gear
        }
    }
}