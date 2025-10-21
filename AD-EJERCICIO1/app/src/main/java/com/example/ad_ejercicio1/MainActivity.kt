package com.example.ad_ejercicio1


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import com.example.ad_ejercicio1.ui.theme.ADEJERCICIO1Theme
import com.example.ad_ejercicio1.data.ProductosRepo
import com.example.ad_ejercicio1.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ADEJERCICIO1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // INI codigo ventana
                    myForm(Modifier.padding(innerPadding))
                    // FIN codigo ventana

                }
            }
        }
    }
}

@Composable
fun myForm(modifier: Modifier) {
    var codigo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally, // centra horizontalmente
        verticalArrangement = Arrangement.Top // empieza desde arriba
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text("Nombre") },
            placeholder = { Text("Nombre del Producto") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        formInput(value=codigo,onValueChange = { codigo = it },"Código del producto","Ejemplo: 1")
        formInput(descripcion,{ descripcion = it },"Descripcion")
        formInput(precio,{ precio = it },"Precio")
        Spacer(modifier = modifier.height(20.dp)) // opcional: espacio entre Crono y el botón
        funButton("Consulta Por Código") { consultaCodigo(codigo) {mensaje = it} }
        funButton("Consulta Por Descripción") { consultaDescripcion(descripcion) {mensaje = it} }
        funButton("Baja por código") { bajaCodigo(codigo) {mensaje = it} }
        funButton("Alta") { alta(codigo, descripcion, precio) {mensaje = it} }
        funButton("Modificación") { modifica(codigo, descripcion, precio) {mensaje = it} }
        funButton("Listar") { listar() {mensaje = it} }
        Text(text = "mensaje:")
        Text(text = mensaje)
    }
}

@Composable
fun formInput(value: String, onValueChange: (String) -> Unit,desc: String, ej: String = "") {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(desc) },
        placeholder = { Text(ej) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
fun funButton(
    buttonText: String,
    onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = buttonText)
    }
}


// ---- COROUTINAS ----
private fun runInput_Output(block: suspend () -> Unit, onError: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try { block() } catch (e: Exception) { onError("Error: ${e.message}") }
    }
}
// ---- MOSTRAR MONEDA LOCAL ----
private fun money(value: Double): String = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("es-ES")).format(value)

// ---- FUNCIONES CON COROUTINAS ----

fun alta(codigo: String, descripcion: String, precio: String, callback: (String) -> Unit) {
    runInput_Output({
        val p = precio.toDoubleOrNull() ?: throw IllegalArgumentException("Precio inválido")
        val producto = Producto(codigo.trim(), descripcion.trim(), p)

        val creado = ProductosRepo.crear(producto)
        if (creado) {
            callback("✅ Producto creado:\nCódigo: ${producto.codigo}\nDescripción: ${producto.descripcion}\nPrecio: ${money(producto.precio)}")
        } else {
            callback("⚠️ Ya existe un producto con el código ${producto.codigo}.")
        }
    }, callback)
}


fun consultaCodigo(codigo: String, callback: (String) -> Unit) {
    if (codigo.isBlank()) { callback("Ingrese un código para consultar"); return }
    runInput_Output({
        val prod = ProductosRepo.obtenerPorCodigo(codigo)
        callback(prod?.let {
            "Encontrado:\nCódigo: ${it.codigo}\nDescripción: ${it.descripcion}\nPrecio: ${money(it.precio)}"
        } ?: "No existe producto con código: $codigo")
    }, callback)
}

fun consultaDescripcion(descripcion: String, callback: (String) -> Unit) {
    if (descripcion.isBlank()) { callback("Ingrese una descripción para consultar"); return }
    runInput_Output({
        val lista = ProductosRepo.obtenerPorDescripcion(descripcion)
        callback(
            if (lista.isEmpty()) "Sin resultados para \"$descripcion\""
            else buildString {
                appendLine("Resultados (${lista.size}):")
                lista.forEach { appendLine("• ${it.codigo} | ${it.descripcion} | ${money(it.precio)}") }
            }
        )
    }, callback)
}

fun bajaCodigo(codigo: String, callback: (String) -> Unit) {
    if (codigo.isBlank()) { callback("Ingrese un código para eliminar"); return }
    runInput_Output({
        val ok = ProductosRepo.eliminarPorCodigo(codigo)
        callback(if (ok) "Eliminado OK: $codigo" else "No existe producto con código: $codigo")
    }, callback)
}

fun modifica(codigo: String, descripcion: String, precio: String, callback: (String) -> Unit) {
    if (codigo.isBlank()) { callback("Ingrese un código para modificar"); return }
    runInput_Output({
        val p = precio.toDoubleOrNull() ?: throw IllegalArgumentException("Precio inválido")
        val ok = ProductosRepo.modificarPrecio(codigo, p)
        callback(if (ok) "✅ Producto Modificado:\nCódigo: $codigo\nNuevo precio: ${money(p)}"
        else "No existe producto con código: $codigo")
    }, callback)
}

fun listar(callback: (String) -> Unit) {
    runInput_Output({
        val lista = ProductosRepo.listarTodos()
        callback(
            if (lista.isEmpty()) "No hay productos."
            else buildString {
                appendLine("Listado (${lista.size}):")
                lista.sortedBy { it.codigo }
                    .forEach { appendLine("• ${it.codigo} | ${it.descripcion} | ${money(it.precio)}") }
            }
        )
    }, callback)
}