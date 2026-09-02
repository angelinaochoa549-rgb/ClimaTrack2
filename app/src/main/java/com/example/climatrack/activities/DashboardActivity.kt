package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dbHelper = DatabaseHelper(this)

        // Recibimos el nombre del técnico que viene desde el Login
        val nombreTecnico = intent.getStringExtra("NOMBRE_TECNICO") ?: "Técnico"

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvPendientes = findViewById<TextView>(R.id.tvPendientes)
        val tvEnProceso = findViewById<TextView>(R.id.tvEnProceso)
        val tvFinalizadas = findViewById<TextView>(R.id.tvFinalizadas)

        tvBienvenida.text = getString(R.string.bienvenida_hola, nombreTecnico)

        // Actualizamos contadores reales
        tvPendientes.text = contarOrdenesPorEstado("PENDIENTE").toString()
        tvEnProceso.text = contarOrdenesPorEstado("EN PROCESO").toString()
        tvFinalizadas.text = contarOrdenesPorEstado("FINALIZADA").toString()

        findViewById<LinearLayout>(R.id.btnOrdenes).setOnClickListener {
            val intent = Intent(this, OrdenesActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnEquipos).setOnClickListener {
            val intent = Intent(this, EquiposActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnHistorial).setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_ordenes -> {
                    startActivity(Intent(this, OrdenesActivity::class.java))
                    true
                }
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

    private fun contarOrdenesPorEstado(estado: String): Int {
        var total = 0
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT COUNT(*) FROM ordenes WHERE estado = ?",
                arrayOf(estado)
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    total = cursor.getInt(0)
                }
            }
        }
        return total
    }
}