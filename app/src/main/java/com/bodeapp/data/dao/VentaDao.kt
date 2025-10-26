package com.bodeapp.data

import androidx.room.*
import com.bodeapp.model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getAllVentas(): Flow<List<Venta>>

    @Query("SELECT * FROM ventas WHERE fecha >= :inicioDelDia ORDER BY fecha DESC")
    fun getVentasDelDia(inicioDelDia: Long): Flow<List<Venta>>

    @Query("SELECT SUM(total) FROM ventas WHERE fecha >= :inicioDelDia")
    fun getTotalVentasDelDia(inicioDelDia: Long): Flow<Double?>

    @Query("SELECT nombreProducto, SUM(cantidad) as totalVendido FROM ventas WHERE fecha >= :inicioDelDia GROUP BY nombreProducto ORDER BY totalVendido DESC LIMIT 5")
    fun getProductosMasVendidos(inicioDelDia: Long): Flow<List<ProductoVendido>>

    @Insert
    suspend fun insertVenta(venta: Venta): Long

    @Delete
    suspend fun deleteVenta(venta: Venta)

    @Query("DELETE FROM ventas WHERE fecha >= :inicioDelDia")
    suspend fun deleteVentasDelDia(inicioDelDia: Long)
}

data class ProductoVendido(
    val nombreProducto: String,
    val totalVendido: Int
)