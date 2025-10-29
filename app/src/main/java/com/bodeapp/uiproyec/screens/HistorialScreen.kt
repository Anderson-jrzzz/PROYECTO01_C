package com.bodeapp.uiproyec.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.bodeapp.model.Compra
import com.bodeapp.model.Venta
import com.bodeapp.util.UserSessionManager
import com.bodeapp.viewmodel.VentaViewModel
import com.bodeapp.viewmodel.CompraViewModel
import java.text.SimpleDateFormat
import java.util.*

sealed class TransaccionHistorial {
    abstract val id: Int
    abstract val producto: String
    abstract val cantidad: Int
    abstract val monto: Double
    abstract val fecha: Long

    data class TransaccionVenta(
        override val id: Int,
        override val producto: String,
        override val cantidad: Int,
        override val monto: Double,
        override val fecha: Long
    ) : TransaccionHistorial()

    data class TransaccionCompra(
        override val id: Int,
        override val producto: String,
        override val cantidad: Int,
        override val monto: Double,
        override val fecha: Long
    ) : TransaccionHistorial()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    navController: NavHostController,
    ventaViewModel: VentaViewModel = viewModel(),
    compraViewModel: CompraViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { UserSessionManager.getInstance(context) }
    val usuarioId = sessionManager.getUserId()
    
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    val tabs = listOf("Todos", "Ventas", "Compras")

    val ventasDelDia by ventaViewModel.ventasDelDia.collectAsState()
    val comprasDelDia by compraViewModel.comprasDelDia.collectAsState()
    val totalVentas by ventaViewModel.totalVentas.collectAsState()
    val totalCompras by compraViewModel.totalCompras.collectAsState()
    val conteoVentas by ventaViewModel.conteoVentas.collectAsState()
    val productosMasVendidos by ventaViewModel.productosMasVendidos.collectAsState()
    val comprasSemana by compraViewModel.comprasSemana.collectAsState()

    // Refrescar datos cuando cambie el usuario
    LaunchedEffect(usuarioId) {
        if (usuarioId != -1) {
            ventaViewModel.refrescarVentas()
            compraViewModel.refrescarCompras()
        }
    }

    val todasTransacciones = remember(ventasDelDia, comprasDelDia) {
        val ventas = ventasDelDia.map { venta ->
            TransaccionHistorial.TransaccionVenta(
                id = venta.id,
                producto = venta.nombreProducto,
                cantidad = venta.cantidad,
                monto = venta.total,
                fecha = venta.fecha
            )
        }

        val compras = comprasDelDia.map { compra ->
            TransaccionHistorial.TransaccionCompra(
                id = compra.id,
                producto = compra.nombreProducto,
                cantidad = compra.cantidad,
                monto = compra.costo,
                fecha = compra.fecha
            )
        }

        (ventas + compras).sortedByDescending { it.fecha }
    }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        text = "Historial",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Total Ventas",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "S/ ${String.format("%.2f", totalVentas)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Total Compras",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "S/ ${String.format("%.2f", totalCompras)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección de Reportes
                Text(
                    text = "Reportes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                // Ventas del día
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ventas del Día",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Transacciones",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "$conteoVentas",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Monto Total",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", totalVentas)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }

                // Productos más vendidos
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Productos Más Vendidos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFF6B00),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (productosMasVendidos.isEmpty()) {
                            Text(
                                text = "No hay datos disponibles",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        } else {
                            productosMasVendidos.forEachIndexed { index, producto ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(
                                                    Color(0xFFFF6B00).copy(alpha = 0.1f),
                                                    RoundedCornerShape(6.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFF6B00)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = producto.nombreProducto,
                                            fontSize = 14.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }
                                    Text(
                                        text = "${producto.totalVendido} unidades",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }
                }

                // Compras de la semana
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Compras de la Semana",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (comprasSemana.isEmpty()) {
                            Text(
                                text = "No hay compras en los últimos 7 días",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        } else {
                            val comprasPorDia = comprasSemana.groupBy { compra ->
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(compra.fecha))
                            }
                            comprasPorDia.forEach { (fecha, compras) ->
                                val totalDia = compras.sumOf { it.costo }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Color(0xFF999999),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = fecha,
                                            fontSize = 14.sp,
                                            color = Color(0xFF333333)
                                        )
                                    }
                                    Text(
                                        text = "S/ ${String.format("%.2f", totalDia)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF44336)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF6B00),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFF6B00)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        val transaccionesFiltradas = when (selectedTab) {
            0 -> todasTransacciones
            1 -> todasTransacciones.filterIsInstance<TransaccionHistorial.TransaccionVenta>()
            2 -> todasTransacciones.filterIsInstance<TransaccionHistorial.TransaccionCompra>()
            else -> todasTransacciones
        }

        if (transaccionesFiltradas.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
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
                        text = "No hay transacciones registradas",
                        color = Color(0xFF999999),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(transaccionesFiltradas) { transaccion ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    TransaccionHistorialItem(transaccion)
                }
            }
        }
    }
}

@Composable
fun TransaccionHistorialItem(transaccion: TransaccionHistorial) {
    val esVenta = transaccion is TransaccionHistorial.TransaccionVenta
    val backgroundColor = if (esVenta) Color(0xFF4CAF50) else Color(0xFFFF6B00)
    val icon = if (esVenta) Icons.Default.DateRange else Icons.Default.ShoppingCart
    val tipo = if (esVenta) "Venta" else "Compra"
    val signo = if (esVenta) "+" else "-"
    val colorMonto = if (esVenta) Color(0xFF4CAF50) else Color(0xFFF44336)

    val dateFormat = if (esVenta) {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    } else {
        SimpleDateFormat("dd/MM", Locale.getDefault())
    }
    val fecha = dateFormat.format(Date(transaccion.fecha))

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
                        .background(backgroundColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaccion.producto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Row {
                        Surface(
                            color = if (esVenta) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = tipo,
                                fontSize = 12.sp,
                                color = backgroundColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cant: ${transaccion.cantidad}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "$signo S/ ${String.format("%.2f", transaccion.monto)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorMonto
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (esVenta) Icons.Default.Lock else Icons.Default.DateRange,
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