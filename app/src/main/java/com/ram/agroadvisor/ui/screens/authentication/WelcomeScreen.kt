package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    val primaryGreen = Color(0xFF4CAF50)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Başlıq
            Text(
                text = "Welcome",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Alt başlıq / təsvir
            Text(
                text = "Welcome to the AgroAdvisor app. Please log in or create a new account to continue.",
                fontSize = 16.sp,
                color = Color.Black.copy(alpha = 0.8f),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Giriş et düyməsi
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(text = "Login", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryGreen
                ),
                border = BorderStroke(1.dp, primaryGreen) // ← burda düzəltdik
            ) {
                Text(text = "Sign Up", fontSize = 16.sp)
            }

        }
    }
}