package com.bodeapp.data

import androidx.room.*
import com.bodeapp.model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Query("SELECT * FROM ventas WHERE usuarioId = :usuarioId AND (:productoId IS NULL OR productoId = :productoId) AND fecha >= :fechaDesde AND fecha <= :fechaHasta ORDER BY fecha DESC")
    fun getVentasFiltradas(usuarioId: Int, productoId: Int?, fechaDesde: Long, fechaHasta: Long): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getAllVentas(usuarioId: Int): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun getVentasDelDia(usuarioId: Int, inicioDelDia: Long): Flow<List<Venta>>

    @Query("SELECT SUM(total) FROM ventas WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia")
    fun getTotalVentasDelDia(usuarioId: Int, inicioDelDia: Long): Flow<Double?>

    @Query("SELECT nombreProducto, SUM(cantidad) as totalVendido FROM ventas WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia GROUP BY nombreProducto ORDER BY totalVendido DESC LIMIT 5")
    fun getProductosMasVendidos(usuarioId: Int, inicioDelDia: Long): Flow<List<ProductoVendido>>

    @Insert
    suspend fun insertVenta(venta: Venta): Long

    @Delete
    suspend fun deleteVenta(venta: Venta)

    @Query("DELETE FROM ventas WHERE usuarioId = :usuarioId AND fecha >= :inicioDelDia")
    suspend fun deleteVentasDelDia(usuarioId: Int, inicioDelDia: Long)
}

data class ProductoVendido(
    val nombreProducto: String,
    val totalVendido: Int
)