package com.example.climatrack.models

data class Usuario(
    val id: Int = 0,
    val usuario: String,
    val password: String,
    val nombre: String,
    val rol: String
)