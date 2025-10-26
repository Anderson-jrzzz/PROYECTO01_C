package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.ProductoRepository
import com.bodeapp.data.repository.VentaRepository
import com.bodeapp.model.Venta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VentaViewModel(application: Application) : AndroidViewModel(application) {

    private val ventaRepository: VentaRepository
    private val productoRepository: ProductoRepository

    private val _ventasDelDia = MutableStateFlow<List<Venta>>(emptyList())
    val ventasDelDia: StateFlow<List<Venta>> = _ventasDelDia.asStateFlow()

    private val _totalVentas = MutableStateFlow(0.0)
    val totalVentas: StateFlow<Double> = _totalVentas.asStateFlow()

    init {
        val ventaDao = BodeAppDatabase.getDatabase(application).ventaDao()
        val productoDao = BodeAppDatabase.getDatabase(application).productoDao()
        ventaRepository = VentaRepository(ventaDao)
        productoRepository = ProductoRepository(productoDao)

        viewModelScope.launch {
            ventaRepository.getVentasDelDia().collect { ventas ->
                _ventasDelDia.value = ventas
            }
        }

        viewModelScope.launch {
            ventaRepository.getTotalVentasDelDia().collect { total ->
                _totalVentas.value = total ?: 0.0
            }
        }
    }

    fun registrarVenta(venta: Venta, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar stock disponible
                val stockActual = productoRepository.getStock(venta.productoId)

                if (stockActual == null) {
                    onError("Producto no encontrado")
                    return@launch
                }

                if (stockActual < venta.cantidad) {
                    onError("Stock insuficiente. Disponible: $stockActual")
                    return@launch
                }

                // Registrar venta
                ventaRepository.insertVenta(venta)

                // Reducir stock
                productoRepository.reducirStock(venta.productoId, venta.cantidad)

                onSuccess()
            } catch (e: Exception) {
                onError("Error al registrar venta: ${e.message}")
            }
        }
    }
}