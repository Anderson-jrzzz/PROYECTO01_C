package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.ProductoRepository
import com.bodeapp.model.Producto
import com.bodeapp.util.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    private val sessionManager: UserSessionManager

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        val productoDao = BodeAppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)
        sessionManager = UserSessionManager.getInstance(application)

        cargarProductos()
    }

    private var productosJob: kotlinx.coroutines.Job? = null

    private fun cargarProductos() {
        productosJob?.cancel()
        productosJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                repository.getAllProductos(usuarioId).collect { listaProductos ->
                    _productos.value = listaProductos
                }
            } else {
                _productos.value = emptyList()
            }
        }
    }

    fun refrescarProductos() {
        cargarProductos()
    }

    fun insertProducto(producto: Producto) {
        viewModelScope.launch {
            repository.insertProducto(producto)
        }
    }

    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            repository.updateProducto(producto)
        }
    }

    fun deleteProducto(producto: Producto) {
        viewModelScope.launch {
            repository.deleteProducto(producto)
        }
    }

    suspend fun getProductoById(id: Int): Producto? {
        return repository.getProductoById(id)
    }

    suspend fun getStock(id: Int): Int? {
        return repository.getStock(id)
    }
}