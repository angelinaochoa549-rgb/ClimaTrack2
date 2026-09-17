package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.OrdenAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Orden
import com.google.android.material.tabs.TabLayout

class OrdenesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: OrdenAdapter
    private lateinit var rvOrdenes: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvSinOrdenes: TextView

    private var estadoActual = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenes)

        dbHelper = DatabaseHelper(this)

        rvOrdenes = findViewById(R.id.rvOrdenes)
        tvTotal = findViewById(R.id.tvTotalPendientes)
        // tvSinOrdenes no existe en el XML actual, usaremos una lógica simple o lo ignoraremos
        // tvSinOrdenes = findViewById(R.id.tvSinOrdenes) 
        
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

        findViewById<View>(R.id.btnPendientes).setOnClickListener {
            estadoActual = "PENDIENTE"
            cargarOrdenes()
        }
        findViewById<View>(R.id.btnEnProceso).setOnClickListener {
            estadoActual = "EN PROCESO"
            cargarOrdenes()
        }
        findViewById<View>(R.id.btnFinalizadas).setOnClickListener {
            estadoActual = "FINALIZADA"
            cargarOrdenes()
        }

        cargarOrdenes()
        setupBottomNavigation()
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
        val query = """
            SELECT o.id, o.numero, o.fecha, c.nombre as cliente, e.modelo as equipo, o.tipo_servicio, o.descripcion, o.estado
            FROM ordenes o
            JOIN clientes c ON o.cliente_id = c.id
            JOIN equipos e ON o.equipo_id = e.id
            WHERE o.estado = ?
        """.trimIndent()

        val lista = mutableListOf<Orden>()

        dbHelper.readableDatabase.rawQuery(query, arrayOf(estadoActual)).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    lista.add(
                        Orden(
                            id = cursor.getInt(0),
                            numero = cursor.getString(1),
                            fecha = cursor.getString(2),
                            clienteNombre = cursor.getString(3),
                            equipoNombre = cursor.getString(4),
                            tipoServicio = cursor.getString(5),
                            descripcion = cursor.getString(6),
                            estado = cursor.getString(7)
                        )
                    )
                } while (cursor.moveToNext())
            }
        }

        adapter.updateList(lista)
        tvTotal.text = "Total: ${lista.size} órdenes"
    }
}