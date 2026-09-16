package com.example.climatrack.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Evento al hacer clic en el botón de ingresar
        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val contrasena = binding.etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Validar credenciales contra la base de datos SQLite
                val usuarioObtenido = dbHelper.validarUsuario(usuario, contrasena)

                if (usuarioObtenido != null) {
                    Toast.makeText(
                        this,
                        "¡Bienvenido ${usuarioObtenido.nombre}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Ir a la pantalla principal (Dashboard)
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Usuario o contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Evento para recuperación de contraseña
        binding.tvOlvidaste.setOnClickListener {
            Toast.makeText(
                this,
                "Por favor contacta al administrador del sistema para restablecer tu contraseña",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}