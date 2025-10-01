package com.quilidev.ad0

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ---- Modelo ----
@JvmInline
value class DniNie(val raw: String) {
    init {
        require(raw.trim().uppercase().matches(Regex("""^[XYZ]?\d{7,8}[A-Z]$"""))) {
            "DNI/NIE inválido: $raw"
        }
    }
    override fun toString() = raw.trim().uppercase()
}

enum class Provincia { ALAVA, BIZKAIA, GIPUZKOA, OTRA }

data class Estudiante(
    val dniNie: DniNie,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val provincia: Provincia
) {
    val edad: Int get() = LocalDate.now().year - fechaNacimiento.year -
            if (LocalDate.now().dayOfYear < fechaNacimiento.dayOfYear) 1 else 0

    fun toMap() = mapOf(
        "dniNie" to dniNie.toString(),
        "nombre" to nombre.trim(),
        "apellidos" to apellidos.trim(),
        "fechaNacimientoIso" to fechaNacimiento.format(DateTimeFormatter.ISO_LOCAL_DATE),
        "provincia" to provincia.name,
        "edad" to edad,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
    )
}

// ---- Persistencia ----
suspend fun guardarEstudiante(db: FirebaseFirestore, estudiante: Estudiante): Result<String> =
    runCatching {
        val docId = estudiante.dniNie.toString()
        db.collection("estudiantes").document(docId).set(estudiante.toMap()).await()
        docId
    }

// ---- Ejemplo ----
suspend fun cargarVariosEstudiantes(db: FirebaseFirestore) {
    val estudiantes = listOf(
        Estudiante(DniNie("Z2005813B"), "Juan", "Quiliche Calderon", LocalDate.parse("1996-12-16"), Provincia.BIZKAIA),
        Estudiante(DniNie("X1234567T"), "Ane", "Lopez Garcia", LocalDate.parse("2005-03-21"), Provincia.GIPUZKOA),
        Estudiante(DniNie("Y7654321M"), "Mikel", "Etxeberria", LocalDate.parse("1990-07-08"), Provincia.ALAVA)
    )

    for (e in estudiantes) {
        guardarEstudiante(db, e)
    }
}

