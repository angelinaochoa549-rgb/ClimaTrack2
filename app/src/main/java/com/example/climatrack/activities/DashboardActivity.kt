package com.example.climatrack.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.climatrack.R
import com.example.climatrack.database.DatabaseHelper
import com.example.climatrack.databinding.ActivityDashboardBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var dbHelper: DatabaseHelper
    private var notificacionesLeidas = false

    private var tempPhotoUri: Uri? = null

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            guardarFotoPerfilUri(uri)
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                guardarFotoPerfilUri(uri)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
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
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Manejo de Insets para Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.topBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Click en Avatar para cambiar foto de perfil
        binding.imgAvatar.setOnClickListener {
            mostrarOpcionesFotoPerfil()
        }
        binding.layoutAvatarContainer.setOnClickListener {
            mostrarOpcionesFotoPerfil()
        }
        cargarFotoPerfil()

        // Configuración de Menú y Campanita
        binding.btnMenu.setOnClickListener {
            mostrarMenuPrincipal()
        }

        binding.layoutNotificaciones.setOnClickListener {
            mostrarCentroNotificaciones()
        }

        binding.btnNotificaciones.setOnClickListener {
            mostrarCentroNotificaciones()
        }

        // Manejo de eventos en las tarjetas de accesos rápidos
        binding.cardOrdenes.setOnClickListener {
            startActivity(Intent(this, OrdenesActivity::class.java))
        }

        binding.cardEquipos.setOnClickListener {
            startActivity(Intent(this, EquiposActivity::class.java))
        }

        binding.cardHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        binding.cardCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Configuración de navegación inferior
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_ordenes -> {
                    startActivity(Intent(this, OrdenesActivity::class.java))
                    true
                }
                R.id.nav_equipos -> {
                    startActivity(Intent(this, EquiposActivity::class.java))
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    true
                }
                else -> false
            }
        }

        actualizarBadgeNotificaciones()
    }

    override fun onResume() {
        super.onResume()
        actualizarBadgeNotificaciones()
        cargarFotoPerfil()
    }

    private fun actualizarBadgeNotificaciones() {
        if (notificacionesLeidas) {
            binding.badgeNotificaciones.visibility = View.GONE
            return
        }

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ordenes WHERE estado IN ('PENDIENTE', 'EN PROCESO')", null)
        var contador = 0
        if (cursor.moveToFirst()) {
            contador = cursor.getInt(0)
        }
        cursor.close()

        if (contador > 0) {
            binding.badgeNotificaciones.text = contador.toString()
            binding.badgeNotificaciones.visibility = View.VISIBLE
        } else {
            binding.badgeNotificaciones.visibility = View.GONE
        }
    }

    private fun mostrarMenuPrincipal() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_menu_principal, null)
        dialog.setContentView(view)

        val imgMenuAvatar = view.findViewById<ImageView>(R.id.imgMenuAvatar)
        if (imgMenuAvatar != null) {
            cargarFotoPerfilEnMenu(imgMenuAvatar)
            imgMenuAvatar.setOnClickListener {
                dialog.dismiss()
                mostrarOpcionesFotoPerfil()
            }
        }

        view.findViewById<View>(R.id.btnCloseMenu)?.setOnClickListener { dialog.dismiss() }

        view.findViewById<View>(R.id.itemMenuOrdenes)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, OrdenesActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuEquipos)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, EquiposActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuNuevoEquipo)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, FormularioEquipoActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuNuevoCliente)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, FormularioClienteActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuHistorial)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuMapa)?.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UbicacionActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMenuPerfil)?.setOnClickListener {
            dialog.dismiss()
            mostrarPerfilTecnico()
        }

        view.findViewById<View>(R.id.itemMenuAcercaDe)?.setOnClickListener {
            dialog.dismiss()
            mostrarAcercaDe()
        }

        view.findViewById<View>(R.id.itemMenuCerrarSesion)?.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    private fun mostrarCentroNotificaciones() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_notificaciones, null)
        dialog.setContentView(view)

        val container = view.findViewById<LinearLayout>(R.id.containerNotificaciones)
        val tvSubtitulo = view.findViewById<TextView>(R.id.tvSubtituloNotif)
        val btnLimpiar = view.findViewById<View>(R.id.btnLimpiarNotificaciones)
        val btnClose = view.findViewById<View>(R.id.btnCloseNotif)

        btnClose?.setOnClickListener { dialog.dismiss() }

        val db = dbHelper.readableDatabase
        val query = """
            SELECT o.id, o.numero, o.estado, c.nombre, e.modelo, o.tipo_servicio
            FROM ordenes o
            JOIN clientes c ON o.cliente_id = c.id
            JOIN equipos e ON o.equipo_id = e.id
            ORDER BY o.id ASC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        var totalNotif = 0

        if (cursor.moveToFirst()) {
            do {
                val ordenId = cursor.getInt(0)
                val numOrden = cursor.getString(1)
                val estado = cursor.getString(2)
                val cliente = cursor.getString(3)
                val equipo = cursor.getString(4)
                val tipoServicio = cursor.getString(5)

                val notifItem = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
                    isClickable = true
                    isFocusable = true
                    setBackgroundResource(android.R.drawable.list_selector_background)
                }

                val rowHeader = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val iconNotif = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(28.dpToPx(), 28.dpToPx()).apply { marginEnd = 12.dpToPx() }
                    setImageResource(if (estado == "PENDIENTE") R.drawable.ic_clock else R.drawable.ic_check_circle)
                    setColorFilter(if (estado == "PENDIENTE") Color.parseColor("#FF9800") else Color.parseColor("#4CAF50"))
                }

                val infoLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvTitulo = TextView(this).apply {
                    text = "Orden $numOrden ($tipoServicio)"
                    setTextColor(Color.parseColor("#102A43"))
                    textSize = 14f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }

                val tvDesc = TextView(this).apply {
                    text = "Cliente: $cliente • Equipo: $equipo\nEstado: $estado"
                    setTextColor(Color.parseColor("#627D98"))
                    textSize = 12f
                }

                infoLayout.addView(tvTitulo)
                infoLayout.addView(tvDesc)

                rowHeader.addView(iconNotif)
                rowHeader.addView(infoLayout)

                notifItem.addView(rowHeader)

                notifItem.setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(this, DetalleOrdenActivity::class.java)
                    intent.putExtra("ORDEN_ID", ordenId)
                    startActivity(intent)
                }

                container?.addView(notifItem)

                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1.dpToPx()).apply {
                        setMargins(0, 8.dpToPx(), 0, 8.dpToPx())
                    }
                    setBackgroundColor(Color.parseColor("#E4E7EB"))
                }
                container?.addView(divider)

                totalNotif++
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Notificación del sistema GPS / Mapas
        val sysNotifItem = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, 16.dpToPx(), 0, 16.dpToPx())
            isClickable = true
            isFocusable = true
            setBackgroundResource(android.R.drawable.list_selector_background)
        }
        val iconSys = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(28.dpToPx(), 28.dpToPx()).apply { marginEnd = 12.dpToPx() }
            setImageResource(R.drawable.ic_puntero)
            setColorFilter(Color.parseColor("#0052CC"))
        }
        val sysInfo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val tvSysTitle = TextView(this).apply {
            text = "Sistema GPS y Mapas Offline"
            setTextColor(Color.parseColor("#102A43"))
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val tvSysDesc = TextView(this).apply {
            text = "Localización OSMdroid sincronizada correctamente"
            setTextColor(Color.parseColor("#627D98"))
            textSize = 12f
        }
        sysInfo.addView(tvSysTitle)
        sysInfo.addView(tvSysDesc)
        sysNotifItem.addView(iconSys)
        sysNotifItem.addView(sysInfo)

        sysNotifItem.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, UbicacionActivity::class.java))
        }
        container?.addView(sysNotifItem)

        tvSubtitulo?.text = "$totalNotif órdenes asignadas a tu cuenta"

        btnLimpiar?.setOnClickListener {
            notificacionesLeidas = true
            binding.badgeNotificaciones.visibility = View.GONE
            Toast.makeText(this, "Notificaciones marcadas como leídas", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun verificarPermisoYCamara() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        try {
            val file = File(cacheDir, "temp_profile_photo.jpg")
            tempPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            tempPhotoUri?.let { uri ->
                takePictureLauncher.launch(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        galeriaLauncher.launch("image/*")
    }

    private fun guardarFotoPerfilUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val destFile = File(filesDir, "profile_avatar.jpg")
            val outputStream = FileOutputStream(destFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val prefs = getSharedPreferences("climatrack_prefs", MODE_PRIVATE)
            prefs.edit().putString("profile_image_path", destFile.absolutePath).apply()

            cargarFotoPerfil()
            Toast.makeText(this, "¡Foto de perfil actualizada con éxito!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al guardar la foto de perfil", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restablecerFotoPerfilDefault() {
        try {
            val destFile = File(filesDir, "profile_avatar.jpg")
            if (destFile.exists()) {
                destFile.delete()
            }
            val prefs = getSharedPreferences("climatrack_prefs", MODE_PRIVATE)
            prefs.edit().remove("profile_image_path").apply()

            cargarFotoPerfil()
            Toast.makeText(this, "Foto de perfil restablecida", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cargarFotoPerfil() {
        val prefs = getSharedPreferences("climatrack_prefs", MODE_PRIVATE)
        val path = prefs.getString("profile_image_path", null)

        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    binding.imgAvatar.setImageBitmap(bitmap)
                    return
                }
            }
        }
        binding.imgAvatar.setImageResource(R.drawable.avatar_tec)
    }

    private fun cargarFotoPerfilEnMenu(imageView: ImageView) {
        val prefs = getSharedPreferences("climatrack_prefs", MODE_PRIVATE)
        val path = prefs.getString("profile_image_path", null)

        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    return
                }
            }
        }
        imageView.setImageResource(R.drawable.avatar_tec)
    }

    private fun mostrarOpcionesFotoPerfil() {
        val opciones = arrayOf(
            "📸 Tomar foto con la cámara",
            "🖼️ Elegir de la galería",
            "🔄 Restablecer foto predeterminada"
        )

        AlertDialog.Builder(this)
            .setTitle("Cambiar Foto de Perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> verificarPermisoYCamara()
                    1 -> abrirGaleria()
                    2 -> restablecerFotoPerfilDefault()
                }
            }
            .setNegativeButton("CANCELAR", null)
            .show()
    }

    private fun mostrarPerfilTecnico() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_perfil_tecnico, null)
        val imgPerfil = dialogView.findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.imgDialogPerfil)
        val btnCambiarFoto = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCambiarFoto)

        if (imgPerfil != null) {
            cargarFotoPerfilEnMenu(imgPerfil)
            imgPerfil.setOnClickListener {
                mostrarOpcionesFotoPerfil()
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        btnCambiarFoto?.setOnClickListener {
            dialog.dismiss()
            mostrarOpcionesFotoPerfil()
        }

        dialogView.findViewById<View>(R.id.btnCerrarPerfil)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarAcercaDe() {
        AlertDialog.Builder(this)
            .setTitle("ClimaTrack Pro")
            .setMessage("📱 Versión: 1.0.0\n🗄️ Base de datos: SQLite v5\n🗺️ Mapas: OpenStreetMap (OSMdroid)\n\nSistema integral para la gestión de mantenimiento de aire acondicionado y climatización.")
            .setPositiveButton("CERRAR", null)
            .show()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}