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
            Toast.makeText(this, "Navegando a Órdenes", Toast.LENGTH_SHORT).show()
        }

        binding.cardEquipos.setOnClickListener {
            Toast.makeText(this, "Navegando a Equipos", Toast.LENGTH_SHORT).show()
        }

        binding.cardHistorial.setOnClickListener {
            Toast.makeText(this, "Navegando a Historial", Toast.LENGTH_SHORT).show()
        }

        binding.cardCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ordenes -> {
                    Toast.makeText(this, "Órdenes", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_equipos -> {
                    Toast.makeText(this, "Equipos", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_historial -> {
                    Toast.makeText(this, "Historial", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}