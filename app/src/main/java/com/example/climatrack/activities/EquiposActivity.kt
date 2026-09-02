package com.example.climatrack.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.R
import com.example.climatrack.adapters.EquipoAdapter
import com.example.climatrack.databinding.ActivityEquiposBinding
import com.example.climatrack.models.Equipo

class EquiposActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEquiposBinding
    private lateinit var adapter: EquipoAdapter
    private val listaEquipos = mutableListOf<Equipo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEquiposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        cargarDatos()
        setupRecyclerView()
        setupBuscador()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_equipos

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    finish()
                    true
                }
                R.id.nav_ordenes -> true
                R.id.nav_equipos -> true
                R.id.nav_historial -> true
                else -> false
            }
        }
    }

    private fun cargarDatos() {
        listaEquipos.add(Equipo("1", "EQ-00015", "Split Pared", "LG", "Dual Inverter 24K", "LG24TI2022015", "ACME S.A.S.", "OPERATIVO", R.drawable.ic_equipos))
        listaEquipos.add(Equipo("2", "EQ-00016", "Cassette", "Samsung", "360 Cassette 36K", "SAM36C2021120", "Frio Total Ltda.", "EN MANTENIMIENTO", R.drawable.ic_equipos))
        listaEquipos.add(Equipo("3", "EQ-00017", "Mini Split", "Midea", "MS-18K", "MIDEA18K3344", "Hotel Caribe", "FUERA DE SERVICIO", R.drawable.ic_equipos))
        listaEquipos.add(Equipo("4", "EQ-00018", "Chiller", "York", "YK-50TR", "YORK50TR7788", "Clinica del Norte", "OPERATIVO", R.drawable.ic_equipos))
    }

    private fun setupRecyclerView() {
        adapter = EquipoAdapter(listaEquipos)
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