package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvError = findViewById<TextView>(R.id.tvError)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val tvOlvidePassword = findViewById<TextView>(R.id.tvOlvidePassword)

        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validación de campos vacíos
            if (usuario.isEmpty() || password.isEmpty()) {
                tvError.text = getString(R.string.error_incompleto)
                tvError.visibility = TextView.VISIBLE
                return@setOnClickListener
            }

            // Validar contra SQLite
            val usuarioValido = dbHelper.validarUsuario(usuario, password)

            if (usuarioValido != null) {
                tvError.visibility = TextView.GONE

                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("NOMBRE_TECNICO", usuarioValido.nombre)
                startActivity(intent)
                finish()
            } else {
                tvError.text = getString(R.string.error_incorrecto)
                tvError.visibility = TextView.VISIBLE
            }
        }

        tvOlvidePassword.setOnClickListener {
            Toast.makeText(this, "Funcionalidad próximamente disponible", Toast.LENGTH_SHORT).show()
        }
    }
}