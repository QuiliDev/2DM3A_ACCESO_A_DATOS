package com.quilidev.ad0

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class NombreApellidos(val nombre: String, val apellidos: String) {
    override fun toString() = "$nombre $apellidos"
}

class EstudiantesRepo(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col = db.collection("estudiantes")

    // Fun1: DNI es igual → Un String Nombre
    suspend fun fun1NombrePorDni(dniNie: DniNie): String? {
        val doc = col.document(dniNie.toString()).get().await()
        return if (doc.exists()) doc.getString("nombre") else null
    }

    // Fun2: Nombre es igual → Lista nombres y apellidos
    suspend fun fun2PorNombre(nombreExacto: String): List<NombreApellidos> {
        val snap = col.whereEqualTo("nombre", nombreExacto.trim()).get().await()
        return snap.documents.mapNotNull { d ->
            val n = d.getString("nombre")
            val a = d.getString("apellidos")
            if (n != null && a != null) NombreApellidos(n, a) else null
        }
    }

    // Fun3: Menor de edad X → Lista nombres y apellidos
    suspend fun fun3MenorDeEdad(x: Int): List<NombreApellidos> {
        val snap = col.whereLessThan("edad", x).get().await()
        return snap.documents.mapNotNull { d ->
            val n = d.getString("nombre")
            val a = d.getString("apellidos")
            if (n != null && a != null) NombreApellidos(n, a) else null
        }
    }

    // Fun4: Mayor o igual de edad X → Lista nombres y apellidos
    suspend fun fun4MayorOIgualEdad(x: Int): List<NombreApellidos> {
        val snap = col.whereGreaterThanOrEqualTo("edad", x).get().await()
        return snap.documents.mapNotNull { d ->
            val n = d.getString("nombre")
            val a = d.getString("apellidos")
            if (n != null && a != null) NombreApellidos(n, a) else null
        }
    }

    // Fun5: Si es vasco (Bizkaia, Álava/Vitoria, Gipuzkoa) → Lista nombres y apellidos
    suspend fun fun5Vascos(): List<NombreApellidos> {
        val provinciasVascas = listOf(Provincia.BIZKAIA.name, Provincia.ALAVA.name, Provincia.GIPUZKOA.name)
        val snap = col.whereIn("provincia", provinciasVascas).get().await()
        return snap.documents.mapNotNull { d ->
            val n = d.getString("nombre")
            val a = d.getString("apellidos")
            if (n != null && a != null) NombreApellidos(n, a) else null
        }
    }

    // Fun6: Si no es vasco → Lista nombres y apellidos
    suspend fun fun6NoVascos(): List<NombreApellidos> {
        val snap = col.whereEqualTo("provincia", Provincia.OTRA.name).get().await()
        return snap.documents.mapNotNull { d ->
            val n = d.getString("nombre")
            val a = d.getString("apellidos")
            if (n != null && a != null) NombreApellidos(n, a) else null
        }
    }
}
