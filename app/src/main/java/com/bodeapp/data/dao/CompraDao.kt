package com.bodeapp.data

import androidx.room.*
import com.bodeapp.model.Compra
import kotlinx.coroutines.flow.Flow

@Dao
interface CompraDao {

    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getAllCompras(usuarioId: Int): Flow<List<Compra>>

    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun getComprasDelDia(usuarioId: Int, inicioDelDia: Long): Flow<List<Compra>>

    @Query("SELECT SUM(costo) FROM compras WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia")
    fun getTotalComprasDelDia(usuarioId: Int, inicioDelDia: Long): Flow<Double?>

    @Query("SELECT * FROM compras WHERE usuarioId = :usuarioId AND fecha >= :inicioSemana ORDER BY fecha DESC")
    fun getComprasDeLaSemana(usuarioId: Int, inicioSemana: Long): Flow<List<Compra>>

    @Insert
    suspend fun insertCompra(compra: Compra): Long

    @Delete
    suspend fun deleteCompra(compra: Compra)

    @Query("DELETE FROM compras WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia")
    suspend fun deleteComprasDelDia(usuarioId: Int, inicioDelDia: Long)
}