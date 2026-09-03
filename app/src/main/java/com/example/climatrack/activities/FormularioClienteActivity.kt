package com.example.climatrack.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class FormularioClienteActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cliente)

        dbHelper = DatabaseHelper(this)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btnGuardarCliente).setOnClickListener {
            guardarCliente()
        }
    }

    private fun guardarCliente() {
        val nombre = findViewById<TextInputEditText>(R.id.etNombreCliente).text.toString().trim()
        val telefono = findViewById<TextInputEditText>(R.id.etTelefonoCliente).text.toString().trim()
        val direccion = findViewById<TextInputEditText>(R.id.etDireccionCliente).text.toString().trim()
        val email = findViewById<TextInputEditText>(R.id.etEmailCliente).text.toString().trim()

        if (nombre.isEmpty() || telefono.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios (*)", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = dbHelper.insertarCliente(nombre, telefono, direccion, email)

        if (resultado != -1L) {
            Toast.makeText(this, "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el cliente", Toast.LENGTH_SHORT).show()
        }
    }
}