package com.example.climatrack.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper

class FormularioClienteActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_cliente)

        dbHelper = DatabaseHelper(this)

        // Botón de flecha atrás en el header personalizado
        findViewById<ImageButton>(R.id.btnVolver).setOnClickListener {
            finish()
        }

        // Botón contenedor de "GUARDAR CLIENTE"
        findViewById<RelativeLayout>(R.id.btnGuardarCliente).setOnClickListener {
            guardarCliente()
        }
    }

    private fun guardarCliente() {
        val nombre = findViewById<EditText>(R.id.etNombreCliente).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.etTelefono).text.toString().trim()
        val direccion = findViewById<EditText>(R.id.etDireccion).text.toString().trim()
        val email = findViewById<EditText>(R.id.etCorreo).text.toString().trim()

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