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
            mostrarDialogoRecuperarPassword()
        }
    }

    private fun mostrarDialogoRecuperarPassword() {
        val dialogBinding = com.example.climatrack.databinding.DialogRecuperarPasswordBinding.inflate(layoutInflater)

        // Pre-llenar usuario si ya ingresó alguno en la pantalla de Login
        val usuarioActual = binding.etUsuario.text.toString().trim()
        if (usuarioActual.isNotEmpty()) {
            dialogBinding.etResetUsuario.setText(usuarioActual)
        }

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        dialogBinding.btnCancelarReset.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnRestablecer.setOnClickListener {
            val usuario = dialogBinding.etResetUsuario.text.toString().trim()
            val nuevaPassword = dialogBinding.etResetNuevaPassword.text.toString().trim()
            val confirmarPassword = dialogBinding.etResetConfirmarPassword.text.toString().trim()

            // Limpiar errores previos
            dialogBinding.tilResetUsuario.error = null
            dialogBinding.tilResetNuevaPassword.error = null
            dialogBinding.tilResetConfirmarPassword.error = null

            var esValido = true

            if (usuario.isEmpty()) {
                dialogBinding.tilResetUsuario.error = "Ingresa tu usuario"
                esValido = false
            }

            if (nuevaPassword.isEmpty()) {
                dialogBinding.tilResetNuevaPassword.error = "Ingresa la nueva contraseña"
                esValido = false
            } else if (nuevaPassword.length < 4) {
                dialogBinding.tilResetNuevaPassword.error = "La contraseña debe tener al menos 4 caracteres"
                esValido = false
            }

            if (confirmarPassword.isEmpty()) {
                dialogBinding.tilResetConfirmarPassword.error = "Confirma la nueva contraseña"
                esValido = false
            } else if (nuevaPassword != confirmarPassword) {
                dialogBinding.tilResetConfirmarPassword.error = "Las contraseñas no coinciden"
                esValido = false
            }

            if (!esValido) return@setOnClickListener

            // Verificar si el usuario existe en la base de datos
            if (!dbHelper.existeUsuario(usuario)) {
                dialogBinding.tilResetUsuario.error = "El usuario no se encuentra registrado"
                Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar la contraseña en la base de datos
            val exito = dbHelper.actualizarPassword(usuario, nuevaPassword)
            if (exito) {
                Toast.makeText(
                    this,
                    "¡Contraseña actualizada con éxito! Ahora puedes ingresar.",
                    Toast.LENGTH_LONG
                ).show()

                binding.etUsuario.setText(usuario)
                binding.etContrasena.setText("")

                dialog.dismiss()
            } else {
                Toast.makeText(
                    this,
                    "Error al actualizar la contraseña. Inténtalo de nuevo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.show()
    }
}