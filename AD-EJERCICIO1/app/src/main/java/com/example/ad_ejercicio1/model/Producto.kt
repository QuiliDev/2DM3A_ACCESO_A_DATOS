package com.example.ad_ejercicio1.model

data class Producto(
    val codigo: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0
) {
    fun toMap() = mapOf(
        "codigo" to codigo.trim(),
        "descripcion" to descripcion.trim(),
        "precio" to precio
    )

    companion object {
        fun fromMap(data: Map<String, Any?>): Producto = Producto(
            codigo = data["codigo"] as? String ?: "",
            descripcion = data["descripcion"] as? String ?: "",
            precio = (data["precio"] as? Number)?.toDouble() ?: 0.0
        )
    }
}