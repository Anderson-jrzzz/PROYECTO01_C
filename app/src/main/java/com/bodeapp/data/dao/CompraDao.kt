package com.bodeapp.data

import androidx.room.*
import com.bodeapp.model.Compra
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Query("SELECT * FROM compras ORDER BY fecha DESC")
    fun getAllCompras(): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun getComprasDelDia(inicioDelDia: Long): Flow<List<Compra>>

    @Query("SELECT SUM(costo) FROM compras WHERE fecha >= :inicioDelDia")
    fun getTotalComprasDelDia(inicioDelDia: Long): Flow<Double?>

    @Insert
    suspend fun insertCompra(compra: Compra): Long

    @Delete
    suspend fun deleteCompra(compra: Compra)
}