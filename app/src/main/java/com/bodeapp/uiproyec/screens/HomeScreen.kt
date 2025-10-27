package com.bodeapp.uiproyec.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun HomeScreen(navController: NavHostController) {
    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6B00), Color(0xFFFFA726))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5))
    ) {
        // Header con gradiente
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeGradient)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Hola,",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Usuario Demo",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mi Bodega",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }

            // Avatar
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "UD",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Menú principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuCard(
                    title = "Productos",
                    subtitle = "Gestiona tu inventario",
                    icon = Icons.Default.ShoppingCart,
                    backgroundColor = Color(0xFFFF6B00),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("productos") }
                )

                Spacer(modifier = Modifier.width(16.dp))

                MenuCard(
                    title = "Ventas",
                    subtitle = "Registra nuevas ventas",
                    icon = Icons.Default.Email,
                    backgroundColor = Color(0xFFFFA726),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("ventas") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuCard(
                    title = "Compras/Insumos",
                    subtitle = "Registra tus compras",
                    icon = Icons.Default.ShoppingCart,
                    backgroundColor = Color(0xFFFF6B00),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("compras") }
                )

                Spacer(modifier = Modifier.width(16.dp))

                MenuCard(
                    title = "Cierre de Caja",
                    subtitle = "Reportes y resumen",
                    icon = Icons.Default.Lock,
                    backgroundColor = Color(0xFFFFA726),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("cierre") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Historial
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("historial") },
                shape = RoundedCornerShape(16.dp),
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
                            .size(48.dp)
                            .background(Color(0xFFFF6B00), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Historial",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "Ver todas las transacciones",
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cerrar sesión
            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color(0xFFFF6B00)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    color = Color(0xFFFF6B00),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(backgroundColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}