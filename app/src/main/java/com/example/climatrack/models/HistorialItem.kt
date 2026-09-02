package com.example.climatrack.models

data class HistorialItem(
    val mantenimientoId: Int,
    val ordenNumero: String,
    val fecha: String,
    val clienteNombre: String,
    val equipoModelo: String,
    val trabajoRealizado: String,
    val tipoServicio: String
)