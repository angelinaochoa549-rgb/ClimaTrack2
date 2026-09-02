package com.example.climatrack.models

data class Equipo(
    val id: String,
    val codigo: String,
    val tipo: String,
    val marca: String,
    val modelo: String,
    val serie: String,
    val cliente: String,
    val estado: String, // "OPERATIVO", "EN MANTENIMIENTO", "FUERA DE SERVICIO"
    val imagenResId: Int
)