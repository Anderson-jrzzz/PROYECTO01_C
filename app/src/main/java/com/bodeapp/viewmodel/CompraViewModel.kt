package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.CompraRepository
import com.bodeapp.data.repository.ProductoRepository
import com.bodeapp.model.Compra
import com.bodeapp.util.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompraViewModel(application: Application) : AndroidViewModel(application) {

    private val compraRepository: CompraRepository
    private val productoRepository: ProductoRepository
    private val sessionManager: UserSessionManager

    private val _comprasDelDia = MutableStateFlow<List<Compra>>(emptyList())
    val comprasDelDia: StateFlow<List<Compra>> = _comprasDelDia.asStateFlow()

    private val _totalCompras = MutableStateFlow(0.0)
    val totalCompras: StateFlow<Double> = _totalCompras.asStateFlow()

    private var comprasJob: kotlinx.coroutines.Job? = null
    private var totalJob: kotlinx.coroutines.Job? = null

    init {
        val compraDao = BodeAppDatabase.getDatabase(application).compraDao()
        val productoDao = BodeAppDatabase.getDatabase(application).productoDao()
        compraRepository = CompraRepository(compraDao)
        productoRepository = ProductoRepository(productoDao)
        sessionManager = UserSessionManager.getInstance(application)

        cargarCompras()
    }

    private fun cargarCompras() {
        comprasJob?.cancel()
        totalJob?.cancel()

        comprasJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                compraRepository.getComprasDelDia(usuarioId).collect { compras ->
                    _comprasDelDia.value = compras
                }
            } else {
                _comprasDelDia.value = emptyList()
            }
        }

        totalJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                compraRepository.getTotalComprasDelDia(usuarioId).collect { total ->
                    _totalCompras.value = total ?: 0.0
                }
            } else {
                _totalCompras.value = 0.0
            }
        }
    }

    fun refrescarCompras() {
        cargarCompras()
    }

    /**
     * Reinicia los contadores de compras del día
     */
    fun reiniciarContadores() {
        viewModelScope.launch {
            _totalCompras.value = 0.0
            _comprasDelDia.value = emptyList()
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