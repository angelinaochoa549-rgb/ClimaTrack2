package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.adapters.EquipoAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityEquiposBinding
import com.example.climatrack.models.Equipo

class EquiposActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEquiposBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: EquipoAdapter
    private val listaEquipos = mutableListOf<Equipo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Manejo de Insets para que la barra de estado/notch no solape los botones del header
        ViewCompat.setOnApplyWindowInsetsListener(binding.headerToolbar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = 16.dpToPx(),
                top = systemBars.top + 8.dpToPx(),
                right = 16.dpToPx(),
                bottom = 12.dpToPx()
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = 0,
                top = 6.dpToPx(),
                right = 0,
                bottom = systemBars.bottom + 6.dpToPx()
            )
            insets
        }

        setupBottomNavigation()
        setupRecyclerView()
        setupBuscador()
        setupFiltroBoton()

        // Botón (+) Agregar Equipo
        binding.btnAgregarEquipo.setOnClickListener {
            startActivity(Intent(this, FormularioEquipoActivity::class.java))
        }

        // Ícono de menú en cabecera
        binding.iconHeader.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun setupFiltroBoton() {
        // Botón de filtro (ícono con líneas de ordenación)
        binding.btnFilter.setOnClickListener {
            mostrarDialogoFiltroEquipos()
        }
    }

    private fun mostrarDialogoFiltroEquipos() {
        val opciones = arrayOf(
            "📋 Todos los equipos",
            "🟢 Operativos / Activos",
            "🔵 En uso",
            "🟡 En mantenimiento",
            "🔴 Fuera de servicio",
            "❄️ Split Pared",
            "📦 Mini Split",
            "🏢 Cassette",
            "🏭 Chiller"
        )

        AlertDialog.Builder(this)
            .setTitle("Filtrar Equipos")
            .setItems(opciones) { _, which ->
                val filtro = when (which) {
                    0 -> "TODOS"
                    1 -> "OPERATIVO"
                    2 -> "EN USO"
                    3 -> "EN MANTENIMIENTO"
                    4 -> "FUERA DE SERVICIO"
                    5 -> "Split Pared"
                    6 -> "Mini Split"
                    7 -> "Cassette"
                    8 -> "Chiller"
                    else -> "TODOS"
                }

                adapter.filtrarPorEstadoOTipo(filtro)

                val labelFiltro = opciones[which].substring(3)
                Toast.makeText(this, "Filtro aplicado: $labelFiltro", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navOrdenes.setOnClickListener {
            startActivity(Intent(this, OrdenesActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navEquipos.setOnClickListener {
            // Ya estás en esta pantalla
        }

        binding.navHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun cargarDatos() {
        val equiposDB = dbHelper.getEquipos()
        listaEquipos.clear()
        listaEquipos.addAll(equiposDB)
        adapter.actualizarLista(listaEquipos)

        if (binding.etBuscar.text.isNotEmpty()) {
            adapter.filtrar(binding.etBuscar.text.toString())
        }
    }

    private fun setupRecyclerView() {
        adapter = EquipoAdapter(listaEquipos) { equipo ->
            val intent = Intent(this, DetalleEquipoActivity::class.java)
            intent.putExtra("EQUIPO_ID", equipo.id)
            startActivity(intent)
        }
        binding.rvEquipos.layoutManager = LinearLayoutManager(this)
        binding.rvEquipos.adapter = adapter
    }

    private fun setupBuscador() {
        binding.etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}