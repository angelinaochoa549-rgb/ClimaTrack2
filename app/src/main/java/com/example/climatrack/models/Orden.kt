package com.example.climatrack.models

data class Orden(
    val id: Int,
    val numero: String,
    val fecha: String,
    val clienteNombre: String,
    val equipoNombre: String,
    val tipoServicio: String,
    val descripcion: String,
    val estado: String
)