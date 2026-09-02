package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        tvTotal = findViewById(R.id.tvTotalOrdenes)
        tvSinOrdenes = findViewById(R.id.tvSinOrdenes)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarOrdenes).setNavigationOnClickListener {
            finish()
        }

        rvOrdenes.layoutManager = LinearLayoutManager(this)
        adapter = OrdenAdapter(emptyList()) { orden ->
            val intent = Intent(this, DetalleOrdenActivity::class.java)
            intent.putExtra("ORDEN_ID", orden.id)
            startActivity(intent)
        }
        rvOrdenes.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                estadoActual = when (tab?.position) {
                    0 -> "PENDIENTE"
                    1 -> "EN PROCESO"
                    2 -> "FINALIZADA"
                    else -> "PENDIENTE"
                }
                cargarOrdenes()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        cargarOrdenes()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_ordenes
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.nav_ordenes -> true
                R.id.nav_equipos -> {
                    startActivity(Intent(this, EquiposActivity::class.java))
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    true
                }
                else -> false
            }
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
        
        if (lista.isEmpty()) {
            tvSinOrdenes.visibility = View.VISIBLE
            rvOrdenes.visibility = View.GONE
        } else {
            tvSinOrdenes.visibility = View.GONE
            rvOrdenes.visibility = View.VISIBLE
        }
    }
}