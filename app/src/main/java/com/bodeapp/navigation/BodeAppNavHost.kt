package com.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bodeapp.uiproyec.screens.*

@Composable
fun BodeAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("productos") { ProductosScreen(navController) }
        composable("ventas") { VentasScreen(navController) }
        composable("compras") { ComprasScreen(navController) }
        composable("cierre") { CierreCajaScreen(navController) }
        composable("historial") { HistorialScreen(navController) }
    }
}