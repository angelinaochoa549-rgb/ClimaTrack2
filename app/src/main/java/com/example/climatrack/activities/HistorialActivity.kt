package com.example.climatrack.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.adapters.HistorialAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityHistorialBinding

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var adapter: HistorialAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Configuración del RecyclerView
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)

        // Cargar todos los registros al iniciar
        val listaInicial = dbHelper.getHistorialMantenimientos("TODOS")
        adapter = HistorialAdapter(listaInicial)
        binding.rvHistorial.adapter = adapter

        // Eventos de clic para los botones de filtro
        binding.btnTodos.setOnClickListener {
            filtrarLista("TODOS")
        }

        binding.btnPreventivos.setOnClickListener {
            filtrarLista("PREVENTIVO")
        }

        binding.btnCorrectivos.setOnClickListener {
            filtrarLista("CORRECTIVO")
        }

        binding.btnInspecciones.setOnClickListener {
            filtrarLista("INSPECCIÓN")
        }

        // Botón regresar
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun filtrarLista(tipo: String) {
        val listaFiltrada = dbHelper.getHistorialMantenimientos(tipo)
        adapter.actualizarLista(listaFiltrada)
    }
}