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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bodeapp.model.Producto
import com.bodeapp.viewmodel.ProductoViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import com.bodeapp.util.UserSessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavHostController,
    viewModel: ProductoViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager.getInstance(context) }
    val usuarioId = sessionManager.getUserId()
    
    val productos by viewModel.productos.collectAsState()

    // Refrescar productos cuando cambie el usuario
    LaunchedEffect(usuarioId) {
        if (usuarioId != -1) {
            viewModel.refrescarProductos()
        }
    }

    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    var nombreProducto by rememberSaveable { mutableStateOf("") }
    var precioProducto by rememberSaveable { mutableStateOf("") }
    var stockProducto by rememberSaveable { mutableStateOf("") }

    var showError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

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
                    text = "Nuevo Producto",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Nombre del producto",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = nombreProducto,
                    onValueChange = { nombreProducto = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Coca Cola 1L") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text(
                    text = "Precio de venta (S/)",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = precioProducto,
                    onValueChange = { precioProducto = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0.00") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Text(
                    text = "Stock inicial",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = stockProducto,
                    onValueChange = { stockProducto = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        if (usuarioId == -1) {
                            errorMessage = "Error: Usuario no autenticado"
                            showError = true
                            return@Button
                        }
                        
                        if (nombreProducto.isEmpty() || precioProducto.isEmpty() || stockProducto.isEmpty()) {
                            errorMessage = "Por favor completa todos los campos"
                            showError = true
                        } else {
                            val nuevoProducto = Producto(
                                usuarioId = usuarioId,
                                nombre = nombreProducto,
                                precio = precioProducto.toDoubleOrNull() ?: 0.0,
                                stock = stockProducto.toIntOrNull() ?: 0
                            )
                            viewModel.insertProducto(nuevoProducto)
                            nombreProducto = ""
                            precioProducto = ""
                            stockProducto = ""
                        }
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
                            text = "Guardar Producto",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Productos registrados (${productos.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            if (productos.isEmpty()) {
                item {
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
                            text = "No hay productos registrados",
                            color = Color(0xFF999999),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(productos) { producto ->
                    ProductoItem(
                        producto = producto,
                        onEdit = {
                            productoSeleccionado = producto
                            showEditDialog = true
                        },
                        onDelete = {
                            productoSeleccionado = producto
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Snackbar para errores
    if (showError) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showError = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = Color(0xFFF44336)
        ) {
            Text(errorMessage, color = Color.White)
        }
    }

    // Dialog para editar
    if (showEditDialog && productoSeleccionado != null) {
        EditarProductoDialog(
            producto = productoSeleccionado!!,
            onDismiss = { showEditDialog = false },
            onConfirm = { nombre, precio, stock ->
                viewModel.updateProducto(
                    productoSeleccionado!!.copy(
                        nombre = nombre,
                        precio = precio,
                        stock = stock
                    )
                )
                showEditDialog = false
            }
        )
    }

    // Dialog para eliminar
    if (showDeleteDialog && productoSeleccionado != null) {
        EliminarProductoDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteProducto(productoSeleccionado!!)
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
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
                        text = producto.nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Row {
                        Text(
                            text = "S/ ${String.format("%.2f", producto.precio)}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Stock: ${producto.stock}",
                            fontSize = 14.sp,
                            color = if (producto.stock > 10) Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF2196F3)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

@Composable
fun EditarProductoDialog(
    producto: Producto,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var precio by remember { mutableStateOf(producto.precio.toString()) }
    var stock by remember { mutableStateOf(producto.stock.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Actualizar Producto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Text(
                    text = "Modifica los datos del producto",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nombre del producto",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Precio de venta (S/)",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Stock",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B00),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            onConfirm(
                                nombre,
                                precio.toDoubleOrNull() ?: 0.0,
                                stock.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF6B00)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Actualizar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun EliminarProductoDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "¿Estás seguro?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Esta acción eliminará el producto permanentemente. No se puede deshacer.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }
}