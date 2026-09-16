package com.example.climatrack.models

import android.net.Uri

data class Evidencia(
    val titulo: String,
    val fecha: String,
    val imagenResId: Int? = null // O la URL de la foto
)