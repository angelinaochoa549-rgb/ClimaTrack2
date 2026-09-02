package com.example.climatrack.activities

import android.content.ContentValues
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.RepuestoAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Repuesto
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RepuestosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: RepuestoAdapter
    private lateinit var rvRepuestos: RecyclerView
    private var ordenId: Int = -1
    private var mantenimientoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repuestos)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            finish()
            return
        }

        // Buscamos el mantenimiento_id asociado a esta orden (el último)
        mantenimientoId = obtenerUltimoMantenimientoId()

        rvRepuestos = findViewById(R.id.rvRepuestos)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        rvRepuestos.layoutManager = LinearLayoutManager(this)
        adapter = RepuestoAdapter(emptyList())
        rvRepuestos.adapter = adapter

        fabAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }

        cargarRepuestos()
    }

    private fun obtenerUltimoMantenimientoId(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM mantenimientos WHERE orden_id = ? ORDER BY id DESC LIMIT 1",
            arrayOf(ordenId.toString())
        )
        var id = -1
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        return id
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
        var total = 0

        if (cursor.moveToFirst()) {
            do {
                val rep = Repuesto(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    codigo = cursor.getString(2),
                    unidad = cursor.getString(3),
                    cantidad = cursor.getInt(4)
                )
                lista.add(rep)
                total += rep.cantidad
            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter.updateList(lista)
        findViewById<TextView>(R.id.tvTotalRepuestos).text = total.toString()
    }

    private fun mostrarDialogoAgregar() {
        // En una app real, aquí listaríamos los repuestos de la tabla 'repuestos'
        // Para el prototipo, agregaremos uno fijo para demostrar la funcionalidad.
        AlertDialog.Builder(this)
            .setTitle("Agregar Repuesto")
            .setMessage("¿Desea agregar 'Filtro de aire x1' a este mantenimiento?")
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
        // Buscamos el ID del repuesto "Filtro de aire" que pusimos en mock data
        val cursor = db.rawQuery("SELECT id FROM repuestos WHERE codigo = 'RPT-001' LIMIT 1", null)
        if (cursor.moveToFirst()) {
            val repuestoId = cursor.getInt(0)
            val values = ContentValues().apply {
                put("mantenimiento_id", mantenimientoId)
                put("repuesto_id", repuestoId)
                put("cantidad", 1)
            }
            db.insert("detalle_repuestos", null, values)
            cargarRepuestos()
        }
        cursor.close()
    }
}