package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.CompraRepository
import com.bodeapp.data.repository.ProductoRepository
import com.bodeapp.model.Compra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompraViewModel(application: Application) : AndroidViewModel(application) {

    private val compraRepository: CompraRepository
    private val productoRepository: ProductoRepository

    private val _comprasDelDia = MutableStateFlow<List<Compra>>(emptyList())
    val comprasDelDia: StateFlow<List<Compra>> = _comprasDelDia.asStateFlow()

    private val _totalCompras = MutableStateFlow(0.0)
    val totalCompras: StateFlow<Double> = _totalCompras.asStateFlow()

    init {
        val compraDao = BodeAppDatabase.getDatabase(application).compraDao()
        val productoDao = BodeAppDatabase.getDatabase(application).productoDao()
        compraRepository = CompraRepository(compraDao)
        productoRepository = ProductoRepository(productoDao)

        viewModelScope.launch {
            compraRepository.getComprasDelDia().collect { compras ->
                _comprasDelDia.value = compras
            }
        }

        viewModelScope.launch {
            compraRepository.getTotalComprasDelDia().collect { total ->
                _totalCompras.value = total ?: 0.0
            }
        }
    }

    fun registrarCompra(compra: Compra, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Registrar compra
                compraRepository.insertCompra(compra)

                // Si tiene productoId, aumentar stock
                compra.productoId?.let { id ->
                    productoRepository.aumentarStock(id, compra.cantidad)
                }

                onSuccess()
            } catch (e: Exception) {
                onError("Error al registrar compra: ${e.message}")
            }
        }
    }
}