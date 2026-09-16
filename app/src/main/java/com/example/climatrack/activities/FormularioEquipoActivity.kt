package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class FormularioEquipoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_equipo)

        dbHelper = DatabaseHelper(this)

        findViewById<View>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        setupSpinners()

        findViewById<MaterialButton>(R.id.btnGuardarEquipo).setOnClickListener {
            guardarEquipo()
        }
    }

    private fun setupSpinners() {
        val tipos = arrayOf("Split Pared", "Mini Split", "Cassette", "Chiller", "Paquete")
        val adapterTipos = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipos)
        findViewById<AutoCompleteTextView>(R.id.etTipoEquipo).setAdapter(adapterTipos)

        val listaClientes = dbHelper.getClientes()
        val nombres = listaClientes.map { it.nombre }.toMutableList()
        nombres.add("+ Agregar nuevo cliente...")
        
        val adapterClientes = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nombres)
        val spCliente = findViewById<AutoCompleteTextView>(R.id.etCliente)
        spCliente.setAdapter(adapterClientes)
        
        spCliente.setOnItemClickListener { _, _, position, _ ->
            if (nombres[position] == "+ Agregar nuevo cliente...") {
                spCliente.setText("")
                startActivity(Intent(this, FormularioClienteActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupSpinners() // Recargar clientes por si se agregó uno nuevo
    }

    private fun guardarEquipo() {
        val codigo = findViewById<android.widget.EditText>(R.id.etCodigoEquipo).text.toString().trim()
        val tipo = findViewById<AutoCompleteTextView>(R.id.etTipoEquipo).text.toString()
        val marca = findViewById<android.widget.EditText>(R.id.etMarca).text.toString().trim()
        val modelo = findViewById<android.widget.EditText>(R.id.etModelo).text.toString().trim()
        val serie = findViewById<android.widget.EditText>(R.id.etNumeroSerie).text.toString().trim()
        val capacidad = findViewById<android.widget.EditText>(R.id.etCapacidad).text.toString().trim()
        val clienteNombre = findViewById<AutoCompleteTextView>(R.id.etCliente).text.toString()

        if (codigo.isEmpty() || tipo.isEmpty() || marca.isEmpty() || modelo.isEmpty() || serie.isEmpty() || clienteNombre.isEmpty() || clienteNombre == "+ Agregar nuevo cliente...") {
            Toast.makeText(this, "Por favor complete los campos obligatorios (*)", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener ID real del cliente seleccionado
        val listaClientes = dbHelper.getClientes()
        val clienteSeleccionado = listaClientes.find { it.nombre == clienteNombre }
        val clienteId = clienteSeleccionado?.id ?: 1

        val resultado = dbHelper.insertarEquipo(
            codigo, tipo, marca, modelo, serie, capacidad, "Ubicación por defecto", clienteId, "OPERATIVO"
        )

        if (resultado != -1L) {
            Toast.makeText(this, "Equipo registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el equipo", Toast.LENGTH_SHORT).show()
        }
    }
}