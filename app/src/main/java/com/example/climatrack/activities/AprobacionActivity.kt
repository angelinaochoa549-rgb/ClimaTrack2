package com.example.climatrack.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityAprobacionBinding
import java.text.SimpleDateFormat
import java.util.*

class AprobacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAprobacionBinding
    private lateinit var dbHelper: DatabaseHelper
    private var ordenId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAprobacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            finish()
            return
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnLimpiarFirma.setOnClickListener {
            binding.signatureView.clear()
            Toast.makeText(this, "Firma borrada", Toast.LENGTH_SHORT).show()
        }

        cargarResumen()

        binding.btnGuardarAprobacion.setOnClickListener {
            val nombre = binding.etNombreCliente.text.toString().trim()

            if (nombre.isEmpty()) {
                binding.etNombreCliente.error = "Por favor ingrese el nombre del cliente"
                return@setOnClickListener
            }

            if (!binding.cbAceptacion.isChecked) {
                Toast.makeText(this, "El cliente debe aceptar el servicio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.signatureView.isEmpty()) {
                Toast.makeText(this, "Por favor solicite la firma del cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            guardarAprobacionYFinalizar(nombre)
        }
    }

    private fun cargarResumen() {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT o.numero, c.nombre, e.modelo, e.codigo, m.diagnostico, m.trabajo_realizado, o.tipo_servicio
            FROM ordenes o
            JOIN clientes c ON o.cliente_id = c.id
            JOIN equipos e ON o.equipo_id = e.id
            LEFT JOIN mantenimientos m ON m.orden_id = o.id
            WHERE o.id = ?
            ORDER BY m.id DESC LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(ordenId.toString()))

        if (cursor.moveToFirst()) {
            binding.tvOrden.text = "Orden: ${cursor.getString(0)}"
            binding.tvCliente.text = "Cliente: ${cursor.getString(1)}"
            binding.tvEquipo.text = "Equipo: ${cursor.getString(2)} (${cursor.getString(3)})"

            val tv = TextView(this)
            tv.text = "Servicio: ${cursor.getString(6)}\n\nDiagnóstico: ${cursor.getString(4)}\n\nTrabajo: ${cursor.getString(5)}"
            tv.setTextColor(resources.getColor(R.color.on_surface, theme))
            binding.llResumen.addView(tv)
        }
        cursor.close()
    }

    private fun guardarAprobacionYFinalizar(nombreCliente: String) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("orden_id", ordenId)
                put("cliente", nombreCliente)
                put("aceptado", 1)
                put("fecha", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
            }
            db.insert("aprobaciones", null, values)

            // Finalizar la orden
            val orderValues = ContentValues().apply {
                put("estado", "FINALIZADA")
            }
            db.update("ordenes", orderValues, "id = ?", arrayOf(ordenId.toString()))

            db.setTransactionSuccessful()
            Toast.makeText(this, "Orden finalizada correctamente", Toast.LENGTH_SHORT).show()

            // Regresar al Dashboard (limpiando stack)
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } finally {
            db.endTransaction()
        }
    }
}