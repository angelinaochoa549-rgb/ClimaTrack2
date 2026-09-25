package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Manejo de eventos en las tarjetas de accesos rápidos
        binding.cardOrdenes.setOnClickListener {
            startActivity(Intent(this, OrdenesActivity::class.java))
        }

        binding.cardEquipos.setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
        }

        binding.cardHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        binding.cardCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_ordenes -> {
                    startActivity(Intent(this, OrdenesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_equipos -> {
                    startActivity(Intent(this, EquiposActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}