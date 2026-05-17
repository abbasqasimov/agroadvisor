package com.ram.agroadvisor.ui.screens.profile.support

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
        "Hava məlumatı haradan gəlir?" to
            "Hava məlumatı WeatherAPI xidmətindən real vaxtda alınır. Ana səhifədə cari temperatur " +
            "və qısa proqnoz göstərilir; 'Hava' kartına basaraq detallı görünüşə və 7 günlük " +
            "proqnoza keçə bilərsiniz.",

        "Bugünkü Tövsiyələr necə hesablanır?" to
            "Ana səhifədəki tövsiyələr cari temperatur, hissi temperatur, külək, yağış ehtimalı, " +
            "UV, görünüş və sabahkı proqnoz əsasında 11 sahə üzrə (çiləmə, suvarma, gübrələmə, " +
            "məhsul yığımı, şum, budama, toxum səpini, UV qoruma, don riski, istilik stresi, açıq " +
            "hava işləri) avtomatik qiymətləndirilir.",

        "Gübrə Kalkulyatoru necə işləyir?" to
            "Kalkulyator tabını açın → 'Kalkulyatoru Aç' → bitki növünü, böyümə mərhələsini, " +
            "torpaq növünü və sahə ölçüsünü (hektar) daxil edin → 'Hesabla' düyməsinə basın. " +
            "Nəticədə NPK miqdarı, baza dəyərləri və AI tövsiyələri göstəriləcək.",

        "Niyə bəzi bitki və mərhələ kombinasiyaları yoxdur?" to
            "Böyümə mərhələləri seçilmiş bitkiyə görə filtrlənir. Kalkulyator yalnız serverin " +
            "kataloqunda mövcud olan (bitki, mərhələ) cütlərini göstərir — bu, dəstəklənməyən " +
            "kombinasiyanın göndərilməsinin qarşısını alır.",

        "Bitki şəklini necə analiz edə bilərəm?" to
            "Aşağı menyudan 'Analiz' (+) tabını seçin → 'Kamera' ilə yeni şəkil çəkin və ya " +
            "'Qalereya'dan mövcud bir şəkil seçin → 'Analiz Et' düyməsinə basın. Tətbiq bitkinin " +
            "adını, xəstəliyi, etibarlılıq faizini və qısa izahı göstərəcək.",

        "Analiz nəticəsindən sonra AI-dan necə kömək ala bilərəm?" to
            "Analiz nəticə səhifəsindəki 'AI-dan daha çox məsləhət al' düyməsinə basın. AI Köməkçi " +
            "açılır və xəstəlik adı ilə hazırlanmış sual mətn sahəsində avtomatik görünür — yalnız " +
            "Göndər düyməsinə basın və ya mətni redaktə edib sonra göndərin.",

        "AI Köməkçi ilə necə yazışmaq olar?" to
            "Ana səhifədəki 'AI Köməkçi' kartına basın. Sualınızı aşağıdakı sahəyə yazıb göndər " +
            "düyməsinə basın. Köməkçi kənd təsərrüfatı, torpaq, hava və bitki xəstəlikləri ilə " +
            "bağlı suallara cavab verir.",

        "Köhnə söhbətlərimə baxa bilərəmmi?" to
            "Bəli. AI Köməkçi ekranının yuxarı sol küncündəki menyu (☰) ikonasına basın — söhbət " +
            "tarixçəsi açılır. İstənilən söhbətə basaraq onu davam etdirə, və ya zibil qutusu " +
            "ikonası ilə silə bilərsiniz.",

        "Şifrəmi necə dəyişə bilərəm?" to
            "Profil → Tənzimləmələr → Hesab Parametrləri bölməsinə keçin. Cari şifrənizi, yeni " +
            "şifrənizi və təsdiqi daxil edin. Yeni şifrə minimum 8 simvol olmalı və ən az 1 böyük " +
            "hərf, 1 kiçik hərf və 1 rəqəm ehtiva etməlidir.",

        "Görünüş (işıqlı/qaranlıq rejim) necə dəyişdirilir?" to
            "Profil → Tənzimləmələr → Görünüş bölməsinə keçin. İşıqlı rejim, Qaranlıq rejim və " +
            "ya Sistem standartı seçimlərindən birini seçin — dəyişiklik dərhal tətbiq olunur.",

        "Bildirişləri necə idarə edə bilərəm?" to
            "Profil → Tənzimləmələr → Bildirişlər seçimi sizi cihazın sistem parametrlərinə " +
            "yönləndirir. Buradan AgroAdvisor üçün bildirişləri açıb-bağlaya, kanal səviyyəsində " +
            "səssiz rejim seçə bilərsiniz.",

        "Məlumatlarım təhlükəsizdirmi?" to
            "Bəli. Bütün şəbəkə əlaqələri HTTPS şifrələməsi ilə qorunur, JWT tokenlər cihazın " +
            "özəl sahəsində saxlanır və şifrələr serverdə yalnız hash formasında yerləşir. " +
            "Ətraflı məlumat üçün Profil → Tənzimləmələr → Məxfilik və Təhlükəsizlik bölməsinə " +
            "baxa bilərsiniz."
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