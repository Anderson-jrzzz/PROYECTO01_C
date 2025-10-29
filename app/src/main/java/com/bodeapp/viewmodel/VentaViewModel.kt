package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.ProductoRepository
import com.bodeapp.data.repository.VentaRepository
import com.bodeapp.model.Venta
import com.bodeapp.util.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VentaViewModel(application: Application) : AndroidViewModel(application) {
    private val _ventasFiltradas = MutableStateFlow<List<Venta>>(emptyList())
    val ventasFiltradas: StateFlow<List<Venta>> = _ventasFiltradas.asStateFlow()

    private var filtroProductoId: Int? = null
    private var filtroFechaDesde: Long? = null
    private var filtroFechaHasta: Long? = null

    private val ventaRepository: VentaRepository
    private val productoRepository: ProductoRepository
    private val sessionManager: UserSessionManager

    private val _ventasDelDia = MutableStateFlow<List<Venta>>(emptyList())
    val ventasDelDia: StateFlow<List<Venta>> = _ventasDelDia.asStateFlow()

    private val _totalVentas = MutableStateFlow(0.0)
    val totalVentas: StateFlow<Double> = _totalVentas.asStateFlow()

    private val _conteoVentas = MutableStateFlow(0)
    val conteoVentas: StateFlow<Int> = _conteoVentas.asStateFlow()

    private val _productosMasVendidos = MutableStateFlow<List<com.bodeapp.data.ProductoVendido>>(emptyList())
    val productosMasVendidos: StateFlow<List<com.bodeapp.data.ProductoVendido>> = _productosMasVendidos.asStateFlow()

    private var ventasJob: kotlinx.coroutines.Job? = null
    private var totalJob: kotlinx.coroutines.Job? = null
    private var conteoJob: kotlinx.coroutines.Job? = null
    private var productosJob: kotlinx.coroutines.Job? = null

    init {
        val ventaDao = BodeAppDatabase.getDatabase(application).ventaDao()
        val productoDao = BodeAppDatabase.getDatabase(application).productoDao()
        ventaRepository = VentaRepository(ventaDao)
        productoRepository = ProductoRepository(productoDao)
        sessionManager = UserSessionManager.getInstance(application)

        cargarVentas()
    }

    private fun cargarVentas() {
        ventasJob?.cancel()
        totalJob?.cancel()
        conteoJob?.cancel()
        productosJob?.cancel()

        ventasJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                ventaRepository.getVentasDelDia(usuarioId).collect { ventas ->
                    _ventasDelDia.value = ventas
                }
            } else {
                _ventasDelDia.value = emptyList()
            }
        }

        totalJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                ventaRepository.getTotalVentasDelDia(usuarioId).collect { total ->
                    _totalVentas.value = total ?: 0.0
                }
            } else {
                _totalVentas.value = 0.0
            }
        }

        conteoJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                ventaRepository.getConteoVentasDelDia(usuarioId).collect { conteo ->
                    _conteoVentas.value = conteo
                }
            } else {
                _conteoVentas.value = 0
            }
        }

        productosJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                ventaRepository.getProductosMasVendidos(usuarioId).collect { productos ->
                    _productosMasVendidos.value = productos
                }
            } else {
                _productosMasVendidos.value = emptyList()
            }
        }
    }

    fun refrescarVentas() {
        cargarVentas()
    }

    /**
     * Reinicia los contadores de ventas del día
     */
    fun reiniciarContadores() {
        viewModelScope.launch {
            _totalVentas.value = 0.0
            _ventasDelDia.value = emptyList()
            _conteoVentas.value = 0
            _productosMasVendidos.value = emptyList()
        }
    }

    fun filtrarVentas(productoId: Int?, fechaDesde: String, fechaHasta: String) {
        viewModelScope.launch {
            try {
                val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val desde = if (fechaDesde.isNotBlank()) {
                    formato.parse(fechaDesde)?.let { fecha ->
                        val calendar = Calendar.getInstance()
                        calendar.time = fecha
                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        calendar.timeInMillis
                    } ?: 0L
                } else {
                    0L
                }

                val hasta = if (fechaHasta.isNotBlank()) {
                    formato.parse(fechaHasta)?.let { fecha ->
                        val calendar = Calendar.getInstance()
                        calendar.time = fecha
                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        calendar.set(Calendar.MILLISECOND, 999)
                        calendar.timeInMillis
                    } ?: System.currentTimeMillis()
                } else {
                    System.currentTimeMillis()
                }

                filtroProductoId = productoId
                filtroFechaDesde = desde
                filtroFechaHasta = hasta

                val usuarioId = sessionManager.getUserId()
                if (usuarioId != -1) {
                    ventaRepository.getVentasFiltradas(usuarioId, productoId, desde, hasta).collect { ventas ->
                        _ventasFiltradas.value = ventas
                    }
                }
            } catch (e: Exception) {
                _ventasFiltradas.value = emptyList()
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