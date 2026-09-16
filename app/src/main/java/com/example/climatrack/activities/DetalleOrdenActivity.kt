package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper

class DetalleOrdenActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var ordenId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_orden)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            Toast.makeText(this, "Error: Orden no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Botón regresar en la barra personalizada
        findViewById<ImageView>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        // Accionar botón "Iniciar Mantenimiento"
        findViewById<View>(R.id.layoutBtnIniciar)?.setOnClickListener {
            val intent = Intent(this, MantenimientoActivity::class.java)
            intent.putExtra("ORDEN_ID", ordenId)
            startActivity(intent)
        }

        // Accionar botón "Ver Hoja de Vida"
        findViewById<View>(R.id.btnVerEquipo)?.setOnClickListener {
            val db = dbHelper.readableDatabase
            db.rawQuery("SELECT equipo_id FROM ordenes WHERE id = ?", arrayOf(ordenId.toString())).use { cursor ->
                if (cursor.moveToFirst()) {
                    val eqId = cursor.getInt(0)
                    val intent = Intent(this, DetalleEquipoActivity::class.java)
                    intent.putExtra("EQUIPO_ID", eqId)
                    startActivity(intent)
                }
            }
        }

        cargarDetalle()
    }

    override fun onResume() {
        super.onResume()
        cargarDetalle()
    }

    private fun cargarDetalle() {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT o.numero, o.fecha, c.nombre, c.direccion, e.modelo, e.codigo, o.tipo_servicio, o.descripcion, o.estado
            FROM ordenes o
            JOIN clientes c ON o.cliente_id = c.id
            JOIN equipos e ON o.equipo_id = e.id
            WHERE o.id = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(ordenId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                findViewById<TextView>(R.id.tvNumeroOrden)?.text = cursor.getString(0)
                findViewById<TextView>(R.id.tvFecha)?.text = "Fecha: ${cursor.getString(1)}"
                findViewById<TextView>(R.id.tvCliente)?.text = cursor.getString(2)
                findViewById<TextView>(R.id.tvDireccion)?.text = cursor.getString(3)
                findViewById<TextView>(R.id.tvEquipoModelo)?.text = cursor.getString(4)
                findViewById<TextView>(R.id.tvEquipoCodigo)?.text = "Código: ${cursor.getString(5)}"
                findViewById<TextView>(R.id.tvTipoServicio)?.text = cursor.getString(6)
                findViewById<TextView>(R.id.tvDescripcion)?.text = cursor.getString(7)

                val estado = cursor.getString(8)
                val tvEstado = findViewById<TextView>(R.id.tvEstado)
                tvEstado?.text = estado

                // Actualiza dinámicamente el color del estado según corresponda
                val colorRes = when (estado) {
                    "PENDIENTE" -> R.color.status_pendiente
                    "EN PROCESO" -> R.color.status_en_proceso
                    "FINALIZADA" -> R.color.status_finalizada
                    else -> R.color.status_cancelada
                }
                tvEstado?.background?.setTint(ContextCompat.getColor(this, colorRes))

                val btnIniciar = findViewById<View>(R.id.layoutBtnIniciar)
                val llAcciones = findViewById<View>(R.id.llAccionesMantenimiento)

                if (estado == "PENDIENTE") {
                    btnIniciar?.visibility = View.VISIBLE
                    llAcciones?.visibility = View.GONE
                } else if (estado == "EN PROCESO") {
                    btnIniciar?.visibility = View.GONE
                    llAcciones?.visibility = View.VISIBLE
                } else {
                    btnIniciar?.visibility = View.GONE
                    llAcciones?.visibility = View.GONE
                }
            }
        }
    }
}