package com.bodeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bodeapp.data.BodeAppDatabase
import com.bodeapp.data.repository.UsuarioRepository
import com.bodeapp.model.Usuario
import com.bodeapp.util.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository
    private val sessionManager: UserSessionManager

    private val _loginResult = MutableStateFlow<LoginResult?>(null)
    val loginResult: StateFlow<LoginResult?> = _loginResult.asStateFlow()

    private val _registerResult = MutableStateFlow<RegisterResult?>(null)
    val registerResult: StateFlow<RegisterResult?> = _registerResult.asStateFlow()

    init {
        val usuarioDao = BodeAppDatabase.getDatabase(application).usuarioDao()
        repository = UsuarioRepository(usuarioDao)
        sessionManager = UserSessionManager.getInstance(application)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val usuario = repository.login(email, password)
                if (usuario != null) {
                    // Guardar sesión del usuario
                    sessionManager.saveUserSession(
                        userId = usuario.id,
                        email = usuario.email,
                        userName = usuario.nombreCompleto,
                        storeName = usuario.nombreTienda
                    )
                    _loginResult.value = LoginResult.Success(usuario)
                } else {
                    _loginResult.value = LoginResult.Error("Correo o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun register(nombreCompleto: String, email: String, nombreTienda: String, password: String) {
        viewModelScope.launch {
            try {
                // Verificar si el email ya está registrado
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    _registerResult.value = RegisterResult.Error("Este correo ya está registrado")
                    return@launch
                }

                // Crear nuevo usuario
                val nuevoUsuario = Usuario(
                    nombreCompleto = nombreCompleto,
                    email = email,
                    nombreTienda = nombreTienda,
                    password = password
                )

                val userId = repository.insertUsuario(nuevoUsuario)
                if (userId > 0) {
                    val usuarioCreado = nuevoUsuario.copy(id = userId.toInt())
                    // Guardar sesión automáticamente después del registro
                    sessionManager.saveUserSession(
                        userId = usuarioCreado.id,
                        email = usuarioCreado.email,
                        userName = usuarioCreado.nombreCompleto,
                        storeName = usuarioCreado.nombreTienda
                    )
                    _registerResult.value = RegisterResult.Success(usuarioCreado)
                } else {
                    _registerResult.value = RegisterResult.Error("Error al crear la cuenta")
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Error al registrar: ${e.message}")
            }
        }
    }

    fun resetLoginResult() {
        _loginResult.value = null
    }

    fun resetRegisterResult() {
        _registerResult.value = null
    }
    
    fun logout() {
        sessionManager.clearSession()
    }
    
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    fun getCurrentUserId(): Int {
        return sessionManager.getUserId()
    }
}

sealed class LoginResult {
    data class Success(val usuario: Usuario) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    data class Success(val usuario: Usuario) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}
