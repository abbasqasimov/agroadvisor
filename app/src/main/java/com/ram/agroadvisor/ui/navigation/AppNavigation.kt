package com.ram.agroadvisor.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ram.agroadvisor.ui.theme.ThemeMode

/** Routes on which the bottom NavigationBar is visible. */
private val BOTTOM_TAB_ROUTES = setOf(
    Screen.Home.route,
    Screen.Calculator.route,
    Screen.Analysis.route,
    Screen.Profile.route
)

private val BOTTOM_NAV_ITEMS = listOf(
    BottomNavItem("Home",       Screen.Home.route,       Icons.Outlined.Home,      Icons.Filled.Home),
    BottomNavItem("Calculator", Screen.Calculator.route, Icons.Outlined.Calculate, Icons.Filled.Calculate),
    BottomNavItem("Analysis",   Screen.Analysis.route,   Icons.Outlined.Add,       Icons.Filled.Add),
    BottomNavItem("Profile",    Screen.Profile.route,    Icons.Outlined.Person,    Icons.Filled.Person)
)

@Composable
fun AppNavigation(
    startDestination: String = Screen.Welcome.route,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChange: (ThemeMode) -> Unit = {}
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    CompositionLocalProvider(LocalNavController provides navController) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentRoute in BOTTOM_TAB_ROUTES) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        BOTTOM_NAV_ITEMS.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(item.label, style = MaterialTheme.typography.labelSmall)
                                },
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
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
        ) { innerPadding ->
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding()),
                navController = navController,
                startDestination = startDestination
            ) {
                navGraph(themeMode, onThemeChange)
            }
        }
    }
}
