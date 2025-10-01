package com.quilidev.ad0
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class db {
    private val dbConection: FirebaseFirestore = FirebaseFirestore.getInstance()
    suspend fun prueba(): String {
        val resultado = dbConection.collection("persona")
            .whereEqualTo("nombre", "nikolai")
            .get()
            .await()

        // Por ejemplo, devolver el nombre del primer documento
        return if (!resultado.isEmpty) {
            resultado.documents[0].getString("nombre") ?: "No encontrado"
        } else {
            "No encontrado"
        }
        
    }

    suspend fun edadMayorDe18(): Boolean {
        return try {
            val resultado = dbConection.collection("persona")
                .whereGreaterThan("edad", 18)
                .get()
                .await()

            !resultado.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}