package com.example.climatrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UbicacionActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var map: MapView
    private var ordenId: Int = -1

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    // Vistas de la interfaz
    private lateinit var txtLatitud: TextView
    private lateinit var txtLongitud: TextView
    private lateinit var txtDireccion: TextView
    private lateinit var txtFecha: TextView
    private lateinit var txtMensaje: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if ((permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
            (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true)) {
            obtenerUbicacionActual()
        } else {
            txtMensaje.text = "ⓘ Permiso de ubicación denegado"
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración obligatoria para osmdroid (User Agent)
        Configuration.getInstance().userAgentValue = "ClimaTrackApp/1.0 (" + applicationContext.packageName + ")"
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContentView(R.layout.activity_geolocalizacion)

        dbHelper = DatabaseHelper(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        ordenId = intent.getIntExtra("ORDEN_ID", -1)

        // Vincular Vistas
        txtLatitud = findViewById(R.id.txtLatitud)
        txtLongitud = findViewById(R.id.txtLongitud)
        txtDireccion = findViewById(R.id.txtDireccion)
        txtFecha = findViewById(R.id.txtFecha)
        txtMensaje = findViewById(R.id.txtMensaje)

        // Inicializar Mapa OSM
        map = findViewById(R.id.mapaOsm)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(16.0)

        // Eventos de botones
        findViewById<TextView>(R.id.btnRegresar)?.setOnClickListener { finish() }

        findViewById<TextView>(R.id.btnActualizar).setOnClickListener {
            verificarPermisosYObtenerUbicacion()
        }

        findViewById<TextView>(R.id.btnGuardar).setOnClickListener {
            guardarUbicacionEnBD()
        }

        // Zoom +/-
        findViewById<TextView>(R.id.btnZoomIn).setOnClickListener {
            map.controller.zoomIn()
        }

        findViewById<TextView>(R.id.btnZoomOut).setOnClickListener {
            map.controller.zoomOut()
        }

        // Botón abrir en app externa (Google Maps / Navegador)
        findViewById<MaterialButton>(R.id.btnVerMapa).setOnClickListener {
            if (latitud != 0.0 && longitud != 0.0) {
                val gmmIntentUri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud(Ubicación Servicio)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=$latitud,$longitud"))
                    startActivity(browserIntent)
                }
            } else {
                Toast.makeText(this, "Obtén la ubicación primero", Toast.LENGTH_SHORT).show()
            }
        }

        verificarPermisosYObtenerUbicacion()
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
                    // Coordenadas mock de prueba si el GPS está apagado o no da señal inmediata
                    latitud = 10.9878
                    longitud = -74.7889
                    Toast.makeText(this, "Usando ubicación inicial aproximada", Toast.LENGTH_SHORT).show()
                }
                actualizarUI()
                actualizarMapa()
                obtenerDireccionTexto(latitud, longitud)
            }.addOnFailureListener {
                txtMensaje.text = "ⓘ Error al solicitar ubicación"
            }
        } catch (e: SecurityException) {
            txtMensaje.text = "ⓘ Error de permisos"
        }
    }

    private fun actualizarUI() {
        txtLatitud.text = String.format(Locale.US, "%.6f", latitud)
        txtLongitud.text = String.format(Locale.US, "%.6f", longitud)
        txtFecha.text = SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault()).format(Date())
        txtMensaje.text = "✓ Ubicación obtenida correctamente"
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

    private fun obtenerDireccionTexto(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0)
                        runOnUiThread { txtDireccion.text = address }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    txtDireccion.text = addresses[0].getAddressLine(0)
                }
            }
        } catch (e: Exception) {
            txtDireccion.text = "Dirección no disponible"
        }
    }

    private fun guardarUbicacionEnBD() {
        if (latitud == 0.0 && longitud == 0.0) {
            Toast.makeText(this, "Primero debes obtener una ubicación válida", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val resultado = dbHelper.guardarUbicacion(ordenId, latitud, longitud, fechaActual)

        if (resultado != -1L) {
            Toast.makeText(this, "Ubicación guardada con éxito", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
        }
    }
}