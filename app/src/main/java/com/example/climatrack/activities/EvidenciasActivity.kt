package com.example.climatrack.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.climatrack.R
import com.example.climatrack.adapters.EvidenciaAdapter
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.models.Evidencia
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EvidenciasActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: EvidenciaAdapter
    private lateinit var rvEvidencias: RecyclerView
    private var ordenId: Int = -1
    private var currentPhotoPath: String = ""

    private val takePicture = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            guardarEvidenciaEnBD(currentPhotoPath)
            cargarEvidencias()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evidencias)

        dbHelper = DatabaseHelper(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            finish()
            return
        }

        rvEvidencias = findViewById(R.id.rvEvidencias)
        val btnTomarFoto = findViewById<Button>(R.id.btnTomarFoto)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            finish()
        }

        rvEvidencias.layoutManager = GridLayoutManager(this, 2)
        adapter = EvidenciaAdapter(emptyList()) { evidencia ->
            eliminarEvidencia(evidencia)
        }
        rvEvidencias.adapter = adapter

        btnTomarFoto.setOnClickListener {
            despacharIntentCamara()
        }

        cargarEvidencias()
    }

    private fun cargarEvidencias() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, orden_id, ruta_foto, fecha FROM evidencias WHERE orden_id = ?",
            arrayOf(ordenId.toString())
        )
        val lista = mutableListOf<Evidencia>()
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Evidencia(
                        id = cursor.getInt(0),
                        ordenId = cursor.getInt(1),
                        rutaFoto = cursor.getString(2),
                        fecha = cursor.getString(3)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        adapter.updateList(lista)
    }

    private fun despacharIntentCamara() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    crearArchivoImagen()
                } catch (ex: Exception) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePicture.launch(takePictureIntent)
                }
            } ?: run {
                // Si no hay app de cámara, mockeamos una entrada para el prototipo
                Toast.makeText(this, "Cámara no disponible, simulando captura...", Toast.LENGTH_SHORT).show()
                guardarEvidenciaEnBD("mock_path_${System.currentTimeMillis()}.jpg")
                cargarEvidencias()
            }
        }
    }

    private fun crearArchivoImagen(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun guardarEvidenciaEnBD(path: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("orden_id", ordenId)
            put("ruta_foto", path)
            put("fecha", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
        }
        db.insert("evidencias", null, values)
    }

    private fun eliminarEvidencia(evidencia: Evidencia) {
        val db = dbHelper.writableDatabase
        db.delete("evidencias", "id = ?", arrayOf(evidencia.id.toString()))
        cargarEvidencias()
        Toast.makeText(this, "Evidencia eliminada", Toast.LENGTH_SHORT).show()
    }
}