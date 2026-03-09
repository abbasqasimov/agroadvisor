package com.ram.agroadvisor.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onBackClick: () -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    // ProfileScreen-dəki arxa plan rəngi ilə eyni
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Account Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryGreen
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor) // Arxa plan açıq boz
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsItem(icon = Icons.Default.Person, title = "Edit Profile", iconColor = primaryGreen)
            SettingsItem(icon = Icons.Default.Lock, title = "Change Password", iconColor = primaryGreen)
            SettingsItem(icon = Icons.Default.Security, title = "Two-Factor Authentication", iconColor = primaryGreen)
            SettingsItem(icon = Icons.Default.PrivacyTip, title = "Privacy Settings", iconColor = primaryGreen)
            SettingsItem(icon = Icons.Default.Storage, title = "Data & Storage", iconColor = primaryGreen)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Delete Account",
                isDanger = true,
                iconColor = Color.Red
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    isDanger: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(16.dp), // Profile-dakı kartlarla eyni radius
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Kartların daxili ağ oldu
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Tıklanma funksiyası */ }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp) // Daha geniş və rahat toxunuş sahəsi
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp, // ProfileScreen-dəki SettingItem ölçüsü ilə eyni
                fontWeight = if (isDanger) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isDanger) Color.Red else Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}