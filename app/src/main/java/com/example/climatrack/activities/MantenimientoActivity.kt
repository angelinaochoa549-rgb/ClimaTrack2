package com.example.climatrack.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityMantenimientoBinding
import java.util.Calendar

class MantenimientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMantenimientoBinding
    private lateinit var dbHelper: DatabaseHelper
    private var ordenId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMantenimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        configurarSpinners()
        configurarDatePicker()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnGuardar.setOnClickListener {
            guardarMantenimiento()
        }
    }

    private fun configurarSpinners() {
        // Tipos de servicio según guía: PREVENTIVO, CORRECTIVO, ASESORÍA, INSPECCIÓN
        val tipos = arrayOf("Preventivo", "Correctivo", "Asesoría", "Inspección")
        binding.spTipoMantenimiento.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos)

        // Estados de equipo según guía: OPERATIVO, EN MANTENIMIENTO, FUERA DE SERVICIO
        val estados = arrayOf("Operativo", "En Mantenimiento", "Fuera de Servicio")
        binding.spEstadoEquipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)

        // Equipos de prueba
        val equipos = arrayOf("Equipo split inverter 24K - EQ-00015", "Cassette 36K - EQ-00016")
        binding.spEquipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, equipos)

        // Técnicos
        val tecnicos = arrayOf("Técnico 01", "Técnico 02")
        binding.spTecnico.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tecnicos)
    }

    private fun configurarDatePicker() {
        val calendar = Calendar.getInstance()
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val fecha = "$day/${month + 1}/$year"
            binding.etFechaMantenimiento.setText(fecha)
            binding.etFechaFin.setText(fecha)
        }

        binding.etFechaMantenimiento.setOnClickListener {
            DatePickerDialog(this, dateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun guardarMantenimiento() {
        val descripcion = binding.etDescripcion.text.toString().trim()
        val fecha = binding.etFechaMantenimiento.text.toString().trim().ifEmpty { "18/08/2026" }
        val tecnico = binding.spTecnico.selectedItem?.toString() ?: "Técnico 01"
        val tipoMantenimiento = binding.spTipoMantenimiento.selectedItem?.toString() ?: "Preventivo"
        val estadoEquipo = binding.spEstadoEquipo.selectedItem?.toString() ?: "Operativo"

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete la descripción del mantenimiento", Toast.LENGTH_SHORT).show()
            return
        }

        val db = dbHelper.writableDatabase
        try {
            db.beginTransaction()

            // 1. Insertar mantenimiento
            val values = android.content.ContentValues().apply {
                put("orden_id", if (ordenId != -1) ordenId else 1)
                put("fecha", fecha)
                put("diagnostico", "Diagnóstico general - $tipoMantenimiento")
                put("trabajo_realizado", descripcion)
                put("observaciones", "Estado del equipo: $estadoEquipo")
                put("recomendaciones", "Monitorear funcionamiento")
                put("tiempo_empleado", "2 horas")
                put("tecnico_nombre", tecnico)
            }
            db.insert("mantenimientos", null, values)

            // 2. Actualizar estado de la orden a "EN PROCESO"
            if (ordenId != -1) {
                val orderValues = android.content.ContentValues().apply {
                    put("estado", "EN PROCESO")
                    put("tipo_servicio", tipoMantenimiento.uppercase())
                }
                db.update("ordenes", orderValues, "id = ?", arrayOf(ordenId.toString()))
            }

            db.setTransactionSuccessful()
            Toast.makeText(this, "Mantenimiento registrado y orden en proceso", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
        }
    }
}