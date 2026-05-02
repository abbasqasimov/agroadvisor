package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
fun SignUpScreen() {


    val navController = LocalNavController.current
    val onSignUpSuccess: () -> Unit = {
        navController.navigate(Screen.FieldDetails.route)
    }
    val onBackToLogin: () -> Unit = {navController.popBackStack()}
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var surnameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameTouched by remember { mutableStateOf(false) }
    var surnameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val isLoading = uiState is AuthUiState.Loading

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            authViewModel.reset()
            onSignUpSuccess()
        }
    }

    fun validateName(n: String) = when {
        n.isBlank() -> "Ad tələb olunur"
        n.length < 2 -> "Ad minimum 2 simvol olmalıdır"
        else -> null
    }
    fun validateSurname(s: String) = when {
        s.isBlank() -> "Soyad tələb olunur"
        s.length < 2 -> "Soyad minimum 2 simvol olmalıdır"
        else -> null
    }
    fun validateEmail(e: String) = when {
        e.isBlank() -> "Email tələb olunur"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches() -> "Yanlış email formatı"
        else -> null
    }
    fun validatePassword(p: String) = when {
        p.isBlank() -> "Şifrə tələb olunur"
        p.length < 8 -> "Şifrə minimum 8 simvol olmalıdır"
        !p.any { it.isUpperCase() } -> "Ən az 1 böyük hərf olmalıdır"
        !p.any { it.isLowerCase() } -> "Ən az 1 kiçik hərf olmalıdır"
        !p.any { it.isDigit() } -> "Ən az 1 rəqəm olmalıdır"
        else -> null
    }

    LaunchedEffect(name, nameTouched) { if (nameTouched) nameError = validateName(name) }
    LaunchedEffect(surname, surnameTouched) { if (surnameTouched) surnameError = validateSurname(surname) }
    LaunchedEffect(email, emailTouched) { if (emailTouched) emailError = validateEmail(email) }
    LaunchedEffect(password, passwordTouched) { if (passwordTouched) passwordError = validatePassword(password) }

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
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Hesab Yarat",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Başlamaq üçün məlumatlarınızı daxil edin",
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
                value = name,
                onValueChange = { name = it; if (!nameTouched) nameTouched = true },
                label = { Text("Ad") },
                singleLine = true,
                isError = nameError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (nameError != null) Text(nameError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                },
                trailingIcon = {
                    if (nameError != null) Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = surname,
                onValueChange = { surname = it; if (!surnameTouched) surnameTouched = true },
                label = { Text("Soyad") },
                singleLine = true,
                isError = surnameError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (surnameError != null) Text(surnameError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                },
                trailingIcon = {
                    if (surnameError != null) Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; if (!emailTouched) emailTouched = true },
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
                onValueChange = { password = it; if (!passwordTouched) passwordTouched = true },
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
                    nameTouched = true
                    surnameTouched = true
                    emailTouched = true
                    passwordTouched = true
                    nameError = validateName(name)
                    surnameError = validateSurname(surname)
                    emailError = validateEmail(email)
                    passwordError = validatePassword(password)
                    if (nameError == null && surnameError == null &&
                        emailError == null && passwordError == null
                    ) {
                        authViewModel.register(name, surname, email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Qeydiyyat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin, enabled = !isLoading) {
                Text("Artıq hesabınız var? Daxil olun", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}