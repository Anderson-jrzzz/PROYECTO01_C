package com.bodeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compras")
data class Compra(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: Int?,
    val nombreProducto: String,
    val cantidad: Int,
    val costo: Double,
    val fecha: Long = System.currentTimeMillis()
)