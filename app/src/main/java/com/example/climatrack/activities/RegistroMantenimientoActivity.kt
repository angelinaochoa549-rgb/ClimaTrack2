package com.example.climatrack.activities

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class RegistroMantenimientoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var ordenId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_mantenimiento)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            Toast.makeText(this, "Error: Orden no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val cgTipo = findViewById<ChipGroup>(R.id.cgTipoServicio)
        val etDiagnostico = findViewById<TextInputEditText>(R.id.etDiagnostico)
        val etTrabajo = findViewById<TextInputEditText>(R.id.etTrabajo)
        val etObservaciones = findViewById<TextInputEditText>(R.id.etObservaciones)
        val etRecomendaciones = findViewById<TextInputEditText>(R.id.etRecomendaciones)
        val actvEstado = findViewById<AutoCompleteTextView>(R.id.actvEstadoEquipo)
        val etTiempo = findViewById<TextInputEditText>(R.id.etTiempo)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        // Date Picker
        etFecha.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etFecha.setText("$day/${month + 1}/$year")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Dropdown para estado equipo
        val estados = arrayOf("OPERATIVO", "EN MANTENIMIENTO", "FUERA DE SERVICIO")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, estados)
        actvEstado.setAdapter(adapter)

        btnGuardar.setOnClickListener {
            val fecha = etFecha.text.toString().trim()
            val diagnostico = etDiagnostico.text.toString().trim()
            val trabajo = etTrabajo.text.toString().trim()
            
            val selectedChipId = cgTipo.checkedChipId
            val tipo = if (selectedChipId != -1) {
                findViewById<Chip>(selectedChipId).text.toString()
            } else ""

            if (fecha.isEmpty() || diagnostico.isEmpty() || trabajo.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos obligatorios (*)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            guardarMantenimiento(fecha, tipo, diagnostico, trabajo, etObservaciones.text.toString(), etRecomendaciones.text.toString())
        }
    }

    private fun guardarMantenimiento(fecha: String, tipo: String, diag: String, trab: String, obs: String, rec: String) {
        val db = dbHelper.writableDatabase
        
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put("orden_id", ordenId)
                put("fecha", fecha)
                put("diagnostico", diag)
                put("trabajo_realizado", trab)
                put("observaciones", obs)
                put("recomendaciones", rec)
            }
            db.insert("mantenimientos", null, values)

            // Actualizar estado de la orden a EN PROCESO o FINALIZADA (dependiendo de la lógica)
            // Por ahora lo pondremos en EN PROCESO si se registra el mantenimiento base.
            val orderValues = ContentValues().apply {
                put("estado", "EN PROCESO")
            }
            db.update("ordenes", orderValues, "id = ?", arrayOf(ordenId.toString()))

            db.setTransactionSuccessful()
            Toast.makeText(this, "Mantenimiento registrado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }
}