package com.example.climatrack.models

import android.net.Uri

data class Evidencia(
    val titulo: String,
    val fecha: String,
    val imageUri: Uri,
    val imagenResId: Int? = null
)