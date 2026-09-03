package com.example.climatrack.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.HistorialAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Equipo
import com.example.climatrack.models.Mantenimiento

class DetalleEquipoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var equipoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_equipo)

        dbHelper = DatabaseHelper(this)
        equipoId = intent.getIntExtra("EQUIPO_ID", -1)

        if (equipoId == -1) {
            Toast.makeText(this, "Error: Equipo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        cargarDatos()
    }

    private fun cargarDatos() {
        val equipo = dbHelper.getEquipoPorId(equipoId)
        if (equipo != null) {
            findViewById<TextView>(R.id.tvCodigoDetalle).text = equipo.codigo
            findViewById<TextView>(R.id.tvEstadoDetalle).text = equipo.estado
            findViewById<TextView>(R.id.tvTipoDetalle).text = "Tipo: ${equipo.tipo}"
            findViewById<TextView>(R.id.tvMarcaDetalle).text = "Marca: ${equipo.marca}"
            findViewById<TextView>(R.id.tvModeloDetalle).text = "Modelo: ${equipo.modelo}"
            findViewById<TextView>(R.id.tvSerieDetalle).text = "Serial: ${equipo.serie}"
            findViewById<TextView>(R.id.tvClienteDetalle).text = "Cliente: ${equipo.cliente}"

            val tvEstado = findViewById<TextView>(R.id.tvEstadoDetalle)
            val colorRes = when (equipo.estado.uppercase()) {
                "OPERATIVO" -> R.color.status_operativo
                "EN MANTENIMIENTO" -> R.color.status_en_proceso
                "FUERA DE SERVICIO" -> R.color.status_cancelada
                else -> R.color.status_pendiente
            }
            tvEstado.setTextColor(ContextCompat.getColor(this, colorRes))

            cargarHistorial(equipo.codigo)
        }
    }

    private fun cargarHistorial(codigoEquipo: String) {
        // En un escenario real, buscaríamos mantenimientos asociados a este equipo específicamente
        // Por ahora cargamos el historial general filtrado por el modelo o lógica similar si existiera
        val historial = dbHelper.getHistorialMantenimientos() 
        // Nota: Idealmente getHistorialMantenimientos debería aceptar un equipoId para filtrar
        
        val rv = findViewById<RecyclerView>(R.id.rvHistorialEquipo)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = HistorialAdapter(historial)
    }
}