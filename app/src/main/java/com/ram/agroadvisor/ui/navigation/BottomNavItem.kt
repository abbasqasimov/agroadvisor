package com.ram.agroadvisor.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val route: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)
