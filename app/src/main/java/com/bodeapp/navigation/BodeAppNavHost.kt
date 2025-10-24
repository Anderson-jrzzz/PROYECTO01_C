package com.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bodeapp.uiproyec.screens.*

@Composable
fun BodeAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("productos") { ProductosScreen(navController) }
        composable("compras") { ComprasScreen(navController) }
        composable("ventas") { VentasScreen(navController) }
        composable("cierre") { CierreCajaScreen(navController) }
    }
}