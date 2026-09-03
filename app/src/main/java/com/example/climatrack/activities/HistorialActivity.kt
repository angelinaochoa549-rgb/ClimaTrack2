package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.R
import com.example.climatrack.adapters.HistorialAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityHistorialBinding
import com.example.climatrack.models.Mantenimiento
import com.google.android.material.tabs.TabLayout

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: HistorialAdapter
    private val listaMantenimientos = mutableListOf<Mantenimiento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupBottomNavigation()
        setupRecyclerView()
        setupTabs()
        
        cargarDatos()
    }

    private fun cargarDatos(filtro: String = "TODOS") {
        val listaDB = dbHelper.getHistorialMantenimientos(filtro)
        listaMantenimientos.clear()
        listaMantenimientos.addAll(listaDB)
        adapter.actualizarLista(listaMantenimientos)
    }

    private fun setupTabs() {
        binding.tabLayout.removeAllTabs()
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("TODOS"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("PREVENTIVO"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("CORRECTIVO"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("INSPECCIÓN"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val filtro = when (tab?.position) {
                    1 -> "PREVENTIVO"
                    2 -> "CORRECTIVO"
                    3 -> "INSPECCIÓN"
                    else -> "TODOS"
                }
                cargarDatos(filtro)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_historial
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_ordenes -> {
                    startActivity(Intent(this, OrdenesActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_equipos -> {
                    startActivity(Intent(this, EquiposActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_historial -> true
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(listaMantenimientos)
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
        binding.rvHistorial.adapter = adapter
    }
}