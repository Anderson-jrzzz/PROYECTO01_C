package com.bodeapp.data.repository

import com.bodeapp.data.CompraDao
import com.bodeapp.model.Compra
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class CompraRepository(private val compraDao: CompraDao) {

    val allCompras: Flow<List<Compra>> = compraDao.getAllCompras()

    fun getComprasDelDia(): Flow<List<Compra>> {
        val inicioDelDia = getStartOfDay()
        return compraDao.getComprasDelDia(inicioDelDia)
    }

    fun getTotalComprasDelDia(): Flow<Double?> {
        val inicioDelDia = getStartOfDay()
        return compraDao.getTotalComprasDelDia(inicioDelDia)
    }

    suspend fun insertCompra(compra: Compra): Long {
        return compraDao.insertCompra(compra)
    }

    suspend fun deleteCompra(compra: Compra) {
        compraDao.deleteCompra(compra)
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