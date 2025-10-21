package com.example.ad_ejercicio1.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.ad_ejercicio1.model.Producto
import kotlinx.coroutines.tasks.await

object ProductosRepo {

    private val col = Firebase.firestore.collection("productos")

    suspend fun crear(producto: Producto): Boolean {
        require(producto.codigo.isNotBlank()) { "El código no puede estar vacío" }

        val docRef = col.document(producto.codigo.trim())

        // Verificamos si ya existe un producto con ese código
        val existente = docRef.get().await()
        if (existente.exists()) {
            return false
        }

        // Si no existe, lo creamos normalmente
        docRef.set(producto.toMap()).await()
        return true
    }


    suspend fun obtenerPorCodigo(codigo: String): Producto? {
        if (codigo.isBlank()) return null
        val snap = col.document(codigo.trim()).get().await()
        return if (snap.exists()) Producto.fromMap(snap.data ?: emptyMap()) else null
    }

    suspend fun obtenerPorDescripcion(descripcion: String): List<Producto> {
        if (descripcion.isBlank()) return emptyList()
        val snap = col.whereEqualTo("descripcion", descripcion.trim()).get().await()
        return snap.documents.mapNotNull { it.data?.let(Producto::fromMap) }
    }

    suspend fun eliminarPorCodigo(codigo: String): Boolean {
        if (codigo.isBlank()) return false
        val ref = col.document(codigo.trim())
        if (!ref.get().await().exists()) return false
        ref.delete().await()
        return true
    }

    suspend fun modificarPrecio(codigo: String, nuevoPrecio: Double): Boolean {
        if (codigo.isBlank()) return false
        val ref = col.document(codigo.trim())
        if (!ref.get().await().exists()) return false
        ref.update("precio", nuevoPrecio).await()
        return true
    }

    suspend fun listarTodos(): List<Producto> {
        val snap = col.get().await()
        return snap.documents.mapNotNull { it.data?.let(Producto::fromMap) }
    }
}