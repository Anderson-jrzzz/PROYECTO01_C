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
import com.bodeapp.model.Venta
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
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Todos", "Ventas", "Compras")

    val ventasDelDia by ventaViewModel.ventasDelDia.collectAsState()
    val comprasDelDia by compraViewModel.comprasDelDia.collectAsState()
    val totalVentas by ventaViewModel.totalVentas.collectAsState()
    val totalCompras by compraViewModel.totalCompras.collectAsState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            val transaccionesFiltradas = when (selectedTab) {
                0 -> todasTransacciones
                1 -> todasTransacciones.filterIsInstance<TransaccionHistorial.TransaccionVenta>()
                2 -> todasTransacciones.filterIsInstance<TransaccionHistorial.TransaccionCompra>()
                else -> todasTransacciones
            }

            if (transaccionesFiltradas.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
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
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transaccionesFiltradas) { transaccion ->
                        TransaccionHistorialItem(transaccion)
                    }
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