package com.bodeapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bodeapp.model.CierreCaja
import kotlinx.coroutines.flow.Flow

@Dao
interface CierreCajaDao {

    @Query("SELECT * FROM cierres_caja ORDER BY fechaCierre DESC")
    fun getAllCierres(): Flow<List<CierreCaja>>

    @Query("SELECT * FROM cierres_caja WHERE fechaCierre BETWEEN :desde AND :hasta ORDER BY fechaCierre DESC")
    fun getCierresByFecha(desde: Long, hasta: Long): Flow<List<CierreCaja>>

    @Insert
    suspend fun insertCierre(cierre: CierreCaja): Long
}
