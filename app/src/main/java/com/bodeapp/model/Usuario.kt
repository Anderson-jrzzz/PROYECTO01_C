package com.bodeapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreCompleto: String,
    val email: String,
    val nombreTienda: String,
    val password: String,
    val fechaRegistro: Long = System.currentTimeMillis()
)