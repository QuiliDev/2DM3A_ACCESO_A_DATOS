package com.quilidev.ad0

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.quilidev.ad0", appContext.packageName)
    }

    @Test
    fun testObtenerDatos() = runBlocking {
        val miClase = db()
        val salida = miClase.prueba()
        println("Salida: $salida")
        assertEquals("Juan", salida)
    }

    @Test
    fun testEdadMayorDe18() = runBlocking {
        val miClase = db()
        val salida = miClase.edadMayorDe18()
        assertTrue(salida) // pasa si hay alguien >18

    }

    @Test
    fun testCargarEstudiante() = runBlocking {
        val db = FirebaseFirestore.getInstance()

        // 1) Escribimos
        cargarVariosEstudiantes(db)

        // 2) Verificamos que quedó en Firestore
        val snap = FirebaseFirestore.getInstance()
            .collection("estudiantes")
            .whereEqualTo("dniNie", "Z2005813B")
            .get()
            .await()

        assertTrue("No se encontró el estudiante insertado", !snap.isEmpty)
    }


}
