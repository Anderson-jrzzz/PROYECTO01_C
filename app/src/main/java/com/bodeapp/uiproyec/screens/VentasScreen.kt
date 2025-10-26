package com.bodeapp.uiproyec.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bodeapp.model.Producto
import com.bodeapp.model.Venta
import com.bodeapp.viewmodel.ProductoViewModel
import com.bodeapp.viewmodel.VentaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    navController: NavHostController,
    productoViewModel: ProductoViewModel = viewModel(),
    ventaViewModel: VentaViewModel = viewModel()
) {
    val productos by productoViewModel.productos.collectAsState()
    val ventasDelDia by ventaViewModel.ventasDelDia.collectAsState()

    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var subtotal by remember { mutableStateOf(0.0) }
    var expanded by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    // Calcular subtotal cuando cambie la cantidad
    LaunchedEffect(cantidad, productoSeleccionado) {
        val cant = cantidad.toIntOrNull() ?: 0
        val precio = productoSeleccionado?.precio ?: 0.0
        subtotal = cant * precio
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeGradient)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva Venta",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Selector de producto
            Text(
                text = "Seleccionar producto",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = productoSeleccionado?.let { "${it.nombre} (Stock: ${it.stock})" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = { Text("Elige un producto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (productos.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay productos registrados") },
                            onClick = {}
                        )
                    } else {
                        productos.forEach { producto ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(producto.nombre, fontWeight = FontWeight.Bold)
                                        Text(
                                            "Stock: ${producto.stock} - S/ ${String.format("%.2f", producto.precio)}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                },
                                onClick = {
                                    productoSeleccionado = producto
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFFFF6B00)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cantidad
            Text(
                text = "Cantidad",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it.filter { char -> char.isDigit() } },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0") },
                enabled = productoSeleccionado != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B00),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subtotal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Subtotal",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", subtotal)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6B00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar Venta
            Button(
                onClick = {
                    if (productoSeleccionado == null) {
                        errorMessage = "Selecciona un producto"
                        showError = true
                        return@Button
                    }

                    val cant = cantidad.toIntOrNull()
                    if (cant == null || cant <= 0) {
                        errorMessage = "Ingresa una cantidad válida"
                        showError = true
                        return@Button
                    }

                    val producto = productoSeleccionado!!
                    val nuevaVenta = Venta(
                        productoId = producto.id,
                        nombreProducto = producto.nombre,
                        cantidad = cant,
                        precioUnitario = producto.precio,
                        total = subtotal
                    )

                    ventaViewModel.registrarVenta(
                        venta = nuevaVenta,
                        onSuccess = {
                            showSuccess = true
                            productoSeleccionado = null
                            cantidad = ""
                            subtotal = 0.0
                        },
                        onError = { error ->
                            errorMessage = error
                            showError = true
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(orangeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Registrar Venta",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de ventas del día
            if (ventasDelDia.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aún no hay ventas registradas hoy",
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = "Ventas del día (${ventasDelDia.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ventasDelDia) { venta ->
                        VentaItem(venta)
                    }
                }
            }
        }
    }

    // Snackbars
    if (showError) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(3000)
            showError = false
        }
    }

    if (showSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showSuccess = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (showError) {
            Snackbar(
                containerColor = Color(0xFFF44336),
                contentColor = Color.White
            ) {
                Text(errorMessage)
            }
        }

        if (showSuccess) {
            Snackbar(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Text("✓ Venta registrada exitosamente")
            }
        }
    }
}

@Composable
fun VentaItem(venta: Venta) {
    val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val fecha = dateFormat.format(Date(venta.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFFA726), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = venta.nombreProducto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Row {
                        Text(
                            text = "Cant: ${venta.cantidad}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "S/ ${String.format("%.2f", venta.total)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = fecha,
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}