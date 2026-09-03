package com.example.climatrack.models

data class Cliente(
    val id: Int = 0,
    val nombre: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val email: String? = null
)