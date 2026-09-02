package com.example.climatrack.models

data class Repuesto(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val unidad: String,
    val cantidad: Int
) {
    val precioUnitario: Double
        get() = when (codigo.uppercase().trim()) {
            "RPT-001", "RPT-0007" -> 25000.0
            "RPT-002", "RPT-0012" -> 18000.0
            "RPT-0021" -> 45000.0
            "RPT-0030" -> 60000.0
            else -> 20000.0
        }

    val total: Double
        get() = cantidad * precioUnitario
}