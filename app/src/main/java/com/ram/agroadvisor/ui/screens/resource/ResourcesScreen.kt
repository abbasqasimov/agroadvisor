package com.ram.agroadvisor.ui.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NewsArticle(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val category: String,
    val imageColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen() {
    val articles = listOf(
        NewsArticle(
            id = 1,
            title = "Yeni Texnologiya: AI ilə Məhsuldarlıq Artımı",
            description = "Süni intellekt əsaslı sistemlər fermerlərin məhsuldarlığını 40% artırır",
            date = "7 Mart 2026",
            category = "Texnologiya",
            imageColor = Color(0xFF4CAF50)
        ),
        NewsArticle(
            id = 2,
            title = "Quraqlığa Davamlı Buğda Sortları",
            description = "Alimlər az su tələb edən yeni buğda sortları inkişaf etdiriblər",
            date = "5 Mart 2026",
            category = "Tədqiqat",
            imageColor = Color(0xFFFFC107)
        ),
        NewsArticle(
            id = 3,
            title = "Organic Əkinçilik: 2026 Tendensiyaları",
            description = "Üzvi məhsul tələbatı bu il 25% artıb, bazar genişlənir",
            date = "3 Mart 2026",
            category = "Bazar",
            imageColor = Color(0xFF81C784)
        ),
        NewsArticle(
            id = 4,
            title = "Zərərvericilərlə Mübarizədə Yeni Üsullar",
            description = "Biologik mübarizə metodları kimyəvi preparatlara alternativ olur",
            date = "1 Mart 2026",
            category = "Təhsil",
            imageColor = Color(0xFF4CAF50)
        ),
        NewsArticle(
            id = 5,
            title = "Smart Suvarma Sistemləri",
            description = "IoT əsaslı suvarma sistemləri su istehlakını 50% azaldır",
            date = "28 Fevral 2026",
            category = "Texnologiya",
            imageColor = Color(0xFF2196F3)
        ),
        NewsArticle(
            id = 6,
            title = "İqlim Dəyişikliyi və Kənd Təsərrüfatı",
            description = "Yeni hesabatlara görə, iqlim dəyişikliyi məhsuldarlığa təsir edir",
            date = "25 Fevral 2026",
            category = "Mühit",
            imageColor = Color(0xFFFF9800)
        ),
        NewsArticle(
            id = 7,
            title = "Dron Texnologiyası Əkinçilikdə",
            description = "Dronlar vasitəsilə məhsul monitorinqi və gübrələmə effektivliyi artır",
            date = "22 Fevral 2026",
            category = "Texnologiya",
            imageColor = Color(0xFF9C27B0)
        ),
        NewsArticle(
            id = 8,
            title = "Qida Təhlükəsizliyi Standartları Yeniləndi",
            description = "Yeni qida təhlükəsizliyi qaydaları fermerlərdən əlavə tələblər edir",
            date = "20 Fevral 2026",
            category = "Qanunvericilik",
            imageColor = Color(0xFFE91E63)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Resources",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                NewsArticleCard(article = article)
            }
        }
    }
}

@Composable
fun NewsArticleCard(article: NewsArticle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to article detail */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(article.imageColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = article.category.first().toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = article.imageColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = article.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = article.imageColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = article.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = article.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = article.date,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Read more",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}