package com.ram.agroadvisor.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.data.model.WeatherResponse
import com.ram.agroadvisor.ui.navigation.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    val navController = LocalNavController.current
    val weatherViewModel: WeatherViewModel = hiltViewModel()
    val uiState by weatherViewModel.uiState.collectAsState()
    val suggestions by weatherViewModel.suggestions.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val skyBlue = Color(0xFF4A90D9)
    val skyBlueDark = Color(0xFF2C6FAC)

    // Location icazə launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            weatherViewModel.fetchWeatherByLocation()
        } else {
            weatherViewModel.fetchWeather("Baku")
        }
    }

    // Ekran açılanda location ilə yüklə
    LaunchedEffect(Unit) {
        val fineGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val coarseGranted = androidx.core.content.ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

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

    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                weatherViewModel.searchSuggestions(it)
                            },
                            placeholder = {
                                Text("Şəhər axtar...", color = Color.White.copy(alpha = 0.7f))
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (searchQuery.isNotBlank()) {
                                        weatherViewModel.fetchWeather(searchQuery)
                                        weatherViewModel.clearSuggestions()
                                        isSearchActive = false
                                        searchQuery = ""
                                    }
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                            weatherViewModel.clearSuggestions()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Cancel", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = skyBlueDark)
                )
            } else {
                TopAppBar(
                    title = {
                        Text("Weather", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                        IconButton(onClick = { weatherViewModel.fetchWeatherByLocation() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = skyBlueDark)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val state = uiState
            when (state) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = skyBlue
                    )
                }
                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            state.message,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { weatherViewModel.fetchWeatherByLocation() }) {
                            Text("Yenidən cəhd et")
                        }
                    }
                }
                is WeatherUiState.Success -> {
                    WeatherContent(
                        data = state.data,
                        skyBlue = skyBlue,
                        skyBlueDark = skyBlueDark
                    )
                }
                else -> {}
            }

            // Suggestions dropdown
            if (suggestions.isNotEmpty() && isSearchActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        suggestions.forEach { suggestion ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        weatherViewModel.fetchWeather(suggestion)
                                        weatherViewModel.clearSuggestions()
                                        isSearchActive = false
                                        searchQuery = ""
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF4A90D9),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = suggestion,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1A1A2E)
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherContent(data: WeatherResponse, skyBlue: Color, skyBlueDark: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(skyBlueDark, skyBlue)))
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Yağış ehtimalı ${data.forecast.forecastday.firstOrNull()?.day?.daily_chance_of_rain ?: 0}%",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            data.current.condition.text,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${data.location.name}, ${data.location.country}",
                                fontSize = 14.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = conditionToEmoji(data.current.condition.code),
                        fontSize = 56.sp,
                        modifier = Modifier.size(72.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "${data.current.temp_c}°C",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text("İndiki", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔺", fontSize = 14.sp)
                            Text(
                                "${data.forecast.forecastday.firstOrNull()?.day?.maxtemp_c}°",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text("Maks", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔻", fontSize = 14.sp)
                            Text(
                                "${data.forecast.forecastday.firstOrNull()?.day?.mintemp_c}°",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text("Min", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        VerticalDivider(
                            modifier = Modifier.height(40.dp),
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🌡️", fontSize = 14.sp)
                            Text(
                                "${data.current.feelslike_c}°",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text("Hiss", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Hava Detalları", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    WeatherDetailItem("☀️", "UV", "${data.current.uv}")
                    WeatherDetailItem("💧", "Görünüş", "${data.current.vis_km} km")
                    WeatherDetailItem("💦", "Rütubət", "${data.current.humidity}%")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    WeatherDetailItem("🌧️", "Yağıntı", "${data.current.precip_mm} mm")
                    WeatherDetailItem("💨", "Külək", "${data.current.wind_kph} km/h")
                    WeatherDetailItem("☁️", "Bulud", "${data.current.cloud}%")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Proqnoz", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text("14 GÜNLÜK PROQNOZ", fontSize = 11.sp, color = Color.Gray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                data.forecast.forecastday.forEachIndexed { index, forecastDay ->
                    val dayName = if (index == 0) "Bu gün" else getDayName(forecastDay.date)
                    ForecastRow(
                        day = dayName,
                        max = "${forecastDay.day.maxtemp_c}°",
                        min = "${forecastDay.day.mintemp_c}°",
                        weatherType = conditionToType(forecastDay.day.condition.code)
                    )
                    if (index < data.forecast.forecastday.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFF0F0F0)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

fun getDayName(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        val dayOfWeek = java.util.Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }.get(java.util.Calendar.DAY_OF_WEEK)
        when (dayOfWeek) {
            java.util.Calendar.MONDAY -> "Bazar ertəsi"
            java.util.Calendar.TUESDAY -> "Çərşənbə axşamı"
            java.util.Calendar.WEDNESDAY -> "Çərşənbə"
            java.util.Calendar.THURSDAY -> "Cümə axşamı"
            java.util.Calendar.FRIDAY -> "Cümə"
            java.util.Calendar.SATURDAY -> "Şənbə"
            java.util.Calendar.SUNDAY -> "Bazar"
            else -> dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}

@Composable
fun WeatherDetailItem(emoji: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A2E),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            label,
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ForecastRow(day: String, max: String, min: String, weatherType: String) {
    val emoji = when (weatherType) {
        "sunny" -> "☀️"
        "cloudy" -> "☁️"
        "rainy" -> "🌧️"
        "snowy" -> "❄️"
        "stormy" -> "⛈️"
        else -> "🌤️"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            day,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            "Maks: $max",
            fontSize = 14.sp,
            color = Color(0xFFE57373),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            "Min: $min",
            fontSize = 14.sp,
            color = Color(0xFF64B5F6),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

fun conditionToEmoji(code: Int): String {
    return when (code) {
        1000 -> "☀️"
        1003, 1006 -> "⛅"
        1009 -> "☁️"
        1030, 1135, 1147 -> "🌫️"
        1063, 1180, 1183, 1186, 1189, 1192, 1195 -> "🌧️"
        1066, 1210, 1213, 1216, 1219, 1222, 1225 -> "❄️"
        1087, 1273, 1276 -> "⛈️"
        else -> "🌤️"
    }
}

fun conditionToType(code: Int): String {
    return when (code) {
        1000 -> "sunny"
        1003, 1006, 1009 -> "cloudy"
        1063, 1180, 1183, 1186, 1189, 1192, 1195 -> "rainy"
        1066, 1210, 1213 -> "snowy"
        1087, 1273, 1276 -> "stormy"
        else -> "cloudy"
    }
}