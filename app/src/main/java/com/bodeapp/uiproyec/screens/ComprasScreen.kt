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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bodeapp.model.Compra
import com.bodeapp.model.Producto
import com.bodeapp.viewmodel.CompraViewModel
import com.bodeapp.viewmodel.ProductoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComprasScreen(
    navController: NavHostController,
    productoViewModel: ProductoViewModel = viewModel(),
    compraViewModel: CompraViewModel = viewModel()
) {
    val productos by productoViewModel.productos.collectAsState()
    val comprasDelDia by compraViewModel.comprasDelDia.collectAsState()

    var selectedProductoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val productoSeleccionado = productos.firstOrNull { it.id == selectedProductoId }
    var nuevoProducto by rememberSaveable { mutableStateOf("") }
    var costo by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var mostrarNuevoProducto by rememberSaveable { mutableStateOf(false) }

    var showError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(orangeGradient)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registrar Compra",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Selector de producto
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Producto/Insumo",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = productoSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        placeholder = { Text("Selecciona un producto") },
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
                        if (productos.isNotEmpty()) {
                            productos.forEach { producto ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(producto.nombre, fontWeight = FontWeight.Bold)
                                            Text(
                                                "Stock actual: ${producto.stock}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedProductoId = producto.id
                                        mostrarNuevoProducto = false
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            tint = Color(0xFFFF6B00)
                                        )
                                    }
                                )
                            }
                            Divider()
                        }
                        DropdownMenuItem(
                            text = { Text("+ Escribir otro producto/insumo") },
                            onClick = {
                                mostrarNuevoProducto = true
                                selectedProductoId = null
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6B00)
                                )
                            }
                        )
                    }
                }
            }
        }

        if (mostrarNuevoProducto) {
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    OutlinedTextField(
                        value = nuevoProducto,
                        onValueChange = { nuevoProducto = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Escribe el nombre del producto") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B00),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Costo
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Costo (S/)",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = costo,
                    onValueChange = { costo = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Cantidad
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // Botón Registrar Compra
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                Button(
                    onClick = {
                        val productoFinal = if (mostrarNuevoProducto) nuevoProducto else productoSeleccionado?.nombre

                        if (productoFinal.isNullOrEmpty()) {
                            errorMessage = "Selecciona o escribe un producto"
                            showError = true
                            return@Button
                        }

                        if (costo.isEmpty() || cantidad.isEmpty()) {
                            errorMessage = "Completa todos los campos"
                            showError = true
                            return@Button
                        }

                        val nuevaCompra = Compra(
                            productoId = selectedProductoId,
                            nombreProducto = productoFinal,
                            cantidad = cantidad.toIntOrNull() ?: 0,
                            costo = costo.toDoubleOrNull() ?: 0.0
                        )

                        compraViewModel.registrarCompra(
                            compra = nuevaCompra,
                            onSuccess = {
                                showSuccess = true
                                selectedProductoId = null
                                nuevoProducto = ""
                                costo = ""
                                cantidad = ""
                                mostrarNuevoProducto = false
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
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
                            text = "Registrar Compra",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Lista de compras
        if (comprasDelDia.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aún no hay compras registradas",
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(
                        text = "Compras recientes (${comprasDelDia.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
            }
            items(comprasDelDia) { compra ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    CompraItem(compra)
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
                Text("✓ Compra registrada exitosamente")
            }
        }
    }
}

@Composable
fun CompraItem(compra: Compra) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val fecha = dateFormat.format(Date(compra.fecha))

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFFF6B00), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = compra.nombreProducto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Row {
                        Text(
                            text = "Cant: ${compra.cantidad}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "- S/ ${String.format("%.2f", compra.costo)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
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