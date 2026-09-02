package com.example.climatrack.models

data class Repuesto(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val unidad: String,
    val cantidad: Int = 0
)