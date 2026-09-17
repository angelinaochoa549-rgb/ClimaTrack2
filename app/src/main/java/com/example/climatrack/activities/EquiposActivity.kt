package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
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

        setupBottomNavigation()
        setupRecyclerView()
        setupBuscador()

        binding.btnAgregarEquipo.setOnClickListener {
            startActivity(Intent(this, FormularioEquipoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
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
}