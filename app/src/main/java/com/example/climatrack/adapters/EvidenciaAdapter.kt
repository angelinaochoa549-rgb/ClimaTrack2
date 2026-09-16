package com.example.climatrack.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.databinding.ItemEvidenciaBinding
import com.example.climatrack.models.Evidencia

class EvidenciasAdapter(
    private val listaEvidencias: MutableList<Evidencia>,
    private val onEliminarClick: (Evidencia) -> Unit
) : RecyclerView.Adapter<EvidenciasAdapter.EvidenciaViewHolder>() {

    inner class EvidenciaViewHolder(val binding: ItemEvidenciaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvidenciaViewHolder {
        val binding = ItemEvidenciaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EvidenciaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EvidenciaViewHolder, position: Int) {
        val item = listaEvidencias[position]
        with(holder.binding) {
            tvFecha.text = item.fecha
            tvTitulo.text = item.titulo

            // Carga limpia de la imagen
            try {
                val inputStream = root.context.contentResolver.openInputStream(item.imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                ivFoto.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            btnEliminar.setOnClickListener {
                onEliminarClick(item)
            }
        }
    }

    override fun getItemCount(): Int = listaEvidencias.size

    fun agregarEvidencia(evidencia: Evidencia) {
        listaEvidencias.add(evidencia)
        notifyItemInserted(listaEvidencias.size - 1)
    }

    fun eliminarEvidencia(evidencia: Evidencia) {
        val index = listaEvidencias.indexOf(evidencia)
        if (index != -1) {
            listaEvidencias.removeAt(index)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, listaEvidencias.size)
        }
    }
}