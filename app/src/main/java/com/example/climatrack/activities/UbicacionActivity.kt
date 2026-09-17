package com.example.climatrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.climatrack.databinding.ActivityGeolocalizacionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UbicacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeolocalizacionBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitudActual: Double = 0.0
    private var longitudActual: Double = 0.0
    private var miMarcador: Marker? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Cargar configuración de OSMdroid antes de inflar la vista
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityGeolocalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupMapaOSM()
        setupListeners()
        obtenerUbicacionActual()
    }

    private fun setupMapaOSM() {
        binding.mapaOsm.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapaOsm.setMultiTouchControls(true)
        binding.mapaOsm.controller.setZoom(17.0)
    }

    private fun setupListeners() {
        // Regresar
        binding.btnRegresar.setOnClickListener {
            finish()
        }

        // Zoom In (+)
        binding.btnZoomIn.setOnClickListener {
            binding.mapaOsm.controller.zoomIn()
        }

        // Zoom Out (-)
        binding.btnZoomOut.setOnClickListener {
            binding.mapaOsm.controller.zoomOut()
        }

        // Ver en mapa externo (Google Maps / Waze)
        binding.btnVerMapa.setOnClickListener {
            if (latitudActual != 0.0 && longitudActual != 0.0) {
                val uri = Uri.parse("geo:$latitudActual,$longitudActual?q=$latitudActual,$longitudActual(Ubicación+Técnico)")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "Aún no se han obtenido las coordenadas", Toast.LENGTH_SHORT).show()
            }
        }

        // Actualizar GPS
        binding.btnActualizar.setOnClickListener {
            binding.txtMensaje.text = "ⓘ   Buscando señal GPS..."
            obtenerUbicacionActual()
        }

        // Guardar Ubicación
        binding.btnGuardar.setOnClickListener {
            if (latitudActual != 0.0 && longitudActual != 0.0) {
                Toast.makeText(this, "Ubicación guardada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Esperando señal de GPS válida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerUbicacionActual() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitudActual = location.latitude
                longitudActual = location.longitude
                actualizarInterfazYUbicacion(latitudActual, longitudActual)
            } else {
                binding.txtMensaje.text = "ⓘ   No se pudo obtener la ubicación. Activa el GPS."
            }
        }.addOnFailureListener {
            binding.txtMensaje.text = "ⓘ   Error al intentar acceder al GPS"
        }
    }

    private fun actualizarInterfazYUbicacion(lat: Double, lng: Double) {
        // Mostrar coordenadas
        binding.txtLatitud.text = String.format(Locale.US, "%.6f", lat)
        binding.txtLongitud.text = String.format(Locale.US, "%.6f", lng)

        // Mostrar fecha y hora
        val sdf = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
        binding.txtFecha.text = sdf.format(Date())

        // Obtener dirección aproximada
        obtenerDireccionTexto(lat, lng)

        // Centrar mapa y mover Pin
        val puntoGps = GeoPoint(lat, lng)
        binding.mapaOsm.controller.setCenter(puntoGps)

        if (miMarcador == null) {
            miMarcador = Marker(binding.mapaOsm).apply {
                title = "Ubicación del Técnico"
                anchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            binding.mapaOsm.overlays.add(miMarcador)
        }
        miMarcador?.position = puntoGps
        binding.mapaOsm.invalidate()

        binding.txtMensaje.text = "✓   Ubicación GPS fijada correctamente"
    }

    private fun obtenerDireccionTexto(lat: Double, lng: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val direccion = addresses[0].getAddressLine(0) ?: "Dirección no identificada"
                        runOnUiThread { binding.txtDireccion.text = direccion }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    binding.txtDireccion.text = addresses[0].getAddressLine(0)
                } else {
                    binding.txtDireccion.text = "Dirección no identificada"
                }
            }
        } catch (e: Exception) {
            binding.txtDireccion.text = "Dirección no disponible"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapaOsm.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapaOsm.onPause()
    }
}