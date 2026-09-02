package com.example.climatrack.models

data class Equipo(
    val id: Int = 0,
    val codigo: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val serie: String,
    val cliente: String,
    val estado: String,
    val rutaImagen: String? = null // Para imágenes desde archivo local/URL
)