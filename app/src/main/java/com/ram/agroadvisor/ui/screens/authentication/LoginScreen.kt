package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.ui.navigation.LocalNavController
import com.ram.agroadvisor.ui.navigation.Screen

@Composable
fun LoginScreen() {
    val navController = LocalNavController.current
    val onLoginSuccess: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Welcome.route) { inclusive = true }
        }
    }
    val onRegisterClick: () -> Unit = {navController.navigate(Screen.SignUp.route)}
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val isLoading = uiState is AuthUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            authViewModel.reset()
            onLoginSuccess()
        }
    }

    fun validateEmail(e: String) = when {
        e.isBlank() -> "Email tələb olunur"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() -> "Yanlış email formatı"
        else -> null
    }

    fun validatePassword(p: String) = when {
        p.isBlank() -> "Şifrə tələb olunur"
        else -> null
    }

    LaunchedEffect(email, emailTouched) {
        if (emailTouched) emailError = validateEmail(email)
    }
    LaunchedEffect(password, passwordTouched) {
        if (passwordTouched) passwordError = validatePassword(password)
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Xoş Gəldiniz",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Davam etmək üçün məlumatlarınızı daxil edin",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Xəta mesajı
            if (uiState is AuthUiState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = (uiState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (!emailTouched) emailTouched = true
                },
                label = { Text("Email") },
                singleLine = true,
                isError = emailError != null,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                },
                trailingIcon = {
                    if (emailError != null) Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (!passwordTouched) passwordTouched = true
                },
                label = { Text("Şifrə") },
                singleLine = true,
                isError = passwordError != null,
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (passwordError != null) Text(passwordError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                },
                trailingIcon = {
                    if (passwordError != null) {
                        Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                    } else {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    emailTouched = true
                    passwordTouched = true
                    emailError = validateEmail(email)
                    passwordError = validatePassword(password)
                    if (emailError == null && passwordError == null) {
                        authViewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Daxil ol", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onRegisterClick, enabled = !isLoading) {
                Text("Hesabınız yoxdur? Qeydiyyat", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}