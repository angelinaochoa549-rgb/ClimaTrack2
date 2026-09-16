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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMantenimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

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

        // Validación según Módulo 4 de la guía
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Lógica para registrar en la base de datos local SQLite
        Toast.makeText(this, "Mantenimiento registrado correctamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}