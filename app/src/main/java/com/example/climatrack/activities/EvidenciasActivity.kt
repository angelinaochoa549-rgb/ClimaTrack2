package com.example.climatrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.climatrack.adapters.EvidenciaAdapter
import com.example.climatrack.databinding.ActivityEvidenciasBinding
import com.example.climatrack.models.Evidencia
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EvidenciasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEvidenciasBinding

    // Lista 100% vacía al inicio
    private val listaEvidencias = mutableListOf<Evidencia>()
    private lateinit var adapter: EvidenciaAdapter
    private var tempPhotoUri: Uri? = null

    private val titulosFrecuentes = listOf(
        "Filtro antes", "Filtro después",
        "Conexiones eléctricas", "Presión del sistema",
        "Unidad interior", "Unidad exterior"
    )

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exitoso ->
        if (exitoso && tempPhotoUri != null) {
            val fechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val indiceTitulo = listaEvidencias.size % titulosFrecuentes.size
            val tituloSugerido = titulosFrecuentes[indiceTitulo]

            val nuevaEvidencia = Evidencia(
                titulo = tituloSugerido,
                fecha = fechaHora,
                imageUri = tempPhotoUri!!
            )

            // Agrega solo la foto recién tomada a la lista
            adapter.agregarEvidencia(nuevaEvidencia)
            binding.rvEvidencias.smoothScrollToPosition(listaEvidencias.size - 1)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            abrirCamara()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvidenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener { finish() }

        setupRecyclerView()

        binding.btnTomarFoto.setOnClickListener { verificarPermisosYCamara() }
        binding.btnMasHeader.setOnClickListener { verificarPermisosYCamara() }
    }

    private fun setupRecyclerView() {
        // Inicializa el adaptador con la lista vacía
        adapter = EvidenciaAdapter(listaEvidencias) { evidencia ->
            adapter.eliminarEvidencia(evidencia)
        }
        binding.rvEvidencias.layoutManager = GridLayoutManager(this, 2)
        binding.rvEvidencias.adapter = adapter
    }

    private fun verificarPermisosYCamara() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        val file = crearArchivoImagen()
        tempPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        // Concede permisos explícitos de lectura sobre la Uri
        grantUriPermission(
            "com.android.camera",
            tempPhotoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        takePictureLauncher.launch(tempPhotoUri)
    }

    private fun crearArchivoImagen(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null) // Guarda en almacenamiento externo de la App para lectura continua
        return File.createTempFile("EVIDENCIA_${timeStamp}_", ".jpg", storageDir)
    }
}