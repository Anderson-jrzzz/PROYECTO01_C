package com.bodeapp.data

import com.bodeapp.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    val allProductos: Flow<List<Producto>> = productoDao.getAllProductos()

    suspend fun insertProducto(producto: Producto): Long {
        return productoDao.insertProducto(producto)
    }

    suspend fun updateProducto(producto: Producto) {
        productoDao.updateProducto(producto)
    }

    suspend fun deleteProducto(producto: Producto) {
        productoDao.deleteProducto(producto)
    }

    suspend fun getProductoById(id: Int): Producto? {
        return productoDao.getProductoById(id)
    }

    suspend fun buscarProductoPorNombre(nombre: String): List<Producto> {
        return productoDao.buscarProductoPorNombre(nombre)
    }

    suspend fun reducirStock(id: Int, cantidad: Int) {
        productoDao.reducirStock(id, cantidad)
    }

    suspend fun aumentarStock(id: Int, cantidad: Int) {
        productoDao.aumentarStock(id, cantidad)
    }

    suspend fun getStock(id: Int): Int? {
        return productoDao.getStock(id)
    }
}