package com.ram.agroadvisor.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ram.agroadvisor.ui.navigation.Screen
import com.ram.agroadvisor.ui.screens.ai.AIAssistantScreen
import com.ram.agroadvisor.ui.screens.ai.AISection
import com.ram.agroadvisor.ui.screens.home.HomeScreen
import com.ram.agroadvisor.ui.screens.plus.PlusScreen
import com.ram.agroadvisor.ui.screens.profile.ProfileScreen
import com.ram.agroadvisor.ui.screens.resources.ResourcesScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController? = null) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var isChatActive by remember { mutableStateOf(false) }

    val items = listOf(
        BottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem("Resources", Icons.Outlined.Book, Icons.Filled.Book),
        BottomNavItem("Plus", Icons.Outlined.Add, Icons.Filled.Add),
        BottomNavItem("AI Chat", Icons.Outlined.Chat, Icons.Filled.Chat),
        BottomNavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person)
    )

    val shouldShowBottomBar = !(selectedItem == 3 && isChatActive)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selectedItem == index) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                if (index != 3) isChatActive = false
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { _ ->
        when (selectedItem) {
            0 -> HomeScreen(navController)
            1 -> ResourcesScreen()
            2 -> PlusScreen()
            3 -> {
                if (isChatActive) {
                    AIAssistantScreen(
                        mainPadding = PaddingValues(0.dp),
                        onBackClick = { isChatActive = false }
                    )
                } else {
                    AISection(
                        onStartChatClick = { isChatActive = true }
                    )
                }
            }
            4 -> ProfileScreen(
                onAccountSettingsClick = {
                    navController?.navigate(Screen.AccountSettings.route)
                },
                onAppearanceClick = {
                    navController?.navigate(Screen.Appearance.route)
                }
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun PlaceholderScreen(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}