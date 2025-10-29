package com.bodeapp.data

import androidx.room.*
import com.bodeapp.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Query("SELECT * FROM productos WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun getAllProductos(usuarioId: Int): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE usuarioId = :usuarioId AND nombre LIKE '%' || :nombre || '%'")
    suspend fun buscarProductoPorNombre(usuarioId: Int, nombre: String): List<Producto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto): Long

    @Update
    suspend fun updateProducto(producto: Producto)

    @Delete
    suspend fun deleteProducto(producto: Producto)

    @Query("UPDATE productos SET stock = stock - :cantidad WHERE id = :id")
    suspend fun reducirStock(id: Int, cantidad: Int)

    @Query("UPDATE productos SET stock = stock + :cantidad WHERE id = :id")
    suspend fun aumentarStock(id: Int, cantidad: Int)

    @Query("SELECT stock FROM productos WHERE id = :id")
    suspend fun getStock(id: Int): Int?
}