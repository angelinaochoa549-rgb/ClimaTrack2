package com.example.climatrack.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.OrdenAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Orden

class OrdenesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: OrdenAdapter
    private lateinit var rvOrdenes: RecyclerView
    private lateinit var tvTotal: TextView

    private lateinit var btnPendientes: Button
    private lateinit var btnEnProceso: Button
    private lateinit var btnFinalizadas: Button

    private var estadoActual = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenes)

        dbHelper = DatabaseHelper(this)

        rvOrdenes = findViewById(R.id.rvOrdenes)
        tvTotal = findViewById(R.id.tvTotalPendientes)
        btnPendientes = findViewById(R.id.btnPendientes)
        btnEnProceso = findViewById(R.id.btnEnProceso)
        btnFinalizadas = findViewById(R.id.btnFinalizadas)
        
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        rvOrdenes.layoutManager = LinearLayoutManager(this)
        adapter = OrdenAdapter(emptyList()) { orden ->
            val intent = Intent(this, DetalleOrdenActivity::class.java)
            intent.putExtra("ORDEN_ID", orden.id)
            startActivity(intent)
        }
        rvOrdenes.adapter = adapter

        btnPendientes.setOnClickListener {
            estadoActual = "PENDIENTE"
            actualizarBotonesTab(btnPendientes)
            cargarOrdenes()
        }
        btnEnProceso.setOnClickListener {
            estadoActual = "EN PROCESO"
            actualizarBotonesTab(btnEnProceso)
            cargarOrdenes()
        }
        btnFinalizadas.setOnClickListener {
            estadoActual = "FINALIZADA"
            actualizarBotonesTab(btnFinalizadas)
            cargarOrdenes()
        }

        actualizarBotonesTab(btnPendientes)
        cargarOrdenes()
        setupBottomNavigation()
    }

    private fun actualizarBotonesTab(btnSeleccionado: Button) {
        val botones = arrayOf(btnPendientes, btnEnProceso, btnFinalizadas)
        for (btn in botones) {
            if (btn == btnSeleccionado) {
                btn.setBackgroundColor(Color.parseColor("#0052CC"))
                btn.setTextColor(Color.WHITE)
            } else {
                btn.setBackgroundColor(Color.TRANSPARENT)
                btn.setTextColor(Color.parseColor("#0052CC"))
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navInicio).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navEquipos).setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
            finish()
        }
    }

    private fun cargarOrdenes() {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Orden>()

        val cursor = if (estadoActual == "FINALIZADA") {
            val query = """
                SELECT o.id, o.numero, o.fecha, c.nombre as cliente, e.modelo as equipo, o.tipo_servicio, o.descripcion, o.estado
                FROM ordenes o
                JOIN clientes c ON o.cliente_id = c.id
                JOIN equipos e ON o.equipo_id = e.id
                WHERE o.estado = 'FINALIZADA' OR o.estado = 'COMPLETADA'
            """.trimIndent()
            db.rawQuery(query, null)
        } else {
            val query = """
                SELECT o.id, o.numero, o.fecha, c.nombre as cliente, e.modelo as equipo, o.tipo_servicio, o.descripcion, o.estado
                FROM ordenes o
                JOIN clientes c ON o.cliente_id = c.id
                JOIN equipos e ON o.equipo_id = e.id
                WHERE o.estado = ?
            """.trimIndent()
            db.rawQuery(query, arrayOf(estadoActual))
        }

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Orden(
                            id = it.getInt(0),
                            numero = it.getString(1),
                            fecha = it.getString(2),
                            clienteNombre = it.getString(3),
                            equipoNombre = it.getString(4),
                            tipoServicio = it.getString(5),
                            descripcion = it.getString(6),
                            estado = it.getString(7)
                        )
                    )
                } while (it.moveToNext())
            }
        }

        adapter.updateList(lista)
        val etiquetaEstado = when (estadoActual) {
            "PENDIENTE" -> "pendientes"
            "EN PROCESO" -> "en proceso"
            else -> "finalizadas"
        }
        tvTotal.text = "Total $etiquetaEstado: ${lista.size} órdenes"
    }
}