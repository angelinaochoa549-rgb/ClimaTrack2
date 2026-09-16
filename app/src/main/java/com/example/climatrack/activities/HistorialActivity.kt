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

        // Listener para el botón de regreso en el header
        binding.btnBack.setOnClickListener { finish() }

        setupBottomNavigation()
        setupRecyclerView()
        setupChips()

        cargarDatos()
    }

    private fun cargarDatos(filtro: String = "TODOS") {
        val listaDB = dbHelper.getHistorialMantenimientos(filtro)
        listaMantenimientos.clear()
        listaMantenimientos.addAll(listaDB)
        adapter.actualizarLista(listaMantenimientos)
    }

    // Adaptado a la estructura de Chips / TextViews de tu XML
    private fun setupChips() {
        binding.chipTodos.setOnClickListener {
            seleccionarChip("TODOS")
            cargarDatos("TODOS")
        }
        binding.chipPreventivo.setOnClickListener {
            seleccionarChip("PREVENTIVO")
            cargarDatos("PREVENTIVO")
        }
        binding.chipCorrectivo.setOnClickListener {
            seleccionarChip("CORRECTIVO")
            cargarDatos("CORRECTIVO")
        }
        binding.chipInspeccion.setOnClickListener {
            seleccionarChip("INSPECCIÓN")
            cargarDatos("INSPECCIÓN")
        }
    }

    private fun seleccionarChip(filtro: String) {
        // Resetea los backgrounds de los chips según la selección
        binding.chipTodos.setBackgroundResource(if (filtro == "TODOS") R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
        binding.chipPreventivo.setBackgroundResource(if (filtro == "PREVENTIVO") R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
        binding.chipCorrectivo.setBackgroundResource(if (filtro == "CORRECTIVO") R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
        binding.chipInspeccion.setBackgroundResource(if (filtro == "INSPECCIÓN") R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected)
    }

    // Adaptado al LinearLayout con items de navegación de tu XML
    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        binding.navOrdenes.setOnClickListener {
            startActivity(Intent(this, OrdenesActivity::class.java))
            finish()
        }
        binding.navEquipos.setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = HistorialAdapter(listaMantenimientos)
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
        binding.rvHistorial.adapter = adapter
    }
}