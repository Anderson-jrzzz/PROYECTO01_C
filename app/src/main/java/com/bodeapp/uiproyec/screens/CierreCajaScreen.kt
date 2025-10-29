package com.bodeapp.uiproyec.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bodeapp.data.ProductoVendido
import com.bodeapp.util.UserSessionManager
import com.bodeapp.viewmodel.VentaViewModel
import com.bodeapp.viewmodel.CompraViewModel
import com.bodeapp.viewmodel.CierreCajaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CierreCajaScreen(
    navController: NavHostController,
    ventaViewModel: VentaViewModel = viewModel(),
    compraViewModel: CompraViewModel = viewModel(),
    cierreCajaViewModel: CierreCajaViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager.getInstance(context) }
    val usuarioId = sessionManager.getUserId()
    
    val totalVentas by ventaViewModel.totalVentas.collectAsState()
    val totalCompras by compraViewModel.totalCompras.collectAsState()
    val utilidad = totalVentas - totalCompras

    val ventasDelDia by ventaViewModel.ventasDelDia.collectAsState()
    val comprasDelDia by compraViewModel.comprasDelDia.collectAsState()

    val cierres by cierreCajaViewModel.cierres.collectAsState()

    // Refrescar datos cuando cambie el usuario
    LaunchedEffect(usuarioId) {
        if (usuarioId != -1) {
            ventaViewModel.refrescarVentas()
            compraViewModel.refrescarCompras()
            cierreCajaViewModel.refrescarCierres()
        }
    }

    var filtroDesde by rememberSaveable { mutableStateOf("") }
    var filtroHasta by rememberSaveable { mutableStateOf("") }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    // Calcular productos más vendidos manualmente
    val productosMasVendidos = remember(ventasDelDia) {
        ventasDelDia
            .groupBy { it.nombreProducto }
            .map { (nombre, ventas) ->
                ProductoVendido(
                    nombreProducto = nombre,
                    totalVendido = ventas.sumOf { it.cantidad }
                )
            }
            .sortedByDescending { it.totalVendido }
            .take(5)
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
                    text = "Cierre del Día",
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
            // Resumen financiero
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Total Ventas",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", totalVentas)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFF44336), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Total Compras",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", totalCompras)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF2196F3), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Utilidad",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "S/ ${String.format("%.2f", utilidad)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (utilidad >= 0) Color(0xFF2196F3) else Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val estado = when {
                                totalVentas == 0.0 && totalCompras == 0.0 -> "SIN MOVIMIENTOS"
                                utilidad > 0 -> "GANANCIA"
                                utilidad < 0 -> "PÉRDIDA"
                                else -> ""
                            }
                            if (estado.isNotEmpty()) {
                                Surface(
                                    color = when (estado) {
                                        "GANANCIA" -> Color(0xFFE8F5E9)
                                        "PÉRDIDA" -> Color(0xFFFFEBEE)
                                        else -> Color(0xFFE3F2FD)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = estado,
                                        color = when (estado) {
                                            "GANANCIA" -> Color(0xFF4CAF50)
                                            "PÉRDIDA" -> Color(0xFFF44336)
                                            else -> Color(0xFF2196F3)
                                        },
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Reportes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            if (productosMasVendidos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFFE0E0E0),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No hay datos de ventas hoy",
                                color = Color(0xFF999999),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Productos más vendidos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                }
                items(productosMasVendidos.withIndex().toList()) { (index, producto) ->
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFFFA726), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = producto.nombreProducto,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Vendidos: ${producto.totalVendido}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }
            }

            // Generar Cierre
            item {
                Button(
                    onClick = {
                        showConfirm = true
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
                            text = "Generar Cierre",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Filtro por fecha
            item {
                Text(
                    text = "Historial de cierres",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = filtroDesde,
                        onValueChange = { filtroDesde = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Desde (yyyy-MM-dd)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B00),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    OutlinedTextField(
                        value = filtroHasta,
                        onValueChange = { filtroHasta = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Hasta (yyyy-MM-dd)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF6B00),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { cierreCajaViewModel.filtrar(filtroDesde, filtroHasta) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00))
                    ) { Text("Buscar", color = Color.White) }
                    OutlinedButton(
                        onClick = {
                            filtroDesde = ""
                            filtroHasta = ""
                            // recolección por defecto ya expone todo
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Limpiar") }
                }
            }

            if (cierres.isEmpty()) {
                item {
                    Text(
                        text = "No hay cierres registrados",
                        color = Color(0xFF999999)
                    )
                }
            } else {
                items(cierres) { cierre ->
                    val fecha = remember(cierre.fechaCierre) {
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(cierre.fechaCierre))
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = fecha, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Ventas: S/ ${String.format("%.2f", cierre.totalVentas)}", color = Color(0xFF4CAF50))
                                Text("Compras: S/ ${String.format("%.2f", cierre.totalCompras)}", color = Color(0xFFF44336))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Utilidad: S/ ${String.format("%.2f", cierre.utilidad)}")
                        }
                    }
                }
            }
        }

        if (showConfirm) {
            AlertDialog(
                onDismissRequest = { showConfirm = false },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirm = false
                        cierreCajaViewModel.generarCierre(
                            totalVentas = totalVentas,
                            totalCompras = totalCompras,
                            onSuccess = {
                                val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                successMessage = "Cierre generado y datos reiniciados: $fecha"
                                showSuccess = true
                            },
                            onError = { err ->
                                successMessage = err
                                showSuccess = true
                            }
                        )
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirm = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmación") },
                text = { Text("¿Seguro de Generar Cierre?") }
            )
        }

        if (showSuccess) {
            LaunchedEffect(showSuccess) {
                kotlinx.coroutines.delay(2500)
                showSuccess = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar { Text(successMessage) }
            }
        }
    }
}