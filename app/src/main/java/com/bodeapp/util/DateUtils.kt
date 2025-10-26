package com.bodeapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun formatearFecha(timestamp: Long, formato: String = "dd/MM/yyyy"): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat(formato, Locale.getDefault())
        return formatter.format(date)
    }

    fun formatearHora(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }

    fun formatearFechaHora(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }

    fun getInicioDelDia(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getFinDelDia(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}