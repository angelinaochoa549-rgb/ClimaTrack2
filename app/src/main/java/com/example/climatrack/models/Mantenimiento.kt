package com.example.climatrack.models

data class Mantenimiento(
    val id: String,
    val orden: String,
    val fecha: String,
    val hora: String,
    val tecnico: String,
    val tipo: String, // "PREVENTIVO", "CORRECTIVO", "INSPECCIÓN"
    val descripcion: String
)