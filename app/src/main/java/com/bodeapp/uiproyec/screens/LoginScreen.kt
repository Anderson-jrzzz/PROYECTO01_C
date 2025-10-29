package com.bodeapp.uiproyec.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
//import androidx.compose.material.icons.filled.Store
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bodeapp.viewmodel.UsuarioViewModel
import com.bodeapp.viewmodel.LoginResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: UsuarioViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val loginResult by viewModel.loginResult.collectAsState()

    // Observar el resultado del login
    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is LoginResult.Success -> {
                isLoading = false
                viewModel.resetLoginResult()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is LoginResult.Error -> {
                isLoading = false
                errorMessage = result.message
                showError = true
                viewModel.resetLoginResult()
            }
            null -> {
                // No hacer nada
            }
        }
    }

    val orangeGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFF6B00),
            Color(0xFFFFA726)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        brush = orangeGradient,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Título
            Text(
                text = "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field
            Text(
                text = "Email o Usuario",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("tu@email.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = null,
                        tint = Color(0xFF999999)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B00),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Text(
                text = "Contraseña",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color(0xFF999999)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Rounded.Lock else Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = Color(0xFF999999)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B00),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Espacio antes del botón de inicio de sesión

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    // Validaciones
                    when {
                        email.isEmpty() -> {
                            errorMessage = "Por favor ingresa tu email"
                            showError = true
                        }
                        password.isEmpty() -> {
                            errorMessage = "Por favor ingresa tu contraseña"
                            showError = true
                        }
                        else -> {
                            // Validar credenciales en la base de datos
                            isLoading = true
                            viewModel.login(email, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(orangeGradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Iniciar Sesión",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register link
            Row {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color(0xFF666666),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = { navController.navigate("register") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Regístrate",
                        color = Color(0xFFFF6B00),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Snackbar de error
        if (showError) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000)
                showError = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Snackbar(
                    containerColor = Color(0xFFF44336),
                    contentColor = Color.White
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}