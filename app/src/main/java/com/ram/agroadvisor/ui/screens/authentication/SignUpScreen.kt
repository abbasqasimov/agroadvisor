package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit = {}
) {
    val primaryGreen = Color(0xFF4CAF50)
    val scrollState = rememberScrollState()

    // State-lər: Ad, Telefon, Email və Şifrə
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Başlıq hissəsi
        Text(
            text = "Create Account",
            color = primaryGreen,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Fill in your details to get started",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 1. Ad Soyad
        CustomOutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full Name",
            primaryGreen = primaryGreen
        )

        // 2. Telefon nömrəsi
        CustomOutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = "Phone Number",
            primaryGreen = primaryGreen,
            keyboardType = KeyboardType.Phone
        )

        // 3. Email
        CustomOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            primaryGreen = primaryGreen,
            keyboardType = KeyboardType.Email
        )

        // 4. Şifrə
        CustomOutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            primaryGreen = primaryGreen,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Növbəti mərhələyə keçid düyməsi
        Button(
            onClick = { onSignUpSuccess() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
        ) {
            Text(
                text = "Next: Field Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Girişə qayıtmaq üçün link
        TextButton(onClick = { onBackToLogin() }) {
            Text(
                text = "Already have an account? Log in",
                color = primaryGreen,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Təkrarçılığın qarşısını almaq üçün xüsusi TextField komponenti.
 * Bu funksiyanı eyni faylda saxla ki, SignUpScreen onu tapa bilsin.
 */
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    primaryGreen: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryGreen,
            focusedLabelColor = primaryGreen,
            cursorColor = primaryGreen,
            unfocusedBorderColor = Color.LightGray
        )
    )
}