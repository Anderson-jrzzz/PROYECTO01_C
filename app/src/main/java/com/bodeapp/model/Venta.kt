package com.bodeapp.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "ventas",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["usuarioId"]), androidx.room.Index(value = ["productoId"])]
)
data class Venta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val productoId: Int,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double,
    val fecha: Long = System.currentTimeMillis()
)