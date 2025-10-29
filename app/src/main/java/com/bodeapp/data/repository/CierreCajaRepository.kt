package com.bodeapp.data.repository

import com.bodeapp.data.CierreCajaDao
import com.bodeapp.model.CierreCaja
import kotlinx.coroutines.flow.Flow

class CierreCajaRepository(private val dao: CierreCajaDao) {
    fun getAll(usuarioId: Int): Flow<List<CierreCaja>> = dao.getAllCierres(usuarioId)
    fun getByFecha(usuarioId: Int, desde: Long, hasta: Long): Flow<List<CierreCaja>> = dao.getCierresByFecha(usuarioId, desde, hasta)
    suspend fun insert(cierre: CierreCaja): Long = dao.insertCierre(cierre)
}
