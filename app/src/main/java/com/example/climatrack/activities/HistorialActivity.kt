package com.example.climatrack.activities

import android.content.Intent
<<<<<<< HEAD
import android.graphics.Color
=======
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.climatrack.R
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

        // Manejo de Insets para que la barra de estado/notch no solape el header
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
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Configuración del RecyclerView
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)

        // Cargar todos los registros al iniciar
        val listaInicial = dbHelper.getHistorialMantenimientos("TODOS")
        adapter = HistorialAdapter(listaInicial)
        binding.rvHistorial.adapter = adapter

        setupBuscador()
        setupFiltrosChips()
        setupBottomNavigation()

        // Botón regresar
        binding.btnBack.setOnClickListener {
            finish()
        }

        setupBottomNavigation()
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
            startActivity(Intent(this, EquiposActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }
        binding.navHistorial.setOnClickListener {
            // Ya estás aquí
        }
    }

    private fun setupBuscador() {
        // Alternar visibilidad de la barra de búsqueda al hacer clic en la lupa del header
        binding.btnFiltroHeader.setOnClickListener {
            if (binding.layoutSearchBar.visibility == View.VISIBLE) {
                ocultarBarraBusqueda()
            } else {
                mostrarBarraBusqueda()
            }
        }

        binding.etBuscarHistorial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s?.toString() ?: ""
                binding.btnClearSearch.visibility = if (texto.isNotEmpty()) View.VISIBLE else View.GONE
                adapter.filtrar(texto)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etBuscarHistorial.setText("")
            adapter.filtrar("")
        }
    }

    private fun mostrarBarraBusqueda() {
        binding.layoutSearchBar.visibility = View.VISIBLE
        binding.etBuscarHistorial.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.etBuscarHistorial, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun ocultarBarraBusqueda() {
        binding.layoutSearchBar.visibility = View.GONE
        binding.etBuscarHistorial.setText("")
        adapter.filtrar("")
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.etBuscarHistorial.windowToken, 0)
    }

    private fun setupFiltrosChips() {
        binding.btnTodos.setOnClickListener {
            actualizarChipSeleccionado(binding.btnTodos)
            filtrarLista("TODOS")
        }

        binding.btnPreventivos.setOnClickListener {
            actualizarChipSeleccionado(binding.btnPreventivos)
            filtrarLista("PREVENTIVO")
        }

        binding.btnCorrectivos.setOnClickListener {
            actualizarChipSeleccionado(binding.btnCorrectivos)
            filtrarLista("CORRECTIVO")
        }

        binding.btnInspecciones.setOnClickListener {
            actualizarChipSeleccionado(binding.btnInspecciones)
            filtrarLista("INSPECCIÓN")
        }
    }

    private fun actualizarChipSeleccionado(chipActivo: TextView) {
        val chips = listOf(binding.btnTodos, binding.btnPreventivos, binding.btnCorrectivos, binding.btnInspecciones)
        for (chip in chips) {
            if (chip == chipActivo) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(Color.parseColor("#0052CC"))
            }
        }
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
            startActivity(Intent(this, EquiposActivity::class.java))
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navHistorial.setOnClickListener {
            // Ya estamos en Historial
        }
    }

    private fun filtrarLista(tipo: String) {
        val listaFiltrada = dbHelper.getHistorialMantenimientos(tipo)
        adapter.actualizarLista(listaFiltrada)
        if (binding.etBuscarHistorial.text.isNotEmpty()) {
            adapter.filtrar(binding.etBuscarHistorial.text.toString())
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}