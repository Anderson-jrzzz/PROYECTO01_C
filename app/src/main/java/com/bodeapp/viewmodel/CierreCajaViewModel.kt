package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.CierreCajaRepository
import com.bodeapp.data.repository.CompraRepository
import com.bodeapp.data.repository.VentaRepository
import com.bodeapp.model.CierreCaja
import com.bodeapp.util.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CierreCajaViewModel(application: Application) : AndroidViewModel(application) {
    private val cierreRepository: CierreCajaRepository
    private val ventaRepository: VentaRepository
    private val compraRepository: CompraRepository
    private val sessionManager: UserSessionManager

    private val _cierres = MutableStateFlow<List<CierreCaja>>(emptyList())
    val cierres: StateFlow<List<CierreCaja>> = _cierres.asStateFlow()

    private var filtroDesde: Long? = null
    private var filtroHasta: Long? = null

    private var cierresJob: kotlinx.coroutines.Job? = null

    init {
        val db = BodeAppDatabase.getDatabase(application)
        cierreRepository = CierreCajaRepository(db.cierreCajaDao())
        ventaRepository = VentaRepository(db.ventaDao())
        compraRepository = CompraRepository(db.compraDao())
        sessionManager = UserSessionManager.getInstance(application)

        cargarCierres()
    }

    private fun cargarCierres() {
        cierresJob?.cancel()
        cierresJob = viewModelScope.launch {
            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                cierreRepository.getAll(usuarioId).collect { lista ->
                    _cierres.value = lista
                }
            } else {
                _cierres.value = emptyList()
            }
        }
    }

    fun refrescarCierres() {
        cargarCierres()
    }

    fun generarCierre(totalVentas: Double, totalCompras: Double, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val usuarioId = sessionManager.getUserId()
                if (usuarioId == -1) {
                    onError("Usuario no autenticado")
                    return@launch
                }
                
                val cierre = CierreCaja(
                    usuarioId = usuarioId,
                    totalVentas = totalVentas,
                    totalCompras = totalCompras,
                    utilidad = totalVentas - totalCompras,
                )
                cierreRepository.insert(cierre)
                try {
                    ventaRepository.deleteVentasDelDia(usuarioId)
                    compraRepository.deleteComprasDelDia(usuarioId)
                    onSuccess()
                } catch (e: Exception) {
                    onError("Cierre generado, pero error al reiniciar datos: ${e.message}")
                }
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

            val usuarioId = sessionManager.getUserId()
            if (usuarioId != -1) {
                cierreRepository.getByFecha(usuarioId, desde, hasta).collect { lista ->
                    _cierres.value = lista
                }
            }
        }
    }
}
