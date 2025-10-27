package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.CierreCajaRepository
import com.bodeapp.model.CierreCaja
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CierreCajaViewModel(application: Application) : AndroidViewModel(application) {
    private val cierreRepository: CierreCajaRepository

    private val _cierres = MutableStateFlow<List<CierreCaja>>(emptyList())
    val cierres: StateFlow<List<CierreCaja>> = _cierres.asStateFlow()

    private var filtroDesde: Long? = null
    private var filtroHasta: Long? = null

    init {
        val dao = BodeAppDatabase.getDatabase(application).cierreCajaDao()
        cierreRepository = CierreCajaRepository(dao)

        viewModelScope.launch {
            cierreRepository.getAll().collect { lista ->
                _cierres.value = lista
            }
        }
    }

    fun generarCierre(totalVentas: Double, totalCompras: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val cierre = CierreCaja(
                    totalVentas = totalVentas,
                    totalCompras = totalCompras,
                    utilidad = totalVentas - totalCompras,
                )
                cierreRepository.insert(cierre)
                onSuccess()
            } catch (e: Exception) {
                onError("Error al generar cierre: ${e.message}")
            }
        }
    }

    fun filtrar(desdeStr: String, hastaStr: String) {
        viewModelScope.launch {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val desde = if (desdeStr.isNotBlank()) {
                formato.parse(desdeStr)?.let { fecha ->
                    val cal = Calendar.getInstance()
                    cal.time = fecha
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    cal.timeInMillis
                } ?: 0L
            } else 0L

            val hasta = if (hastaStr.isNotBlank()) {
                formato.parse(hastaStr)?.let { fecha ->
                    val cal = Calendar.getInstance()
                    cal.time = fecha
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.set(Calendar.MILLISECOND, 999)
                    cal.timeInMillis
                } ?: System.currentTimeMillis()
            } else System.currentTimeMillis()

            filtroDesde = desde
            filtroHasta = hasta

            cierreRepository.getByFecha(desde, hasta).collect { lista ->
                _cierres.value = lista
            }
        }
    }
}
