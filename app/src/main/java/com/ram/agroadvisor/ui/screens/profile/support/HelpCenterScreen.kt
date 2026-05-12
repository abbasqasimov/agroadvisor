package com.ram.agroadvisor.ui.screens.profile

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ram.agroadvisor.ui.navigation.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen() {
    val navController = LocalNavController.current
    val faqs = listOf(
        "Bitki inkişafını necə izləmək olar?" to "Bitkilərim bölməsinə keçin və real vaxt yeniləmələrini görmək üçün konkret sahənizi seçin.",
        "AI Köməkçidən necə istifadə etmək olar?" to "Torpaq, hava və ya bitki xəstəlikləri haqqında sual vermək üçün menyudakı robot ikonasına basın.",
        "Məkanı necə yeniləmək olar?" to "Cari kənd təsərrüfatı regionunuzu yeniləmək üçün Hesab Parametrləri > Profilə keçin.",
        "Məlumatlarım təhlükəsizdirmi?" to "Bəli, AgroAdvisor təsərrüfat məlumatlarınızı qorumaq üçün sənaye standartı şifrələmədən istifadə edir."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yardım Mərkəzi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Tez-tez verilən suallar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(faqs) { faq ->
                var expanded by remember { mutableStateOf(false) }
                ElevatedCard(
                    onClick = { expanded = !expanded },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .animateContentSize()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                faq.first,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
                        }
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(faq.second, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}