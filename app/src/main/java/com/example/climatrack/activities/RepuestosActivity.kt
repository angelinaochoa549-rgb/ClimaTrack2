package com.example.climatrack.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.RepuestoAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Repuesto
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        ordenId = intent.getIntExtra("ORDEN_ID", 1)

        mantenimientoId = obtenerOcrearMantenimientoId()

        tvTotalRepuestos = findViewById(R.id.tvTotalRepuestos)
        rvRepuestos = findViewById(R.id.rvRepuestos)

        findViewById<Toolbar>(R.id.toolbar)?.setNavigationOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAgregarRepuesto)?.setOnClickListener {
            mostrarDialogoAgregar()
        }

        configurarBottomNavigation()

        // Inicializamos el Adapter pasando la lambda para el evento de eliminar
        rvRepuestos.layoutManager = LinearLayoutManager(this)
        adapter = RepuestoAdapter(emptyList()) { repuesto ->
            mostrarOpcionesRepuesto(repuesto)
        }
        rvRepuestos.adapter = adapter

        cargarHeaderOrden()
        cargarRepuestos()
    }

    private fun configurarBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav?.selectedItemId = R.id.nav_equipos
        bottomNav?.setOnItemSelectedListener { item ->
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
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    finish()
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

            findViewById<TextView>(R.id.tvNumeroOrden)?.text = "Orden: $numOrden"
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
        val repuestos = dbHelper.getRepuestosDisponibles()
        val nombres = repuestos.map { "${it.nombre} (${it.codigo})" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Seleccionar Repuesto")
            .setItems(nombres) { _, which ->
                val seleccionado = repuestos[which]
                pedirDetallesRepuesto(seleccionado.id, seleccionado.nombre)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun pedirDetallesRepuesto(repuestoId: Int, nombre: String) {
        val view = layoutInflater.inflate(R.layout.dialog_detalle_repuesto, null)
        val etCantidad = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCantidadRepuesto)
        val etObservacion = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etObservacionRepuesto)

        AlertDialog.Builder(this)
            .setTitle("Detalles de $nombre")
            .setView(view)
            .setPositiveButton("AGREGAR") { _, _ ->
                val cantidad = etCantidad.text.toString().toIntOrNull() ?: 1
                val observacion = etObservacion.text.toString().trim()
                agregarRepuestoReal(repuestoId, cantidad, observacion)
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun agregarRepuestoReal(repuestoId: Int, cantidad: Int, observacion: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("mantenimiento_id", mantenimientoId)
            put("repuesto_id", repuestoId)
            put("cantidad", cantidad)
            put("observacion", observacion)
        }
        val res = db.insert("detalle_repuestos", null, values)
        if (res != -1L) {
            Toast.makeText(this, "Repuesto agregado", Toast.LENGTH_SHORT).show()
            cargarRepuestos()
        }
    }

    private fun mostrarOpcionesRepuesto(repuesto: Repuesto) {
        val opciones = arrayOf("Eliminar de la orden")
        AlertDialog.Builder(this)
            .setTitle(repuesto.nombre)
            .setItems(opciones) { _, which ->
                if (which == 0) {
                    eliminarRepuesto(repuesto.id)
                }
            }
            .show()
    }

    private fun eliminarRepuesto(repuestoId: Int) {
        val db = dbHelper.writableDatabase
        val filasBorradas = db.delete(
            "detalle_repuestos",
            "mantenimiento_id = ? AND repuesto_id = ?",
            arrayOf(mantenimientoId.toString(), repuestoId.toString())
        )
        if (filasBorradas > 0) {
            Toast.makeText(this, "Repuesto eliminado", Toast.LENGTH_SHORT).show()
            cargarRepuestos()
        }
    }
}