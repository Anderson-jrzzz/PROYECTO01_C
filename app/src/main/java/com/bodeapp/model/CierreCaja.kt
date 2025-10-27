package com.bodeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cierres_caja")
data class CierreCaja(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val totalVentas: Double,
    val totalCompras: Double,
    val utilidad: Double,
    val fechaCierre: Long = System.currentTimeMillis()
)
