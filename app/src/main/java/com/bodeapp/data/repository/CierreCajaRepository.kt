package com.bodeapp.data.repository

import com.bodeapp.data.CierreCajaDao
import com.bodeapp.model.CierreCaja
import kotlinx.coroutines.flow.Flow

class CierreCajaRepository(private val dao: CierreCajaDao) {
    fun getAll(): Flow<List<CierreCaja>> = dao.getAllCierres()
    fun getByFecha(desde: Long, hasta: Long): Flow<List<CierreCaja>> = dao.getCierresByFecha(desde, hasta)
    suspend fun insert(cierre: CierreCaja): Long = dao.insertCierre(cierre)
}
