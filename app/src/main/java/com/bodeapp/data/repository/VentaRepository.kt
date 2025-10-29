package com.bodeapp.data.repository

import com.bodeapp.data.ProductoVendido
import com.bodeapp.data.VentaDao
import com.bodeapp.model.Venta
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class VentaRepository(private val ventaDao: VentaDao) {
    fun getVentasFiltradas(usuarioId: Int, productoId: Int?, fechaDesde: Long, fechaHasta: Long): Flow<List<Venta>> {
        return ventaDao.getVentasFiltradas(usuarioId, productoId, fechaDesde, fechaHasta)
    }

    fun getAllVentas(usuarioId: Int): Flow<List<Venta>> {
        return ventaDao.getAllVentas(usuarioId)
    }

    fun getVentasDelDia(usuarioId: Int): Flow<List<Venta>> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getVentasDelDia(usuarioId, inicioDelDia)
    }

    fun getTotalVentasDelDia(usuarioId: Int): Flow<Double?> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getTotalVentasDelDia(usuarioId, inicioDelDia)
    }

    fun getProductosMasVendidos(usuarioId: Int): Flow<List<ProductoVendido>> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getProductosMasVendidos(usuarioId, inicioDelDia)
    }

    suspend fun insertVenta(venta: Venta): Long {
        return ventaDao.insertVenta(venta)
    }

    suspend fun deleteVenta(venta: Venta) {
        ventaDao.deleteVenta(venta)
    }

    suspend fun deleteVentasDelDia(usuarioId: Int) {
        val inicioDelDia = getStartOfDay()
        ventaDao.deleteVentasDelDia(usuarioId, inicioDelDia)
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}