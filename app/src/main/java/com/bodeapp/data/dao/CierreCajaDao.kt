package com.bodeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bodeapp.model.CierreCaja
import kotlinx.coroutines.flow.Flow

@Dao
interface CierreCajaDao {

    @Query("SELECT * FROM cierres_caja WHERE usuarioId = :usuarioId ORDER BY fechaCierre DESC")
    fun getAllCierres(usuarioId: Int): Flow<List<CierreCaja>>

    @Query("SELECT * FROM cierres_caja WHERE usuarioId = :usuarioId AND fechaCierre BETWEEN :desde AND :hasta ORDER BY fechaCierre DESC")
    fun getCierresByFecha(usuarioId: Int, desde: Long, hasta: Long): Flow<List<CierreCaja>>

    @Insert
    suspend fun insertCierre(cierre: CierreCaja): Long
}
