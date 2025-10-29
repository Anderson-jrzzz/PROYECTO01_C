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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bodeapp.model.Producto
import com.bodeapp.model.Venta
import com.bodeapp.util.UserSessionManager
import com.bodeapp.viewmodel.ProductoViewModel
import com.bodeapp.viewmodel.VentaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosVentas(
    productos: List<Producto>,
    ventasFiltradas: List<Venta>,
    onFiltrar: (productoId: Int?, fechaDesde: String, fechaHasta: String) -> Unit
) {
    var productoFiltro by remember { mutableStateOf<Producto?>(null) }
    var fechaDesde by remember { mutableStateOf("") }
    var fechaHasta by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorFecha by remember { mutableStateOf<String?>(null) }
    var showResultsDialog by remember { mutableStateOf(false) }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Título del filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFFF6B00),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filtrar Ventas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Producto dropdown
            Text(
                text = "Producto",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = productoFiltro?.nombre ?: "Todos los productos",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFFFF6B00),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLeadingIconColor = Color(0xFFFF6B00),
                        unfocusedLeadingIconColor = Color(0xFF999999)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Todos los productos",
                                fontWeight = if (productoFiltro == null) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            productoFiltro = null
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = Color(0xFFFF6B00)
                            )
                        }
                    )
                    productos.forEach { producto ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    producto.nombre,
                                    fontWeight = if (productoFiltro?.id == producto.id) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                productoFiltro = producto
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = if (productoFiltro?.id == producto.id) Color(0xFFFF6B00) else Color(0xFF999999)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rango de fechas
            Text(
                text = "Rango de Fechas",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fecha desde
                OutlinedTextField(
                    value = fechaDesde,
                    onValueChange = {
                        fechaDesde = it
                        errorFecha = null
                    },
                    label = { Text("Desde", fontSize = 12.sp) },
                    placeholder = { Text("yyyy-MM-dd", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFF6B00),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLeadingIconColor = Color(0xFFFF6B00),
                        unfocusedLeadingIconColor = Color(0xFF999999),
                        errorBorderColor = Color(0xFFF44336),
                        errorLeadingIconColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = errorFecha != null && fechaDesde.isNotBlank()
                )

                // Fecha hasta
                OutlinedTextField(
                    value = fechaHasta,
                    onValueChange = {
                        fechaHasta = it
                        errorFecha = null
                    },
                    label = { Text("Hasta", fontSize = 12.sp) },
                    placeholder = { Text("yyyy-MM-dd", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFF6B00),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedLeadingIconColor = Color(0xFFFF6B00),
                        unfocusedLeadingIconColor = Color(0xFF999999),
                        errorBorderColor = Color(0xFFF44336),
                        errorLeadingIconColor = Color(0xFFF44336)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    isError = errorFecha != null && fechaHasta.isNotBlank()
                )
            }

            // Mensaje de error
            if (errorFecha != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = errorFecha!!,
                        color = Color(0xFFF44336),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de filtrar
            Button(
                onClick = {
                    val regex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
                    if ((fechaDesde.isNotBlank() && !regex.matches(fechaDesde)) ||
                        (fechaHasta.isNotBlank() && !regex.matches(fechaHasta))) {
                        errorFecha = "Formato de fecha inválido (debe ser yyyy-MM-dd)"
                    } else {
                        errorFecha = null
                        onFiltrar(productoFiltro?.id, fechaDesde, fechaHasta)
                        showResultsDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(orangeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aplicar Filtros",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Diálogo de resultados filtrados
    if (showResultsDialog) {
        VentasFiltradasDialog(
            ventas = ventasFiltradas,
            onDismiss = { showResultsDialog = false }
        )
    }
}

@Composable
fun VentasFiltradasDialog(
    ventas: List<Venta>,
    onDismiss: () -> Unit
) {
    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(orangeGradient)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Resultados del Filtro",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${ventas.size} venta(s) encontrada(s)",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Lista de ventas con scroll
                if (ventas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFFE0E0E0),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No se encontraron ventas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF999999)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Intenta ajustar los filtros",
                                fontSize = 14.sp,
                                color = Color(0xFFBBBBBB)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ventas) { venta ->
                            VentaItemDialog(venta)
                        }
                        // Espaciado al final
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VentaItemDialog(venta: Venta) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val fecha = dateFormat.format(Date(venta.fecha))
    val hora = timeFormat.format(Date(venta.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Nombre del producto y Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = venta.nombreProducto,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C),
                        lineHeight = 22.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Total destacado
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF999999),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "S/ ${String.format("%.2f", venta.total)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFEEEEEE))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Información detallada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fecha y hora
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFFFF6B00),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = fecha,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF555555),
                            lineHeight = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = hora,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF777777),
                            lineHeight = 18.sp
                        )
                    }
                }

                // Cantidad y Precio unitario
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cantidad:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF888888),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${venta.cantidad}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Precio unit:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF888888),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "S/ ${String.format("%.2f", venta.precioUnitario)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFF6B00),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentasScreen(
    navController: NavHostController,
    productoViewModel: ProductoViewModel = viewModel(),
    ventaViewModel: VentaViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager.getInstance(context) }
    val usuarioId = sessionManager.getUserId()
    
    val productos by productoViewModel.productos.collectAsState()
    val ventasDelDia by ventaViewModel.ventasDelDia.collectAsState()
    val ventasFiltradas by ventaViewModel.ventasFiltradas.collectAsState()

    // Refrescar datos cuando cambie el usuario
    LaunchedEffect(usuarioId) {
        if (usuarioId != -1) {
            productoViewModel.refrescarProductos()
            ventaViewModel.refrescarVentas()
        }
    }

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

        // Contenido con scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Selector de producto
            item {
                Column {
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
                }
            }

            // Cantidad
            item {
                Column {
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
                }
            }

            // Subtotal
            item {
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
            }

            // Botón Registrar Venta
            item {
                Button(
                    onClick = {
                        if (usuarioId == -1) {
                            errorMessage = "Error: Usuario no autenticado"
                            showError = true
                            return@Button
                        }
                        
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
                            usuarioId = usuarioId,
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
            }

            // Filtros de ventas
            item {
                FiltrosVentas(
                    productos = productos,
                    ventasFiltradas = ventasFiltradas,
                    onFiltrar = { productoId, fechaDesde, fechaHasta ->
                        ventaViewModel.filtrarVentas(productoId, fechaDesde, fechaHasta)
                    }
                )
            }



            // Items de ventas
            items(ventasFiltradas) { venta ->
                VentaItem(venta)
            }
        }
    }


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