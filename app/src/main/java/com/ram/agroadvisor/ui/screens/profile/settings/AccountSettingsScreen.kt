package com.ram.agroadvisor.ui.screens.profile.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.ui.common.dismissKeyboardOnTap
import com.ram.agroadvisor.ui.navigation.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen() {
    val navController = LocalNavController.current
    val viewModel: AccountSettingsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var currentError by remember { mutableStateOf<String?>(null) }
    var newError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    val isLoading = state is UpdatePasswordState.Loading

    // Backend rules: 8+ chars, lowercase, uppercase, digit.
    fun validateNewPassword(p: String): String? = when {
        p.isBlank() -> "Yeni şifrə tələb olunur"
        p.length < 8 -> "Şifrə minimum 8 simvol olmalıdır"
        !p.any { it.isUpperCase() } -> "Ən az 1 böyük hərf olmalıdır"
        !p.any { it.isLowerCase() } -> "Ən az 1 kiçik hərf olmalıdır"
        !p.any { it.isDigit() } -> "Ən az 1 rəqəm olmalıdır"
        else -> null
    }

    fun validateCurrent(p: String): String? =
        if (p.isBlank()) "Cari şifrə tələb olunur" else null

    fun validateConfirm(c: String, n: String): String? = when {
        c.isBlank() -> "Şifrəni təsdiq edin"
        c != n -> "Şifrələr uyğun gəlmir"
        else -> null
    }

    // Clear the entered values after a successful update so the form doesn't
    // hold sensitive material and the success state is visibly final.
    LaunchedEffect(state) {
        if (state is UpdatePasswordState.Success) {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hesab Parametrləri",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Şifrəni dəyişdir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Hesabınızı qorumaq üçün güclü şifrə seçin",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inline success / error banner
            when (val s = state) {
                is UpdatePasswordState.Success -> {
                    StatusBanner(
                        message = s.message,
                        isError = false,
                        onDismiss = { viewModel.reset() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                is UpdatePasswordState.Error -> {
                    StatusBanner(
                        message = s.message,
                        isError = true,
                        onDismiss = { viewModel.reset() }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                else -> {}
            }

            // Form card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PasswordField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                            currentError = null
                        },
                        label = "Cari şifrə",
                        isError = currentError != null,
                        errorMessage = currentError,
                        visible = currentVisible,
                        onVisibilityToggle = { currentVisible = !currentVisible },
                        enabled = !isLoading
                    )

                    PasswordField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            newError = null
                            // Re-evaluate confirm match when new changes.
                            if (confirmPassword.isNotEmpty()) {
                                confirmError = validateConfirm(confirmPassword, it)
                            }
                        },
                        label = "Yeni şifrə",
                        isError = newError != null,
                        errorMessage = newError,
                        visible = newVisible,
                        onVisibilityToggle = { newVisible = !newVisible },
                        enabled = !isLoading
                    )

                    PasswordField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmError = null
                        },
                        label = "Yeni şifrəni təsdiq et",
                        isError = confirmError != null,
                        errorMessage = confirmError,
                        visible = confirmVisible,
                        onVisibilityToggle = { confirmVisible = !confirmVisible },
                        enabled = !isLoading
                    )

                    Text(
                        text = "Şifrə minimum 8 simvol olmalı və 1 böyük hərf, 1 kiçik hərf və 1 rəqəm ehtiva etməlidir.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    currentError = validateCurrent(currentPassword)
                    newError = validateNewPassword(newPassword)
                    confirmError = validateConfirm(confirmPassword, newPassword)
                    if (currentError == null && newError == null && confirmError == null) {
                        viewModel.updatePassword(currentPassword, newPassword, confirmPassword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Şifrəni Yenilə",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String?,
    visible: Boolean,
    onVisibilityToggle: () -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = isError,
        enabled = enabled,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        supportingText = {
            if (!errorMessage.isNullOrBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        },
        trailingIcon = {
            if (isError) {
                Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
            } else {
                IconButton(onClick = onVisibilityToggle) {
                    Icon(
                        imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
private fun StatusBanner(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = if (isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) {
                Text(
                    "Bağla",
                    color = if (isError)
                        MaterialTheme.colorScheme.onErrorContainer
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
