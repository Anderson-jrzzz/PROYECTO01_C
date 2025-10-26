package com.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bodeapp.uiproyec.screens.*
import com.bodeapp.viewmodel.CompraViewModel
import com.bodeapp.viewmodel.ProductoViewModel
import com.bodeapp.viewmodel.VentaViewModel

@Composable
fun BodeAppNavHost(navController: NavHostController) {
    // ViewModels compartidos entre pantallas
    val productoViewModel: ProductoViewModel = viewModel()
    val ventaViewModel: VentaViewModel = viewModel()
    val compraViewModel: CompraViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Pantallas de autenticación
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        // Pantalla principal
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Pantallas de funcionalidad
        composable("productos") {
            ProductosScreen(
                navController = navController,
                viewModel = productoViewModel
            )
        }

        composable("ventas") {
            VentasScreen(
                navController = navController,
                productoViewModel = productoViewModel,
                ventaViewModel = ventaViewModel
            )
        }

        composable("compras") {
            ComprasScreen(
                navController = navController,
                productoViewModel = productoViewModel,
                compraViewModel = compraViewModel
            )
        }

        composable("cierre") {
            CierreCajaScreen(
                navController = navController,
                ventaViewModel = ventaViewModel,
                compraViewModel = compraViewModel
            )
        }

        composable("historial") {
            HistorialScreen(
                navController = navController,
                ventaViewModel = ventaViewModel,
                compraViewModel = compraViewModel
            )
        }
    }
}