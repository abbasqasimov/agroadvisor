package com.ram.agroadvisor.ui.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ram.agroadvisor.ui.navigation.LocalNavController

data class Crop(
    val id: Int,
    val name: String,
    val emoji: String,
    val season: String,
    val soilType: String,
    val waterNeeds: String,
    val growthDays: Int,
    val description: String,
    val diseases: List<CropDisease>,
    val tips: List<String>
)

data class CropDisease(
    val name: String,
    val symptoms: String,
    val treatment: String,
    val severity: DiseaseSeverity
)

enum class DiseaseSeverity { LOW, MEDIUM, HIGH }

val mockCrops = listOf(
    Crop(
        id = 1,
        name = "Buğda",
        emoji = "🌾",
        season = "Payız - Yaz",
        soilType = "Gil-qumsal",
        waterNeeds = "Orta",
        growthDays = 180,
        description = "Buğda Azərbaycanda ən geniş yayılmış taxıl bitkisidir. Əsasən Kür-Araz ovalığında, Gəncə-Qazax zonasında becərilir. Soyuğa davamlıdır.",
        diseases = listOf(
            CropDisease("Sarı pas", "Yarpaqlarda sarı zolaqlar əmələ gəlir, un kimi toz görünür", "Propikonazol və ya tebukonazol əsaslı fungisid tətbiq edin", DiseaseSeverity.HIGH),
            CropDisease("Unlu şeh", "Yarpaqlarda ağ unlu örtük, yarpaqlar saralır", "Kükürd əsaslı fungisid, havalanmanı artırın", DiseaseSeverity.MEDIUM),
            CropDisease("Septorioz", "Yarpaqlarda qonur ləkələr, məhsul azalır", "Triazol qrupu fungisidlər", DiseaseSeverity.HIGH),
            CropDisease("Kök çürüməsi", "Kökdə qəhvəyi rəng, bitki solur", "Drenajı yaxşılaşdırın, toxumu dərmanla emal edin", DiseaseSeverity.HIGH)
        ),
        tips = listOf(
            "Əkindən əvvəl torpağı 25-30 sm dərinliyə şumlayın",
            "Hər hektara 180-220 kq toxum səpin",
            "Azot gübrəsini vegetasiya dövründə 2 mərhələdə verin",
            "Yağışdan sonra zərərverici monitorinqi aparın",
            "Məhsulu 14% rütubətdən az olduqda yığın"
        )
    ),
    Crop(
        id = 2,
        name = "Pomidor",
        emoji = "🍅",
        season = "Mart - Oktyabr",
        soilType = "Yüngül humuslu qumsal",
        waterNeeds = "Yüksək",
        growthDays = 90,
        description = "Pomidor Azərbaycanda həm açıq, həm örtülü sahələrdə becərilir. Abşeron, Lənkəran, Şirvan zonalarında geniş yayılmışdır.",
        diseases = listOf(
            CropDisease("Fitoftoroz", "Yarpaqlarda tünd qəhvəyi ləkələr, meyvə çürüyür", "Mis əsaslı fungisid, xəstə hissələri dərhal kəsin", DiseaseSeverity.HIGH),
            CropDisease("Alternarioz", "Qəhvəyi həlqəvi ləkələr, sarı haşiyə", "Xlorotal əsaslı fungisid", DiseaseSeverity.MEDIUM),
            CropDisease("Mozaika virusu", "Yarpaqda ala-bula rəng, böyümə dayanır", "Xəstə bitkiləri söküb məhv edin", DiseaseSeverity.HIGH),
            CropDisease("Ağ çürümə", "Gövdədə ağ miselyum, toxuma çürüyür", "Havalanmanı artırın, fungisid tətbiq edin", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Tingləri 60x40 sm sxemlə əkin",
            "Suvarmanı damcı üsulu ilə aparın",
            "Hər 10-14 gündən bir gübrələyin",
            "Yaşıl kütlə çox olanda tepəni qoparın",
            "Meyvələri tam yetişməmiş yığın"
        )
    ),
    Crop(
        id = 3,
        name = "Kartof",
        emoji = "🥔",
        season = "Fevral - İyun / Avqust - Noyabr",
        soilType = "Yüngül humuslu, drenajlı",
        waterNeeds = "Orta",
        growthDays = 90,
        description = "Kartof Azərbaycanda həm erkən, həm də gec becərilir. Dağlıq rayonlarda — Lerik, Şəki, Gədəbəy zonalarında yüksək keyfiyyətli kartof yetişir.",
        diseases = listOf(
            CropDisease("Fitoftoroz", "Yarpaqlarda qəhvəyi ləkələr, nəm havada ağ miselyum", "Sistemik fungisid, növbəli əkin tətbiq edin", DiseaseSeverity.HIGH),
            CropDisease("Skab", "Yumruda qabarıq və ya çökük ləkələr", "Torpaq pH-nı 5.0-5.5-ə endirin", DiseaseSeverity.LOW),
            CropDisease("Rizoktoniya", "Cücərtidə qara ləkələr, kök zədələnir", "Sağlam toxumluq seçin, dərmanla emal edin", DiseaseSeverity.MEDIUM),
            CropDisease("Virus xəstəlikləri", "Yarpaq buruşması, ala-bula rəng", "Sertifikatlı toxumluq istifadə edin", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Toxumluğu əkmədən əvvəl işıqda cücərdin",
            "70x30 sm sxemlə əkin",
            "Yumrular 5-8 sm böyüdükdə torpaq çəkin",
            "Məhsulu quru, qaranlıq yerdə saxlayın",
            "Bir sahədə 3 ildən tez əkməyin"
        )
    ),
    Crop(
        id = 4,
        name = "Üzüm",
        emoji = "🍇",
        season = "Aprel - Oktyabr",
        soilType = "Daşlı-gilli, drenajlı",
        waterNeeds = "Aşağı-Orta",
        growthDays = 150,
        description = "Üzümçülük Azərbaycanda ən qədim sahələrdən biridir. Gəncə-Qazax, Şirvan, Abşeron, Naxçıvan zonalarında geniş yayılmışdır. 450-dən çox yerli sort mövcuddur.",
        diseases = listOf(
            CropDisease("Mildyu", "Yarpaqlarda yağlı ləkələr, alt tərəfdə ağ örtük", "Mis sulfat + əhəng məhlulu (Bordo mayesi)", DiseaseSeverity.HIGH),
            CropDisease("Oidium", "Yaşıl hissələrdə ağ toz örtük, giləmeyvə çatlayır", "Kükürd tətbiqi, havalanma", DiseaseSeverity.HIGH),
            CropDisease("Botrytis", "Salxımda boz-çəhrayı çürümə", "Sıxlığı azaldın, mis fungisid", DiseaseSeverity.MEDIUM),
            CropDisease("Filoksera", "Kök sistemi zədələnir, bitki məhv olur", "Filokseraya davamlı calaq üzərində becərin", DiseaseSeverity.HIGH)
        ),
        tips = listOf(
            "Hər il qış budaması aparın",
            "Yazda 2-3 yaşıl əməliyyat edin",
            "Bordo mayesini erkən yazda çiləyin",
            "Bağı alaqlardan daim təmiz saxlayın",
            "Məhsulu səhər saatlarında yığın"
        )
    ),
    Crop(
        id = 5,
        name = "Qarğıdalı",
        emoji = "🌽",
        season = "Aprel - Sentyabr",
        soilType = "Humuslu-gilli, dərin şumlanan",
        waterNeeds = "Yüksək",
        growthDays = 110,
        description = "Qarğıdalı Azərbaycanda həm qida, həm yem məqsədilə becərilir. Kür-Araz ovalığı, Gəncə-Qazax zonası əsas becərilmə əraziləridir.",
        diseases = listOf(
            CropDisease("Pas xəstəliyi", "Yarpaqlarda sarı-qəhvəyi toz ləkələr", "Davamlı sortlar seçin, fungisid tətbiq edin", DiseaseSeverity.MEDIUM),
            CropDisease("Füzarioz", "Süpürgədə çürümə, ağ-çəhrayı miselyum", "Toxumu emal edin, növbəli əkin", DiseaseSeverity.HIGH),
            CropDisease("Mısır güvəsi", "Gövdədə deşiklər, bitki sınır", "Bioinsektisid, erkən əkin", DiseaseSeverity.HIGH),
            CropDisease("Helmintosporioz", "Uzunsov qəhvəyi ləkələr", "Fungisid, toxum emalı", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "70x25 sm sxemlə əkin",
            "Cücərmə dövründə alaq otlarını məhv edin",
            "Azot gübrəsini 2 mərhələdə verin",
            "Süd yetişkənliyində suvarmanı artırın",
            "Rütubət 14%-dən az olduqda yığın"
        )
    ),
    Crop(
        id = 6,
        name = "Soğan",
        emoji = "🧅",
        season = "Fevral - İyul",
        soilType = "Yüngül humuslu qumsal",
        waterNeeds = "Orta",
        growthDays = 120,
        description = "Soğan Azərbaycanda geniş becərilən tərəvəz bitkisidir. Abşeron, Şirvan, Muğan zonalarında sənaye miqyasında istehsal olunur.",
        diseases = listOf(
            CropDisease("Boz çürümə", "Boyunda yumşalma, çürümə", "Drenaj, havalanma, fungisid", DiseaseSeverity.HIGH),
            CropDisease("Mildyu", "Yarpaqlarda boz-bənövşəyi örtük", "Mis əsaslı fungisid, sıxlığı azaldın", DiseaseSeverity.MEDIUM),
            CropDisease("Soğan milçəyi", "Kökdə sürfələr, bitki saralır", "Torpağa insektisid, erkən əkin", DiseaseSeverity.HIGH),
            CropDisease("Pas", "Narıncı toz ləkələr", "Fungisid tətbiqi", DiseaseSeverity.LOW)
        ),
        tips = listOf(
            "Tinglik üçün toxumu fevralda səpin",
            "Suvarmanı yığımdan 2 həftə əvvəl dayandırın",
            "Yarpaqlar yatdıqda məhsul yetişmişdir",
            "Saxlamadan əvvəl 2-3 həftə qurudun",
            "Bir sahədə 3-4 ildən tez əkməyin"
        )
    ),
    Crop(
        id = 7,
        name = "Pambıq",
        emoji = "🌿",
        season = "Aprel - Oktyabr",
        soilType = "Gil-qumsal, duzlaşmaya davamlı",
        waterNeeds = "Yüksək",
        growthDays = 160,
        description = "Pambıq Azərbaycanda strateji bitki hesab olunur. Kür-Araz ovalığı, Muğan-Mil düzü əsas becərilmə zonasıdır.",
        diseases = listOf(
            CropDisease("Vilt", "Bitki birdən solar, gövdədə qəhvəyi zolaqlар", "Davamlı sort seçin, torpaq dezinfeksiyası", DiseaseSeverity.HIGH),
            CropDisease("Göbələk xəstəlikləri", "Kapsulada çürümə", "Fungisid, vaxtında yığım", DiseaseSeverity.MEDIUM),
            CropDisease("Pambıq sovkası", "Kapsulada deşiklər", "Bioinsektisid, feromonlu tələlər", DiseaseSeverity.HIGH),
            CropDisease("Mənənə", "Yarpaqlar buruşur, yapışqan maddə", "İnsektisid, təbii düşmənlər", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Torpaq temperaturu 14°C-dən yuxarı olanda əkin",
            "Çiçəkləmə dövründə suvarmanı artırın",
            "Baş zoğunu vaxtında qoparın",
            "Kapsulların 60%-i açılanda yığıma başlayın",
            "Növbəli əkinə ciddi riayət edin"
        )
    ),
    Crop(
        id = 8,
        name = "Çəltik",
        emoji = "🌾",
        season = "May - Oktyabr",
        soilType = "Ağır gil, su keçirməyən",
        waterNeeds = "Çox yüksək",
        growthDays = 140,
        description = "Çəltik Azərbaycanda əsasən Lənkəran-Astara zonasında becərilir. Subtropik iqlim şəraitini sevir, bollu su tələb edir.",
        diseases = listOf(
            CropDisease("Blast xəstəliyi", "Yarpaqlarda göz şəkilli ləkələr, süpürgə qırılır", "Trisilazol fungisid, davamlı sort", DiseaseSeverity.HIGH),
            CropDisease("Bakterial yanıqlıq", "Yarpaq kənarlarında sarı-qəhvəyi rəng", "Mis fungisid, sağlam toxum", DiseaseSeverity.HIGH),
            CropDisease("Çəltik midyası", "Gövdədə deşiklər, ak süpürgə", "İnsektisid, erkən əkin", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Tingliyi 30-35 günlük əkin",
            "Əkindən sonra tarlada su səviyyəsini 5-7 sm saxlayın",
            "Süd yetişkənliyində suyu boşaldın",
            "Rütubət 20%-ə enəndə yığın",
            "Torpağı hər 3-4 ildən bir qurudun"
        )
    ),
    Crop(
        id = 9,
        name = "Alma",
        emoji = "🍎",
        season = "Mart - Noyabr",
        soilType = "Dərin humuslu, drenajlı",
        waterNeeds = "Orta",
        growthDays = 150,
        description = "Alma Azərbaycanda ən geniş yayılmış meyvə bitkisidir. Gədəbəy, Şəmkir, Qax, Lerik dağlıq zonalarında yüksək keyfiyyətli alma yetişir.",
        diseases = listOf(
            CropDisease("Qabıq xərçəngi", "Gövdə və budaqlarda çat, çürümə", "Xəstə hissələri kəsin, bağçılıq boyası ilə örtün", DiseaseSeverity.HIGH),
            CropDisease("Alma kəpənəyi", "Meyvədə qurd, tökülmə", "Feromonlu tələlər, insektisid", DiseaseSeverity.HIGH),
            CropDisease("Unlu şeh", "Cavan zoğlarda ağ örtük", "Kükürd, fungisid", DiseaseSeverity.MEDIUM),
            CropDisease("Pas ləkəsi", "Yarpaqlarda narıncı ləkələr", "Mis fungisid, ardıc ağaclarını kəskin", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Hər il qış budaması aparın",
            "Yazda erkən Bordo mayesi çiləyin",
            "Hər ağaca 60-80 kq üzvi gübrə verin",
            "Meyvələrin bir qismini erkən yığıb seyrəldin",
            "Yığımdan sonra bağı yarpaqlardan təmizləyin"
        )
    ),
    Crop(
        id = 10,
        name = "Günəbaxan",
        emoji = "🌻",
        season = "Aprel - Sentyabr",
        soilType = "Humuslu, dərin torpaq",
        waterNeeds = "Aşağı",
        growthDays = 100,
        description = "Günəbaxan Azərbaycanda yağlı toxumlu bitki kimi becərilir. Quraqlığa davamlıdır, az qulluq tələb edir.",
        diseases = listOf(
            CropDisease("Sklerotiniya", "Gövdədə ağ miselyum, bitki çürüyür", "Növbəli əkin, fungisid", DiseaseSeverity.HIGH),
            CropDisease("Mildyu", "Yarpaqlarda sarı ləkələr, alt tərəfdə boz örtük", "Metalaksil fungisid", DiseaseSeverity.MEDIUM),
            CropDisease("Pas", "Narıncı toz ləkələr", "Davamlı sort, fungisid", DiseaseSeverity.MEDIUM),
            CropDisease("Günəbaxan güvəsi", "Toxumlarda sürfələr", "İnsektisid, erkən yığım", DiseaseSeverity.HIGH)
        ),
        tips = listOf(
            "70x35 sm sxemlə əkin",
            "Bir sahədə 7 ildən tez əkməyin",
            "Çiçəkləmə dövründə arı ailələri gətirin",
            "Başlar 35° əyiləndə yığıma başlayın",
            "Yığımdan dərhal sonra qurudun"
        )
    ),
    Crop(
        id = 11,
        name = "Şəkər Çuğunduru",
        emoji = "🌱",
        season = "Mart - Oktyabr",
        soilType = "Dərin humuslu gil-qumsal",
        waterNeeds = "Yüksək",
        growthDays = 170,
        description = "Şəkər çuğunduru Azərbaycanda şəkər istehsalının əsas xammalıdır. Gəncə-Qazax zonasında becərilir.",
        diseases = listOf(
            CropDisease("Serkossporioz", "Yarpaqlarda açıq qəhvəyi ləkələr", "Triazol fungisid, növbəli əkin", DiseaseSeverity.HIGH),
            CropDisease("Kök yeyən", "Cücərtilərin dibindən kəsilməsi", "Torpağa insektisid, erkən əkin", DiseaseSeverity.HIGH),
            CropDisease("Virus sarılığı", "Yarpaqlar saralır, böyümə dayanır", "Mənənəyə qarşı mübarizə, davamlı sort", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Torpağı 30-35 sm dərinliyə şumlayın",
            "45x20 sm sxemlə əkin",
            "Seyrəltməni vaxtında aparın",
            "Şəkər miqdarı maksimuma çatanda yığın",
            "Yığımdan sonra torpağı kalkiyumla zənginləşdirin"
        )
    ),
    Crop(
        id = 12,
        name = "Tütün",
        emoji = "🍃",
        season = "Aprel - Sentyabr",
        soilType = "Yüngül qumsal, humuslu",
        waterNeeds = "Orta",
        growthDays = 120,
        description = "Tütün Azərbaycanda əsasən Kəlbəcər, Qax, Zaqatala, İsmayıllı rayonlarında becərilir.",
        diseases = listOf(
            CropDisease("Mozaika virusu", "Yarpaqda ala-bula rəng, deformasiya", "Xəstə bitkiləri söküb məhv edin", DiseaseSeverity.HIGH),
            CropDisease("Peronosporoz", "Yarpaqlarda sarı ləkələr, alt tərəfdə boz örtük", "Mis fungisid, havalanma", DiseaseSeverity.HIGH),
            CropDisease("Tütün sovkası", "Yarpaqda deşiklər, sürfələr", "Bioinsektisid, yığım vaxtı", DiseaseSeverity.MEDIUM)
        ),
        tips = listOf(
            "Tingliyi istilikdə hazırlayın",
            "Güclü külək olan yerlərdən çəkinin",
            "Çiçəkləmə başlayanda tozu qoparın",
            "Yarpaqları aşağıdan yuxarıya doğru yığın",
            "Qurutma rejimini düzgün saxlayın"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropGuideScreen() {
    val navController = LocalNavController.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCrop by remember { mutableStateOf<Crop?>(null) }

    val filteredCrops = mockCrops.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    if (selectedCrop != null) {
        CropDetailScreen(
            crop = selectedCrop!!,
            onBackClick = { selectedCrop = null }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Bitki Bələdçisi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Bitki axtar...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCrops) { crop ->
                        CropCard(
                            crop = crop,
                            onClick = { selectedCrop = crop }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun CropCard(crop: Crop, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(crop.emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    crop.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    crop.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CropChip(icon = "📅", text = crop.season)
                    CropChip(icon = "⏱️", text = "${crop.growthDays} gün")
                }
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CropChip(icon: String, text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropDetailScreen(crop: Crop, onBackClick: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ümumi", "Xəstəliklər", "Tövsiyələr")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(crop.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            crop.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Bitki haqqında",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        crop.description,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        "Əsas Xüsusiyyətlər",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CropInfoRow("📅", "Mövsüm", crop.season)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CropInfoRow("🌱", "Torpaq növü", crop.soilType)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CropInfoRow("💧", "Su ehtiyacı", crop.waterNeeds)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    CropInfoRow("⏱️", "Böyümə müddəti", "${crop.growthDays} gün")
                                }
                            }
                        }
                    }

                    1 -> {
                        if (crop.diseases.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Xəstəlik məlumatı yoxdur",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(crop.diseases) { disease ->
                                DiseaseCard(disease = disease)
                            }
                        }
                    }

                    2 -> {
                        items(crop.tips) { tip ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        tip,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CropInfoRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DiseaseCard(disease: CropDisease) {
    val (bgColor, severityText, severityColor) = when (disease.severity) {
        DiseaseSeverity.LOW -> Triple(Color(0xFFE8F5E9), "Aşağı risk", Color(0xFF2E7D32))
        DiseaseSeverity.MEDIUM -> Triple(Color(0xFFFFF8E1), "Orta risk", Color(0xFFF57F17))
        DiseaseSeverity.HIGH -> Triple(Color(0xFFFFEBEE), "Yüksək risk", Color(0xFFC62828))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    disease.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = severityColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        severityText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = severityColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = severityColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Əlamətlər",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                    Text(
                        disease.symptoms,
                        fontSize = 13.sp,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "Müalicə",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.6f)
                    )
                    Text(
                        disease.treatment,
                        fontSize = 13.sp,
                        color = Color(0xFF1A1A2E).copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}