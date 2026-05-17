package com.ram.agroadvisor.ui.screens.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ram.agroadvisor.data.model.ForecastDay
import com.ram.agroadvisor.data.model.WeatherResponse
import com.ram.agroadvisor.ui.common.dismissKeyboardOnTap
import com.ram.agroadvisor.ui.navigation.LocalNavController
import kotlin.math.roundToInt

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
        modifier = Modifier
            .fillMaxSize()
            .dismissKeyboardOnTap(),
        containerColor = MaterialTheme.colorScheme.background,
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
                                Text(
                                    "Şəhər axtar...",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
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
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "Hava",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { weatherViewModel.fetchWeatherByLocation() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is WeatherUiState.Loading -> WeatherLoading()
                is WeatherUiState.Error -> WeatherError(
                    message = state.message,
                    onRetry = { weatherViewModel.fetchWeatherByLocation() }
                )
                is WeatherUiState.Success -> WeatherContent(data = state.data)
                else -> Unit
            }

            if (suggestions.isNotEmpty() && isSearchActive) {
                SuggestionList(
                    suggestions = suggestions,
                    onPick = { suggestion ->
                        weatherViewModel.fetchWeather(suggestion)
                        weatherViewModel.clearSuggestions()
                        isSearchActive = false
                        searchQuery = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .align(Alignment.TopStart)
                )
            }
        }
    }
}

@Composable
private fun WeatherLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Hava məlumatı yüklənir...",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeatherError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Hava məlumatı alınmadı",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yenidən cəhd et")
        }
    }
}

@Composable
private fun SuggestionList(
    suggestions: List<String>,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            suggestions.forEachIndexed { idx, suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(suggestion) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = suggestion,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (idx < suggestions.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
fun WeatherContent(data: WeatherResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeroCard(data = data)
        Spacer(modifier = Modifier.height(16.dp))
        DetailsCard(data = data)
        Spacer(modifier = Modifier.height(16.dp))
        ForecastCard(data = data)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HeroCard(data: WeatherResponse) {
    val today = data.forecast.forecastday.firstOrNull()?.day
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            // Location row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "${data.location.name}, ${data.location.country}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Temperature + emoji row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${data.current.temp_c.roundToInt()}°",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1
                    )
                    Text(
                        data.current.condition.text,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Hissi ${data.current.feelslike_c.roundToInt()}°",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = conditionToEmoji(data.current.condition.code),
                    fontSize = 96.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Min / Max / Rain-chance chip row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroPill(
                    icon = Icons.Default.KeyboardArrowUp,
                    label = "Maks",
                    value = "${today?.maxtemp_c?.roundToInt() ?: data.current.temp_c.roundToInt()}°"
                )
                HeroDivider()
                HeroPill(
                    icon = Icons.Default.KeyboardArrowDown,
                    label = "Min",
                    value = "${today?.mintemp_c?.roundToInt() ?: data.current.temp_c.roundToInt()}°"
                )
                HeroDivider()
                HeroPill(
                    icon = Icons.Default.WaterDrop,
                    label = "Yağış",
                    value = "${today?.daily_chance_of_rain ?: 0}%"
                )
            }
        }
    }
}

@Composable
private fun HeroPill(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1
            )
            Text(
                label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HeroDivider() {
    Box(
        modifier = Modifier
            .height(28.dp)
            .width(1.dp)
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
    )
}

@Composable
private fun DetailsCard(data: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Hava Detalları",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Cari göstəricilər",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2 x 3 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailTile(
                    icon = Icons.Outlined.WbSunny,
                    label = "UV",
                    value = "${data.current.uv.roundToInt()}",
                    hint = uvHint(data.current.uv),
                    modifier = Modifier.weight(1f)
                )
                DetailTile(
                    icon = Icons.Outlined.Visibility,
                    label = "Görünüş",
                    value = "${data.current.vis_km.roundToInt()} km",
                    modifier = Modifier.weight(1f)
                )
                DetailTile(
                    icon = Icons.Outlined.WaterDrop,
                    label = "Rütubət",
                    value = "${data.current.humidity}%",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailTile(
                    icon = Icons.Outlined.Grain,
                    label = "Yağıntı",
                    value = formatMm(data.current.precip_mm),
                    modifier = Modifier.weight(1f)
                )
                DetailTile(
                    icon = Icons.Outlined.Air,
                    label = "Külək",
                    value = "${data.current.wind_kph.roundToInt()} km/s",
                    modifier = Modifier.weight(1f)
                )
                DetailTile(
                    icon = Icons.Outlined.Cloud,
                    label = "Bulud",
                    value = "${data.current.cloud}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DetailTile(
    icon: ImageVector,
    label: String,
    value: String,
    hint: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        if (hint != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                hint,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ForecastCard(data: WeatherResponse) {
    val days = data.forecast.forecastday
    // Cap to 7 days for cleanliness.
    val shown = days.take(7)
    // Used to scale the per-row temperature bar against the whole week's range.
    val weekMin = shown.minOfOrNull { it.day.mintemp_c } ?: 0.0
    val weekMax = shown.maxOfOrNull { it.day.maxtemp_c } ?: 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Həftəlik Proqnoz",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${shown.size} GÜN",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            shown.forEachIndexed { index, forecastDay ->
                ForecastRow(
                    forecastDay = forecastDay,
                    isToday = index == 0,
                    weekMin = weekMin,
                    weekMax = weekMax
                )
                if (index < shown.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastRow(
    forecastDay: ForecastDay,
    isToday: Boolean,
    weekMin: Double,
    weekMax: Double
) {
    val dayLabel = if (isToday) "Bu gün" else getDayName(forecastDay.date)
    val emoji = conditionToEmoji(forecastDay.day.condition.code)
    val min = forecastDay.day.mintemp_c
    val max = forecastDay.day.maxtemp_c
    val rain = forecastDay.day.daily_chance_of_rain

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            dayLabel,
            fontSize = 14.sp,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(96.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(emoji, fontSize = 22.sp)
        if (rain > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "$rain%",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "${min.roundToInt()}°",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))
        TemperatureRangeBar(
            min = min,
            max = max,
            weekMin = weekMin,
            weekMax = weekMax,
            modifier = Modifier
                .width(80.dp)
                .height(6.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "${max.roundToInt()}°",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Start
        )
    }
}

/**
 * Small horizontal bar that visualises where this day's temperature range
 * sits inside the week's overall range. Cold end is blue, hot end is red.
 */
@Composable
private fun TemperatureRangeBar(
    min: Double,
    max: Double,
    weekMin: Double,
    weekMax: Double,
    modifier: Modifier = Modifier
) {
    val span = (weekMax - weekMin).coerceAtLeast(1.0)
    val startFrac = ((min - weekMin) / span).coerceIn(0.0, 1.0).toFloat()
    val endFrac = ((max - weekMin) / span).coerceIn(0.0, 1.0).toFloat()
    val coolColor = Color(0xFF64B5F6)
    val warmColor = Color(0xFFFF8A65)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(endFrac - startFrac)
                .offset(x = (startFrac * 80).dp)
                .clip(RoundedCornerShape(50))
                .background(
                    Brush.horizontalGradient(listOf(coolColor, warmColor))
                )
        )
    }
}

// ---- helpers ----

private fun uvHint(uv: Double): String? = when {
    uv >= 11 -> "Ekstrem"
    uv >= 8 -> "Çox yüksək"
    uv >= 6 -> "Yüksək"
    else -> null
}

private fun formatMm(mm: Double): String {
    return if (mm < 1.0 && mm > 0.0) "%.1f mm".format(mm)
    else "${mm.roundToInt()} mm"
}

fun getDayName(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        val cal = java.util.Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
        }
        when (cal.get(java.util.Calendar.DAY_OF_WEEK)) {
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
