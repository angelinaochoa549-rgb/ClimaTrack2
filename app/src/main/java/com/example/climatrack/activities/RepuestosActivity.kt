package com.example.climatrack.activities

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.RepuestoAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Repuesto
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.Locale

class RepuestosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: RepuestoAdapter
    private lateinit var rvRepuestos: RecyclerView
    private lateinit var tvTotalRepuestos: TextView
    private var ordenId: Int = -1
    private var mantenimientoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repuestos)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", 1) // ID por defecto de prueba en caso de recibir -1

        // Buscamos o creamos el mantenimiento_id asociado a esta orden
        mantenimientoId = obtenerOcrearMantenimientoId()

        tvTotalRepuestos = findViewById(R.id.tvTotalRepuestos)
        rvRepuestos = findViewById(R.id.rvRepuestos)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

        // Configuración de Toolbar
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        val btnAgregarHeader = findViewById<ImageView>(R.id.btnAgregarRepuesto)
        btnAgregarHeader?.setOnClickListener {
            mostrarDialogoAgregar()
        }

        // Configuración de BottomNavigationView
        configurarBottomNavigation()

        // Configuración del RecyclerView
        rvRepuestos.layoutManager = LinearLayoutManager(this)
        adapter = RepuestoAdapter(emptyList())
        rvRepuestos.adapter = adapter

        fabAgregar?.setOnClickListener {
            mostrarDialogoAgregar()
        }

        cargarHeaderOrden()
        cargarRepuestos()
    }

    private fun configurarBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.selectedItemId = R.id.nav_equipos
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Navegar a Inicio", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ordenes -> {
                    Toast.makeText(this, "Navegar a Órdenes", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_equipos -> true
                R.id.nav_historial -> {
                    Toast.makeText(this, "Navegar a Historial", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun obtenerOcrearMantenimientoId(): Int {
        val dbReadable = dbHelper.readableDatabase
        val cursor = dbReadable.rawQuery(
            "SELECT id FROM mantenimientos WHERE orden_id = ? ORDER BY id DESC LIMIT 1",
            arrayOf(ordenId.toString())
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()

        // Si no existe un mantenimiento asociado a la orden, creamos uno de prueba automáticamente
        if (id == -1) {
            val dbWritable = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("orden_id", ordenId)
                put("fecha", "19/08/2026")
                put("diagnostico", "Mantenimiento Preventivo")
            }
            id = dbWritable.insert("mantenimientos", null, values).toInt()
        }
        return id
    }

    private fun cargarHeaderOrden() {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT o.numero, o.estado, c.nombre AS cliente, e.tipo, e.modelo, e.codigo
            FROM ordenes o
            LEFT JOIN clientes c ON o.cliente_id = c.id
            LEFT JOIN equipos e ON o.equipo_id = e.id
            WHERE o.id = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(ordenId.toString()))
        if (cursor.moveToFirst()) {
            val numOrden = cursor.getString(0) ?: "OT-00025"
            val estado = cursor.getString(1) ?: "EN PROCESO"
            val cliente = cursor.getString(2) ?: "ACME S.A.S."
            val equipoTipo = cursor.getString(3) ?: "Split Inverter"
            val equipoModelo = cursor.getString(4) ?: "24K"
            val equipoCodigo = cursor.getString(5) ?: "EQ-00015"

            findViewById<TextView>(R.id.tvNumeroOrden)?.text = getString(R.string.label_orden_num, numOrden)
            findViewById<TextView>(R.id.tvEstadoBadge)?.text = estado.uppercase()
            findViewById<TextView>(R.id.tvClienteInfo)?.text = "Cliente: $cliente"
            findViewById<TextView>(R.id.tvEquipoInfo)?.text = "Equipo: $equipoTipo $equipoModelo ($equipoCodigo)"
        }
        cursor.close()
    }

    private fun cargarRepuestos() {
        if (mantenimientoId == -1) return

        val db = dbHelper.readableDatabase
        val query = """
            SELECT r.id, r.nombre, r.codigo, r.unidad, dr.cantidad
            FROM detalle_repuestos dr
            JOIN repuestos r ON dr.repuesto_id = r.id
            WHERE dr.mantenimiento_id = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(mantenimientoId.toString()))
        val lista = mutableListOf<Repuesto>()
        var sumaTotal = 0.0

        if (cursor.moveToFirst()) {
            do {
                val codigo = cursor.getString(2) ?: ""
                val cantidad = cursor.getInt(4)

                val rep = Repuesto(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    codigo = codigo,
                    unidad = cursor.getString(3),
                    cantidad = cantidad
                )
                lista.add(rep)

                // Asignación de precio dinámico según el código para calcular el costo total
                val precioUnitario = when (codigo.uppercase().trim()) {
                    "RPT-001", "RPT-0007" -> 25000.0
                    "RPT-002", "RPT-0012" -> 18000.0
                    "RPT-0021" -> 45000.0
                    "RPT-0030" -> 60000.0
                    else -> 20000.0
                }
                sumaTotal += (precioUnitario * cantidad)

            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter.updateList(lista)

        val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
            maximumFractionDigits = 0
        }
        tvTotalRepuestos.text = formatoMoneda.format(sumaTotal)
    }

    private fun mostrarDialogoAgregar() {
        AlertDialog.Builder(this)
            .setTitle("Agregar Repuesto")
            .setMessage("¿Desea agregar 'Capacitor 35 uF x1' a este mantenimiento?")
            .setPositiveButton("AGREGAR") { _, _ ->
                agregarRepuestoMock()
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun agregarRepuestoMock() {
        if (mantenimientoId == -1) {
            Toast.makeText(this, "Debe registrar el mantenimiento primero", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT id FROM repuestos WHERE codigo = 'RPT-002' LIMIT 1", null)
        if (cursor.moveToFirst()) {
            val repuestoId = cursor.getInt(0)
            val values = ContentValues().apply {
                put("mantenimiento_id", mantenimientoId)
                put("repuesto_id", repuestoId)
                put("cantidad", 1)
            }
            db.insert("detalle_repuestos", null, values)
            cargarRepuestos()
        } else {
            // Si no encuentra RPT-002, agrega con el primer repuesto de la tabla
            val cursorFallback = db.rawQuery("SELECT id FROM repuestos LIMIT 1", null)
            if (cursorFallback.moveToFirst()) {
                val repuestoId = cursorFallback.getInt(0)
                val values = ContentValues().apply {
                    put("mantenimiento_id", mantenimientoId)
                    put("repuesto_id", repuestoId)
                    put("cantidad", 1)
                }
                db.insert("detalle_repuestos", null, values)
                cargarRepuestos()
            }
            cursorFallback.close()
        }
        cursor.close()
    }
}