package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.R
import com.example.climatrack.adapters.HistorialAdapter
import com.example.climatrack.databinding.ActivityHistorialBinding
import com.example.climatrack.models.Mantenimiento
import com.google.android.material.tabs.TabLayout

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var adapter: HistorialAdapter
    private val listaMantenimientos = mutableListOf<Mantenimiento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupBottomNavigation()
        cargarDatos()
        setupTabs()
        setupRecyclerView()
    }

    private fun setupBottomNavigation() {
        // Seleccionar el ítem actual de Historial
        binding.bottomNavigation.selectedItemId = R.id.nav_historial

        // Configurar acciones de la barra inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.nav_ordenes -> {
                    startActivity(Intent(this, OrdenesActivity::class.java))
                    true
                }
                R.id.nav_equipos -> {
                    startActivity(Intent(this, EquiposActivity::class.java))
                    true
                }
                R.id.nav_historial -> true
                else -> false
            }
        }
    }

    private fun cargarDatos() {
        listaMantenimientos.add(
            Mantenimiento("1", "OT-00025", "18/08/2026", "10:30 a. m.", "Técnico 01", "PREVENTIVO", "Limpieza de filtros, revisión de presión, ajuste de conexiones eléctricas y prueba de funcionamiento.")
        )
        listaMantenimientos.add(
            Mantenimiento("2", "OT-00018", "12/07/2026", "09:15 a. m.", "Técnico 02", "CORRECTIVO", "Cambio de capacitor y limpieza de serpentín.")
        )
        listaMantenimientos.add(
            Mantenimiento("3", "OT-00012", "10/06/2026", "11:00 a. m.", "Técnico 01", "PREVENTIVO", "Mantenimiento preventivo general.")
        )
        listaMantenimientos.add(
            Mantenimiento("4", "OT-00007", "05/05/2026", "03:20 p. m.", "Técnico 03", "CORRECTIVO", "Fuga de refrigerante, sellado y carga.")
        )
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("TODOS"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("PREVENTIVOS"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("CORRECTIVOS"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("INSPECCIONES"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> adapter.actualizarLista(listaMantenimientos)
                    1 -> adapter.actualizarLista(listaMantenimientos.filter { it.tipo == "PREVENTIVO" })
                    2 -> adapter.actualizarLista(listaMantenimientos.filter { it.tipo == "CORRECTIVO" })
                    3 -> adapter.actualizarLista(listaMantenimientos.filter { it.tipo == "INSPECCIÓN" })
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(listaMantenimientos)
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
        binding.rvHistorial.adapter = adapter
    }
}