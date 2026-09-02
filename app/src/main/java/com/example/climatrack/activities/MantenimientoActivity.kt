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
        val query = "SELECT numero_orden, cliente, equipo, tipo_servicio, fecha, hora_inicio, diagnostico, trabajo_realizado, observaciones, estado_equipo, tiempo_empleado, tecnico FROM ordenes WHERE id = ?"

        db.rawQuery(query, arrayOf(ordenId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                findViewById<TextView>(R.id.tvNumeroOrden).text = "Orden: ${cursor.getString(0)}"
                findViewById<TextView>(R.id.tvClienteInfo).text = "Cliente: ${cursor.getString(1)}"
                findViewById<TextView>(R.id.tvEquipoInfo).text = "Equipo: ${cursor.getString(2)}"

                etFecha.setText(cursor.getString(4))
                etHoraInicio.setText(cursor.getString(5))
                etDiagnostico.setText(cursor.getString(6))
                etTrabajoRealizado.setText(cursor.getString(7))
                etObservaciones.setText(cursor.getString(8))
                spEstadoEquipo.setText(cursor.getString(9), false)
                spTiempoEmpleado.setText(cursor.getString(10), false)
                spTecnico.setText(cursor.getString(11), false)
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

        val tipoServicio = when (toggleGroupTipoServicio.checkedButtonId) {
            R.id.btnPreventivo -> "Preventivo"
            R.id.btnCorrectivo -> "Correctivo"
            R.id.btnAsesoria -> "Asesoría"
            R.id.btnInspeccion -> "Inspección"
            else -> "Preventivo"
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tipo_servicio", tipoServicio)
            put("fecha", etFecha.text.toString())
            put("hora_inicio", etHoraInicio.text.toString())
            put("diagnostico", diagnostico)
            put("trabajo_realizado", trabajo)
            put("observaciones", etObservaciones.text.toString().trim())
            put("estado_equipo", spEstadoEquipo.text.toString())
            put("tiempo_empleado", spTiempoEmpleado.text.toString())
            put("tecnico", spTecnico.text.toString())
            put("estado_orden", "EN PROCESO")
        }

        val rowsUpdated = db.update("ordenes", values, "id = ?", arrayOf(ordenId.toString()))

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Mantenimiento guardado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar el mantenimiento", Toast.LENGTH_SHORT).show()
        }
    }
}