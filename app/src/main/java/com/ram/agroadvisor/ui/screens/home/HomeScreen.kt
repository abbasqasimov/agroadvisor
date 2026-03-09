package com.ram.agroadvisor.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ram.agroadvisor.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController? = null) {
    val primaryGreen = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF4CAF50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "AgroAdvisor",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Your Smart Farming Assistant",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        modifier = Modifier.padding(end = 16.dp),
                        badge = {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text("3")
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryGreen
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            )
        ) {
            // Weather Card
            item {
                WeatherCard(
                    temperature = "28°C",
                    description = "Sunny, Low Humidity",
                    recommendation = "Today\nGood for spraying",
                    backgroundColor = lightGreen,
                    onClick = { navController?.navigate(Screen.Weather.route) }
                )
            }

            // Feature Cards Grid
            item {
                Spacer(modifier = Modifier.height(16.dp))
                FeatureGrid(navController)
            }

            // Today's Tips Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                TodaysTipsSection()
            }

            // Crop Calendar Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CropCalendarSection()
            }

            // Featured Article Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                FeaturedArticleSection()
            }

            // Son boşluq
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    temperature: String,
    description: String,
    recommendation: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LightMode,
                    contentDescription = "Weather",
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFFFFEB3B)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        temperature,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        description,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    recommendation,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun FeatureGrid(navController: NavController? = null) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureCard(
                icon = Icons.Default.Chat,
                title = "AI Assistant",
                backgroundColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate(Screen.AIAssistant.route) }
            )
            FeatureCard(
                icon = Icons.Default.WaterDrop,
                title = "Irrigation",
                backgroundColor = Color(0xFFE3F2FD),
                iconColor = Color(0xFF2196F3),
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate(Screen.Irrigation.route) }
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
                backgroundColor = Color(0xFFFFF9C4),
                iconColor = Color(0xFFFBC02D),
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate(Screen.CropGuide.route) }
            )
            FeatureCard(
                icon = Icons.Default.TrendingUp,
                title = "Market Price",
                backgroundColor = Color(0xFFF3E5F5),
                iconColor = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f),
                onClick = { navController?.navigate(Screen.MarketPrice.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    tint = iconColor
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun TodaysTipsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Today's Tips",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            TextButton(onClick = { /* TODO */ }) {
                Text("See All", color = Color(0xFF4CAF50))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TipCard(
            icon = Icons.Default.WaterDrop,
            title = "Optimal Irrigation Time",
            description = "Best time to water your crops is early morning (5-7 AM) to minimize water loss from evaporation.",
            iconBackgroundColor = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TipCard(
    icon: ImageVector,
    title: String,
    description: String,
    iconBackgroundColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.7f), lineHeight = 20.sp)
            }
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
            Text("Crop Calendar", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        CropCalendarCard(cropName = "Wheat", description = "Harvest in 15 days", iconBackgroundColor = Color(0xFFFFF9C4))
        Spacer(modifier = Modifier.height(12.dp))
        CropCalendarCard(cropName = "Tomatoes", description = "Fertilize today", iconBackgroundColor = Color(0xFFE8F5E9), icon = Icons.Default.LocalFlorist)
    }
}

@Composable
fun CropCalendarCard(
    cropName: String,
    description: String,
    iconBackgroundColor: Color,
    icon: ImageVector = Icons.Default.Eco
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cropName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 14.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = Color.Gray)
        }
    }
}

@Composable
fun FeaturedArticleSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Featured Article", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFF81C784)))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(description, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* TODO */ }
                ) {
                    Text(readMoreText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}