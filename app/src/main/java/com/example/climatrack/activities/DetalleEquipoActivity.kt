package com.example.climatrack.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.HistorialAdapter
import com.example.climatrack.database.DatabaseHelper

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
            findViewById<TextView>(R.id.tvTipoDetalle).text = "Tipo: ${equipo.tipo}"
            findViewById<TextView>(R.id.tvMarcaDetalle).text = "Marca: ${equipo.marca}"
            findViewById<TextView>(R.id.tvModeloDetalle).text = "Modelo: ${equipo.modelo}"
            findViewById<TextView>(R.id.tvSerieDetalle).text = "Serial: ${equipo.serie}"
            findViewById<TextView>(R.id.tvClienteDetalle).text = "Cliente: ${equipo.cliente}"

            // --- ESTILO DE CHIP/BADGE PARA EL ESTADO DEL EQUIPO ---
            val tvEstado = findViewById<TextView>(R.id.tvEstadoDetalle)

            when (equipo.estado.uppercase()) {
                "ACTIVO", "OPERATIVO" -> {
                    tvEstado.text = "● Activo"
                    tvEstado.setBackgroundResource(R.drawable.bg_status_activo)
                    tvEstado.setTextColor(Color.parseColor("#0D652D")) // Verde oscuro
                }
                "EN USO" -> {
                    tvEstado.text = "● En uso"
                    tvEstado.setBackgroundResource(R.drawable.bg_status_en_uso)
                    tvEstado.setTextColor(Color.parseColor("#1A73E8")) // Azul oscuro
                }
                "EN MANTENIMIENTO", "FUERA DE SERVICIO" -> {
                    tvEstado.text = "● En mantenimiento"
                    tvEstado.setBackgroundResource(R.drawable.bg_status_mantenimiento)
                    tvEstado.setTextColor(Color.parseColor("#5F6368")) // Gris oscuro
                }
                else -> {
                    tvEstado.text = equipo.estado
                    tvEstado.setBackgroundResource(R.drawable.bg_status_mantenimiento)
                    tvEstado.setTextColor(Color.parseColor("#5F6368"))
                }
            }

            cargarHistorial(equipo.codigo)
        }
    }

    private fun cargarHistorial(codigoEquipo: String) {
        val historial = dbHelper.getHistorialMantenimientos()

        val rv = findViewById<RecyclerView>(R.id.rvHistorialEquipo)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = HistorialAdapter(historial)
    }
}