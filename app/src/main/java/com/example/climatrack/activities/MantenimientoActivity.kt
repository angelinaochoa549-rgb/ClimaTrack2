package com.example.climatrack.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Locale

class MantenimientoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var ordenId: Int = -1

    private lateinit var toggleGroupTipoServicio: MaterialButtonToggleGroup
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHoraInicio: TextInputEditText
    private lateinit var etDiagnostico: TextInputEditText
    private lateinit var etTrabajoRealizado: TextInputEditText
    private lateinit var etObservaciones: TextInputEditText
    private lateinit var etRecomendaciones: TextInputEditText
    private lateinit var spEstadoEquipo: AutoCompleteTextView
    private lateinit var spTiempoEmpleado: AutoCompleteTextView
    private lateinit var spTecnico: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mantenimiento)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        inicializarVistas()
        configurarToolbar()
        configurarPickers()
        configurarSpinners()

        if (ordenId != -1) {
            cargarDatosOrden()
        }
    }

    private fun inicializarVistas() {
        toggleGroupTipoServicio = findViewById(R.id.toggleGroupTipoServicio)
        etFecha = findViewById(R.id.etFecha)
        etHoraInicio = findViewById(R.id.etHoraInicio)
        etDiagnostico = findViewById(R.id.etDiagnostico)
        etTrabajoRealizado = findViewById(R.id.etTrabajoRealizado)
        etObservaciones = findViewById(R.id.etObservaciones)
        etRecomendaciones = findViewById(R.id.etRecomendaciones)
        spEstadoEquipo = findViewById(R.id.spEstadoEquipo)
        spTiempoEmpleado = findViewById(R.id.spTiempoEmpleado)
        spTecnico = findViewById(R.id.spTecnico)

        // Seleccionar por defecto "Preventivo"
        toggleGroupTipoServicio.check(R.id.btnPreventivo)
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        findViewById<TextView>(R.id.btnGuardar).setOnClickListener {
            guardarMantenimiento()
        }
    }

    private fun configurarPickers() {
        // Selector de Fecha
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    etFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Selector de Hora
        etHoraInicio.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    etHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun configurarSpinners() {
        val estados = arrayOf("Operativo", "En Mantenimiento", "Fuera de Servicio")
        val adapterEstados = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, estados)
        spEstadoEquipo.setAdapter(adapterEstados)

        val tiempos = arrayOf("30m", "1h 00m", "1h 30m", "2h 00m", "2h 30m", "3h 00m")
        val adapterTiempos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiempos)
        spTiempoEmpleado.setAdapter(adapterTiempos)

        val tecnicos = arrayOf("Técnico 01", "Técnico 02", "Técnico 03")
        val adapterTecnicos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tecnicos)
        spTecnico.setAdapter(adapterTecnicos)
    }

    private fun cargarDatosOrden() {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT o.numero, c.nombre, e.modelo, o.tipo_servicio, o.fecha, o.descripcion, e.estado
            FROM ordenes o
            JOIN clientes c ON o.cliente_id = c.id
            JOIN equipos e ON o.equipo_id = e.id
            WHERE o.id = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(ordenId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                findViewById<TextView>(R.id.tvNumeroOrden).text = "Orden: ${cursor.getString(0)}"
                findViewById<TextView>(R.id.tvClienteInfo).text = "Cliente: ${cursor.getString(1)}"
                findViewById<TextView>(R.id.tvEquipoInfo).text = "Equipo: ${cursor.getString(2)}"

                etFecha.setText(cursor.getString(4))
                etDiagnostico.setText(cursor.getString(5))
                spEstadoEquipo.setText(cursor.getString(6), false)
            }
        }
    }

    private fun guardarMantenimiento() {
        val diagnostico = etDiagnostico.text.toString().trim()
        val trabajo = etTrabajoRealizado.text.toString().trim()

        if (diagnostico.isEmpty() || trabajo.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios (*)", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            // 1. Insertar en la tabla mantenimientos
            val mantValues = ContentValues().apply {
                put("orden_id", ordenId)
                put("fecha", etFecha.text.toString())
                put("diagnostico", diagnostico)
                put("trabajo_realizado", trabajo)
                put("observaciones", etObservaciones.text.toString().trim())
                put("recomendaciones", etRecomendaciones.text.toString().trim())
                put("tiempo_empleado", spTiempoEmpleado.text.toString())
                put("tecnico_nombre", spTecnico.text.toString())
            }
            val mantId = db.insert("mantenimientos", null, mantValues)

            if (mantId == -1L) throw Exception("Error al insertar mantenimiento")

            // 2. Actualizar estado de la orden a EN PROCESO
            val orderValues = ContentValues().apply {
                put("estado", "EN PROCESO")
            }
            db.update("ordenes", orderValues, "id = ?", arrayOf(ordenId.toString()))

            // 3. Actualizar estado del equipo
            val nuevoEstadoEquipo = spEstadoEquipo.text.toString().uppercase()
            val queryEquipoId = "SELECT equipo_id FROM ordenes WHERE id = ?"
            var equipoId = -1
            db.rawQuery(queryEquipoId, arrayOf(ordenId.toString())).use { cursor ->
                if (cursor.moveToFirst()) equipoId = cursor.getInt(0)
            }

            if (equipoId != -1) {
                val equipoValues = ContentValues().apply {
                    put("estado", nuevoEstadoEquipo)
                }
                db.update("equipos", equipoValues, "id = ?", arrayOf(equipoId.toString()))
            }

            db.setTransactionSuccessful()
            Toast.makeText(this, "Mantenimiento registrado correctamente", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }
}
