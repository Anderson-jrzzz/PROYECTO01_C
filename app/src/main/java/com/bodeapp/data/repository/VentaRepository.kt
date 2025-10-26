package com.bodeapp.data.repository

import com.bodeapp.data.ProductoVendido
import com.bodeapp.data.VentaDao
import com.bodeapp.model.Venta
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class VentaRepository(private val ventaDao: VentaDao) {
    fun getVentasFiltradas(productoId: Int?, fechaDesde: Long, fechaHasta: Long): Flow<List<Venta>> {
        return ventaDao.getVentasFiltradas(productoId, fechaDesde, fechaHasta)
    }

    val allVentas: Flow<List<Venta>> = ventaDao.getAllVentas()

    fun getVentasDelDia(): Flow<List<Venta>> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getVentasDelDia(inicioDelDia)
    }

    fun getTotalVentasDelDia(): Flow<Double?> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getTotalVentasDelDia(inicioDelDia)
    }

    fun getProductosMasVendidos(): Flow<List<ProductoVendido>> {
        val inicioDelDia = getStartOfDay()
        return ventaDao.getProductosMasVendidos(inicioDelDia)
    }

    suspend fun insertVenta(venta: Venta): Long {
        return ventaDao.insertVenta(venta)
    }

    suspend fun deleteVenta(venta: Venta) {
        ventaDao.deleteVenta(venta)
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