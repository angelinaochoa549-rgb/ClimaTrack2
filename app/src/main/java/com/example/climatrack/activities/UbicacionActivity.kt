package com.example.climatrack.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class UbicacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: MapView
    private var ordenId: Int = -1

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if ((permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
            (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)) {
            obtenerUbicacionActual()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Importante para osmdroid: Cargar configuración
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        
        setContentView(R.layout.activity_ubicacion)

        dbHelper = DatabaseHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        if (ordenId == -1) {
            finish()
            return
        }

        // Inicializar Mapa OSM
        map = findViewById(R.id.mapaOsm)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        val mapController = map.controller
        mapController.setZoom(15.0)

        val btnActualizar = findViewById<Button>(R.id.btnActualizarUbicacion)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarUbicacion)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarUbicacion).setNavigationOnClickListener {
            finish()
        }

        btnActualizar.setOnClickListener {
            verificarPermisosYObtenerUbicacion()
        }

        btnGuardar.setOnClickListener {
            guardarUbicacionEnBD()
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun verificarPermisosYObtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            obtenerUbicacionActual()
        }
    }

    private fun obtenerUbicacionActual() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    latitud = location.latitude
                    longitud = location.longitude
                } else {
                    // Mock para prototipo
                    Toast.makeText(this, "No se pudo obtener ubicación real, usando mock...", Toast.LENGTH_SHORT).show()
                    latitud = 10.9878
                    longitud = -74.7889
                }
                actualizarUI()
                actualizarMapa()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error de seguridad: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarUI() {
        findViewById<TextView>(R.id.tvLatitud).text = latitud.toString()
        findViewById<TextView>(R.id.tvLongitud).text = longitud.toString()
        findViewById<TextView>(R.id.tvFechaHora).text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        findViewById<Button>(R.id.btnGuardarUbicacion).isEnabled = true
    }

    private fun actualizarMapa() {
        val startPoint = GeoPoint(latitud, longitud)
        map.controller.setCenter(startPoint)
        
        map.overlays.clear()
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Ubicación del Servicio"
        map.overlays.add(startMarker)
        map.invalidate()
    }

    private fun guardarUbicacionEnBD() {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("orden_id", ordenId)
            put("latitud", latitud)
            put("longitud", longitud)
            put("fecha", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()))
        }
        db.insert("ubicaciones", null, values)
        Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show()
        finish()
    }
}