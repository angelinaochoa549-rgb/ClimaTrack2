package com.example.climatrack.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityAprobacionBinding
import java.io.File
import java.io.FileOutputStream
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
            Toast.makeText(this, getString(R.string.firma_borrada), Toast.LENGTH_SHORT).show()
        }

        cargarResumen()

        binding.btnGuardarAprobacion.setOnClickListener {
            val nombre = binding.etNombreCliente.text.toString().trim()

            if (nombre.isEmpty()) {
                binding.etNombreCliente.error = getString(R.string.error_nombre_requerido)
                return@setOnClickListener
            }

            if (!binding.cbAceptacion.isChecked) {
                Toast.makeText(this, getString(R.string.error_aceptacion_requerida), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.signatureView.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_firma_requerida), Toast.LENGTH_SHORT).show()
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
            binding.tvOrden.text = getString(R.string.label_orden_num, cursor.getString(0))
            binding.tvCliente.text = getString(R.string.label_cliente, cursor.getString(1))
            binding.tvEquipo.text = getString(R.string.label_equipo_aprobacion, "${cursor.getString(2)} (${cursor.getString(3)})")

            val tv = TextView(this)
            tv.text = getString(R.string.label_servicio_resumen, cursor.getString(6), cursor.getString(4), cursor.getString(5))
            tv.setTextColor(resources.getColor(R.color.on_surface, theme))
            binding.llResumen.addView(tv)
        }
        cursor.close()
    }

    private fun guardarAprobacionYFinalizar(nombreCliente: String) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val rutaFirma = guardarFirmaComoImagen()

            val values = ContentValues().apply {
                put("orden_id", ordenId)
                put("cliente", nombreCliente)
                put("aceptado", 1)
                put("fecha", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
                put("ruta_firma", rutaFirma)
            }
            db.insert("aprobaciones", null, values)

            // Finalizar la orden
            val orderValues = ContentValues().apply {
                put("estado", "FINALIZADA")
            }
            db.update("ordenes", orderValues, "id = ?", arrayOf(ordenId.toString()))

            db.setTransactionSuccessful()
            Toast.makeText(this, getString(R.string.aprobacion_guardada), Toast.LENGTH_SHORT).show()

            // Regresar al Dashboard (limpiando stack)
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_guardar_aprobacion, e.message), Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }

    private fun guardarFirmaComoImagen(): String? {
        val bitmap = binding.signatureView.getBitmap() ?: return null
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "FIRMA_${ordenId}_$timeStamp.png"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        return try {
            val file = File(storageDir, fileName)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
