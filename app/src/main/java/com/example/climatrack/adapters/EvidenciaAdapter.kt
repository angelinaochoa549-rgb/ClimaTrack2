package com.example.climatrack.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.models.Evidencia
import java.io.File

class EvidenciaAdapter(
    private var evidencias: List<Evidencia>,
    private val onDeleteClick: (Evidencia) -> Unit
) : RecyclerView.Adapter<EvidenciaAdapter.EvidenciaViewHolder>() {

    class EvidenciaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivFoto: ImageView = view.findViewById(R.id.ivFoto)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenciaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evidencia, parent, false)
        return EvidenciaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvidenciaViewHolder, position: Int) {
        val evidencia = evidencias[position]
        
        if (evidencia.rutaFoto.startsWith("content://") || evidencia.rutaFoto.startsWith("file://")) {
            holder.ivFoto.setImageURI(Uri.parse(evidencia.rutaFoto))
        } else {
            val file = File(evidencia.rutaFoto)
            if (file.exists()) {
                holder.ivFoto.setImageURI(Uri.fromFile(file))
            } else {
                holder.ivFoto.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        }
        
        holder.tvFecha.text = evidencia.fecha
        holder.btnEliminar.setOnClickListener { onDeleteClick(evidencia) }
    }

    override fun getItemCount() = evidencias.size

    fun updateList(newList: List<Evidencia>) {
        evidencias = newList
        notifyDataSetChanged()
    }
}