package com.bodeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bodeapp.model.*

@Database(
    entities = [
        Producto::class,
        Venta::class,
        Compra::class,
        Usuario::class,
        CierreCaja::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BodeAppDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun compraDao(): CompraDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cierreCajaDao(): CierreCajaDao

    companion object {
        @Volatile
        private var INSTANCE: BodeAppDatabase? = null

        fun getDatabase(context: Context): BodeAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BodeAppDatabase::class.java,
                    "bodeapp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}