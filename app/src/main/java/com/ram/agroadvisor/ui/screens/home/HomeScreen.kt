package com.ram.agroadvisor.ui.screens.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.data.local.LocalNotification
import com.ram.agroadvisor.data.model.AgroRecommendation
import com.ram.agroadvisor.data.model.AgroStatus
import com.ram.agroadvisor.ui.navigation.LocalNavController
import com.ram.agroadvisor.ui.navigation.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navController = LocalNavController.current
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showNotifications by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(weatherViewModel.getNotifications()) }
    val unreadCount = notifications.count { !it.isRead }

    LaunchedEffect(uiState) {
        if (uiState is WeatherUiState.Success || uiState is WeatherUiState.Error) {
            isRefreshing = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) weatherViewModel.fetchWeatherByLocation()
        else weatherViewModel.fetchWeather("Baku")
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            weatherViewModel.fetchWeatherByLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val temperature = when (val s = uiState) {
        is WeatherUiState.Success -> "${s.data.current.temp_c}°C"
        else -> "--°C"
    }
    val description = when (val s = uiState) {
        is WeatherUiState.Success -> s.data.current.condition.text
        else -> "Yüklənir..."
    }
    val recommendation = when (val s = uiState) {
        is WeatherUiState.Success -> {
            val rain = s.data.forecast.forecastday.firstOrNull()?.day?.daily_chance_of_rain ?: 0
            "Today\nYağış: $rain%"
        }
        else -> "Today\n--"
    }
    val weatherIcon = when (val s = uiState) {
        is WeatherUiState.Success -> conditionToEmoji(s.data.current.condition.code)
        else -> "🌤️"
    }
    val agroRecommendations = when (val s = uiState) {
        is WeatherUiState.Success -> weatherViewModel.getAgriculturalRecommendations(s.data)
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "AgroAdvisor",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "Your Smart Farming Assistant",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = {
                            showNotifications = !showNotifications
                            if (!showNotifications) {
                                weatherViewModel.markAllAsRead()
                                notifications = weatherViewModel.getNotifications()
                            }
                        }) {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        ) { Text(unreadCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showNotifications,
                            onDismissRequest = {
                                showNotifications = false
                                weatherViewModel.markAllAsRead()
                                notifications = weatherViewModel.getNotifications()
                            },
                            modifier = Modifier.width(320.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Bildirişlər",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (notifications.isNotEmpty()) {
                                    TextButton(onClick = {
                                        weatherViewModel.clearAllNotifications()
                                        notifications = emptyList()
                                    }) {
                                        Text(
                                            "Hamısını sil",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            if (notifications.isEmpty()) {
                                DropdownMenuItem(
                                    text = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Bildiriş yoxdur",
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {}
                                )
                            } else {
                                notifications.forEach { notification ->
                                    LocalNotificationItem(notification = notification)
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                notifications = weatherViewModel.getNotifications()
                weatherViewModel.fetchWeatherByLocation()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    WeatherCard(
                        temperature = temperature,
                        description = description,
                        recommendation = recommendation,
                        weatherEmoji = weatherIcon,
                        isLoading = uiState is WeatherUiState.Loading,
                        onClick = { navController.navigate(Screen.Weather.route) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureGrid()
                }

                if (agroRecommendations.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        AgroRecommendationsSection(recommendations = agroRecommendations)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CropCalendarSection()
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    FeaturedArticleSection()
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun LocalNotificationItem(notification: LocalNotification) {
    val timeText = remember(notification.time) {
        try {
            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("az"))
            sdf.format(Date(notification.time))
        } catch (e: Exception) {
            ""
        }
    }

    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (notification.title.contains("Kritik") || notification.title.contains("⚠️"))
                            Icons.Default.Warning
                        else
                            Icons.Default.WbSunny,
                        contentDescription = null,
                        tint = if (!notification.isRead)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        notification.message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        lineHeight = 16.sp
                    )
                    Text(
                        timeText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        },
        onClick = {}
    )
}

@Composable
fun AgroRecommendationsSection(recommendations: List<AgroRecommendation>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bugünkü Tövsiyələr",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "🌤️ Hava əsasında",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        recommendations.forEach { rec ->
            AgroRecommendationCard(recommendation = rec)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AgroRecommendationCard(recommendation: AgroRecommendation) {
    val (bgColor, iconColor, statusText) = when (recommendation.status) {
        AgroStatus.GOOD -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "✅ Uyğundur")
        AgroStatus.WARNING -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "⚠️ Diqqətli olun")
        AgroStatus.BAD -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "❌ Tövsiyə edilmir")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = recommendation.emoji, fontSize = 32.sp, modifier = Modifier.size(44.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recommendation.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = recommendation.reason,
                    fontSize = 13.sp,
                    color = Color(0xFF1A1A2E).copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = iconColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    temperature: String,
    description: String,
    recommendation: String,
    weatherEmoji: String = "🌤️",
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Yüklənir...", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = weatherEmoji, fontSize = 40.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            temperature,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            description,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            maxLines = 1
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        recommendation,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureGrid() {
    val navController = LocalNavController.current
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.Chat,
                title = "AI Assistant",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.AIAssistant.routeWithPrefill(null)) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.Eco,
                title = "Crop Guide",
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate(Screen.CropGuide.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CropCalendarSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Crop Calendar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            IconButton(onClick = { }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        CropCalendarCard(cropName = "Wheat", description = "Harvest in 15 days")
        Spacer(modifier = Modifier.height(12.dp))
        CropCalendarCard(cropName = "Tomatoes", description = "Fertilize today", icon = Icons.Default.LocalFlorist)
    }
}

@Composable
fun CropCalendarCard(
    cropName: String,
    description: String,
    icon: ImageVector = Icons.Default.Eco
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cropName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FeaturedArticleSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Featured Article", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(12.dp))
        FeaturedArticleCard(
            title = "Smart Farming Technology in 2026",
            description = "Discover how AI and IoT are revolutionizing agriculture and increasing crop yields.",
            readMoreText = "Read More"
        )
    }
}

@Composable
fun FeaturedArticleCard(title: String, description: String, readMoreText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Column(modifier = Modifier.padding(20.dp)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Text(readMoreText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}