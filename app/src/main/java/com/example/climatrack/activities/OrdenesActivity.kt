package com.example.climatrack.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
<<<<<<< HEAD
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
=======
import android.widget.Button
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.OrdenAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Orden

class OrdenesActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: OrdenAdapter
    private lateinit var rvOrdenes: RecyclerView
    private lateinit var tvTotal: TextView
<<<<<<< HEAD
    private lateinit var layoutSearchBar: View
    private lateinit var etBuscar: EditText
    private lateinit var btnClearSearch: ImageView
=======

    private lateinit var btnPendientes: Button
    private lateinit var btnEnProceso: Button
    private lateinit var btnFinalizadas: Button
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9

    private var estadoActual = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordenes)

        dbHelper = DatabaseHelper(this)

        val headerLayout = findViewById<View>(R.id.headerLayout)
        val bottomBarCustom = findViewById<View>(R.id.bottomBarCustom)

        // Manejo de Insets para que la barra de estado y notch no tapen la barra superior
        ViewCompat.setOnApplyWindowInsetsListener(headerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = 16.dpToPx(),
                top = systemBars.top + 8.dpToPx(),
                right = 16.dpToPx(),
                bottom = 12.dpToPx()
            )
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomBarCustom) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        rvOrdenes = findViewById(R.id.rvOrdenes)
        tvTotal = findViewById(R.id.tvTotalPendientes)
<<<<<<< HEAD
        layoutSearchBar = findViewById(R.id.layoutSearchBar)
        etBuscar = findViewById(R.id.etBuscarOrdenes)
        btnClearSearch = findViewById(R.id.btnClearSearch)

=======
        btnPendientes = findViewById(R.id.btnPendientes)
        btnEnProceso = findViewById(R.id.btnEnProceso)
        btnFinalizadas = findViewById(R.id.btnFinalizadas)
        
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        setupBuscador()

        rvOrdenes.layoutManager = LinearLayoutManager(this)
        adapter = OrdenAdapter(emptyList()) { orden ->
            val intent = Intent(this, DetalleOrdenActivity::class.java)
            intent.putExtra("ORDEN_ID", orden.id)
            startActivity(intent)
        }
        rvOrdenes.adapter = adapter

        btnPendientes.setOnClickListener {
            estadoActual = "PENDIENTE"
<<<<<<< HEAD
            actualizarUIFiltros()
=======
            actualizarBotonesTab(btnPendientes)
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
            cargarOrdenes()
        }
        btnEnProceso.setOnClickListener {
            estadoActual = "EN PROCESO"
<<<<<<< HEAD
            actualizarUIFiltros()
=======
            actualizarBotonesTab(btnEnProceso)
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
            cargarOrdenes()
        }
        btnFinalizadas.setOnClickListener {
            estadoActual = "FINALIZADA"
<<<<<<< HEAD
            actualizarUIFiltros()
            cargarOrdenes()
        }

        actualizarUIFiltros()
=======
            actualizarBotonesTab(btnFinalizadas)
            cargarOrdenes()
        }

        actualizarBotonesTab(btnPendientes)
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
        cargarOrdenes()
        setupBottomNavigation()
    }

<<<<<<< HEAD
    private fun setupBuscador() {
        findViewById<ImageView>(R.id.btnSearch).setOnClickListener {
            if (layoutSearchBar.visibility == View.VISIBLE) {
                ocultarBarraBusqueda()
            } else {
                mostrarBarraBusqueda()
            }
        }

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s?.toString() ?: ""
                btnClearSearch.visibility = if (texto.isNotEmpty()) View.VISIBLE else View.GONE
                adapter.filtrar(texto)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnClearSearch.setOnClickListener {
            etBuscar.setText("")
            adapter.filtrar("")
        }
    }

    private fun mostrarBarraBusqueda() {
        layoutSearchBar.visibility = View.VISIBLE
        etBuscar.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(etBuscar, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun ocultarBarraBusqueda() {
        layoutSearchBar.visibility = View.GONE
        etBuscar.setText("")
        adapter.filtrar("")
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(etBuscar.windowToken, 0)
    }

    private fun actualizarUIFiltros() {
        val btnPendientes = findViewById<TextView>(R.id.btnPendientes)
        val btnEnProceso = findViewById<TextView>(R.id.btnEnProceso)
        val btnFinalizadas = findViewById<TextView>(R.id.btnFinalizadas)

        val tabs = listOf(
            Triple(btnPendientes, "PENDIENTE", "Pendientes"),
            Triple(btnEnProceso, "EN PROCESO", "En proceso"),
            Triple(btnFinalizadas, "FINALIZADA", "Finalizadas")
        )

        for ((view, estado, _) in tabs) {
            if (view != null) {
                if (estado == estadoActual) {
                    view.setBackgroundResource(R.drawable.bg_chip_selected)
                    view.setTextColor(Color.WHITE)
                } else {
                    view.setBackgroundResource(android.R.color.transparent)
                    view.setTextColor(Color.parseColor("#0052CC"))
                }
=======
    private fun actualizarBotonesTab(btnSeleccionado: Button) {
        val botones = arrayOf(btnPendientes, btnEnProceso, btnFinalizadas)
        for (btn in botones) {
            if (btn == btnSeleccionado) {
                btn.setBackgroundColor(Color.parseColor("#0052CC"))
                btn.setTextColor(Color.WHITE)
            } else {
                btn.setBackgroundColor(Color.TRANSPARENT)
                btn.setTextColor(Color.parseColor("#0052CC"))
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navInicio).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navEquipos).setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
            finish()
        }
        findViewById<View>(R.id.navHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
            finish()
        }
    }

    private fun cargarOrdenes() {
        val db = dbHelper.readableDatabase
        val lista = mutableListOf<Orden>()

        val cursor = if (estadoActual == "FINALIZADA") {
            val query = """
                SELECT o.id, o.numero, o.fecha, c.nombre as cliente, e.modelo as equipo, o.tipo_servicio, o.descripcion, o.estado
                FROM ordenes o
                JOIN clientes c ON o.cliente_id = c.id
                JOIN equipos e ON o.equipo_id = e.id
                WHERE o.estado = 'FINALIZADA' OR o.estado = 'COMPLETADA'
            """.trimIndent()
            db.rawQuery(query, null)
        } else {
            val query = """
                SELECT o.id, o.numero, o.fecha, c.nombre as cliente, e.modelo as equipo, o.tipo_servicio, o.descripcion, o.estado
                FROM ordenes o
                JOIN clientes c ON o.cliente_id = c.id
                JOIN equipos e ON o.equipo_id = e.id
                WHERE o.estado = ?
            """.trimIndent()
            db.rawQuery(query, arrayOf(estadoActual))
        }

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    lista.add(
                        Orden(
                            id = it.getInt(0),
                            numero = it.getString(1),
                            fecha = it.getString(2),
                            clienteNombre = it.getString(3),
                            equipoNombre = it.getString(4),
                            tipoServicio = it.getString(5),
                            descripcion = it.getString(6),
                            estado = it.getString(7)
                        )
                    )
                } while (it.moveToNext())
            }
        }

        adapter.updateList(lista)
<<<<<<< HEAD
        if (etBuscar.text.isNotEmpty()) {
            adapter.filtrar(etBuscar.text.toString())
        }
        tvTotal.text = "Total: ${lista.size} órdenes"
=======
        val etiquetaEstado = when (estadoActual) {
            "PENDIENTE" -> "pendientes"
            "EN PROCESO" -> "en proceso"
            else -> "finalizadas"
        }
        tvTotal.text = "Total $etiquetaEstado: ${lista.size} órdenes"
>>>>>>> 77214c7b77679aa1f03a430104a0800c507dbef9
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}