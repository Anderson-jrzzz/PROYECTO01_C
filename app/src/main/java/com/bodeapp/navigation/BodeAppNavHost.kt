package com.bodeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bodeapp.uiproyec.screens.CierreCajaScreen
import com.bodeapp.uiproyec.screens.ComprasScreen
import com.bodeapp.uiproyec.screens.HomeScreen
import com.bodeapp.uiproyec.screens.LoginScreen
import com.bodeapp.uiproyec.screens.ProductosScreen
import com.bodeapp.uiproyec.screens.RegisterScreen
import com.bodeapp.uiproyec.screens.VentasScreen


@Composable
fun BodeAppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {

        composable("login") { LoginScreen() }
        composable("register") { RegisterScreen() }
        composable("home") { HomeScreen() }
        composable("productos") { ProductosScreen() }
        composable("ventas") { VentasScreen() }
        composable("compras") { ComprasScreen() }
        composable("cierre") { CierreCajaScreen() }
    }
}