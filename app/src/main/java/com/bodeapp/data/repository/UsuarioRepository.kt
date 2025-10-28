package com.bodeapp.data.repository

import com.bodeapp.data.UsuarioDao
import com.bodeapp.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun insertUsuario(usuario: Usuario): Long {
        return usuarioDao.insertUsuario(usuario)
    }

    suspend fun updateUsuario(usuario: Usuario) {
        usuarioDao.updateUsuario(usuario)
    }

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    suspend fun getUserByEmail(email: String): Usuario? {
        return usuarioDao.getUserByEmail(email)
    }
}
