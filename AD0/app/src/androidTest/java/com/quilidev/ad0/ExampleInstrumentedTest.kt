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

    private val db = FirebaseFirestore.getInstance()
    private val repo = EstudiantesRepo(db)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.quilidev.ad0", appContext.packageName)
    }

    @Test
    fun prepararDatos() = runBlocking {
        cargarVariosEstudiantes(db)
        assertTrue(true)
    }

    // -------- Fun1
    @Test
    fun fun1_DNI_Igual_devuelveNombre() = runBlocking {
        val nombre = repo.fun1NombrePorDni(DniNie("Z2005813B"))
        assertEquals("Juan", nombre)
    }

    // -------- Fun2
    @Test
    fun fun2_NombreIgual_listaNombresApellidos() = runBlocking {
        val lista = repo.fun2PorNombre("Juan")
        // Esperamos 2 "Juan": "Quiliche Calderon" y "Sanchez Ruiz"
        val textos = lista.map { it.toString() }.toSet()
        assertTrue(textos.contains("Juan Quiliche Calderon"))
        assertTrue(textos.contains("Juan Sanchez Ruiz"))
        assertEquals(2, lista.size)
    }

    // -------- Fun3
    @Test
    fun fun3_MenorDeEdadX() = runBlocking {
        val menores = repo.fun3MenorDeEdad(18)
        assertTrue(menores.any { it.nombre == "Lucia" && it.apellidos == "Perez Soto" })
    }

    // -------- Fun4
    @Test
    fun fun4_MayorOIgualEdadX() = runBlocking {
        val mayores = repo.fun4MayorOIgualEdad(30)
        assertTrue(mayores.any { it.nombre == "Nikolai" && it.apellidos == "Ivanov" })
    }

    // -------- Fun5
    @Test
    fun fun5_Vascos() = runBlocking {
        val vascos = repo.fun5Vascos().map { it.toString() }.toSet()
        assertTrue(vascos.contains("Juan Quiliche Calderon"))
        assertTrue(vascos.contains("Ane Lopez Garcia"))
        assertTrue(vascos.contains("Mikel Etxeberria"))
        assertTrue(vascos.contains("Juan Sanchez Ruiz"))
        assertFalse(vascos.contains("Nikolai Ivanov"))
        assertFalse(vascos.contains("Lucia Perez Soto"))
    }

    // -------- Fun6
    @Test
    fun fun6_NoVascos() = runBlocking {
        val noVascos = repo.fun6NoVascos().map { it.toString() }.toSet()
        assertTrue(noVascos.contains("Nikolai Ivanov"))
        assertTrue(noVascos.contains("Lucia Perez Soto"))
        assertFalse(noVascos.contains("Juan Quiliche Calderon"))
        assertFalse(noVascos.contains("Ane Lopez Garcia"))
        assertFalse(noVascos.contains("Mikel Etxeberria"))
        assertFalse(noVascos.contains("Juan Sanchez Ruiz"))
    }

}
