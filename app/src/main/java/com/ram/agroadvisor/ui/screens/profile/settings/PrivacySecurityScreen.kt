package com.ram.agroadvisor.ui.screens.profile.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ram.agroadvisor.ui.navigation.LocalNavController
import com.ram.agroadvisor.ui.navigation.Screen

/** Public-facing privacy policy URL. Change this constant if the hosted page moves. */
private const val PRIVACY_POLICY_URL = "https://agroadvisor.az/privacy-policy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen() {
    val navController = LocalNavController.current
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Məxfilik və Təhlükəsizlik",
                        fontSize = 18.sp,
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Məlumatlarınız qorunur",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "AgroAdvisor şəxsi məlumatlarınızı şifrələmə və " +
                                    "təhlükəsiz protokollarla qoruyur.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // ---- Data we collect ----
            InfoSection(
                icon = Icons.Outlined.Person,
                title = "Topladığımız məlumatlar",
                bullets = listOf(
                    "Ad, soyad və e-poçt ünvanı — hesabınızı yaratmaq üçün.",
                    "Şifrə — yalnız şifrələnmiş formada (heç vaxt açıq mətn olaraq saxlanmır).",
                    "Yüklədiyiniz bitki şəkilləri — yalnız xəstəlik təhlili üçün istifadə olunur.",
                    "AI köməkçi ilə yazışmalarınız — keçmiş söhbətləri bərpa etmək üçün.",
                    "Cihaz məkanı (icazə verildikdə) — yalnız yerli hava məlumatı üçün."
                )
            )

            // ---- How we use it ----
            InfoSection(
                icon = Icons.Outlined.VerifiedUser,
                title = "Məlumatlardan necə istifadə edirik",
                bullets = listOf(
                    "Hava şəraitinə uyğun kənd təsərrüfatı tövsiyələri vermək.",
                    "Bitki xəstəliklərini AI ilə müəyyən etmək.",
                    "Sahənizə uyğun NPK gübrə hesablamaları aparmaq.",
                    "Kritik hava xəbərdarlıqları üçün bildirişlər göndərmək.",
                    "Xidmət keyfiyyətini yaxşılaşdırmaq."
                )
            )

            // ---- Security measures ----
            InfoSection(
                icon = Icons.Outlined.Lock,
                title = "Təhlükəsizlik tədbirləri",
                bullets = listOf(
                    "Bütün şəbəkə əlaqələri HTTPS şifrələməsi ilə qorunur.",
                    "JWT tokenlər cihazınızın özəl saxlama sahəsində saxlanılır.",
                    "Şifrələr serverdə yalnız hash formasında saxlanır.",
                    "Şəxsi məlumatlarınız üçüncü tərəflərə satılmır və paylaşılmır."
                )
            )

            // ---- Your rights ----
            InfoSection(
                icon = Icons.Outlined.Gavel,
                title = "Hüquqlarınız",
                bullets = listOf(
                    "Hesab məlumatlarınıza istənilən vaxt baxa bilərsiniz.",
                    "Şifrənizi Hesab Parametrlərindən yeniləyə bilərsiniz.",
                    "Hesabınızı silmək üçün dəstəklə əlaqə saxlayın.",
                    "İcazələri (məkan, kamera) cihaz parametrlərindən geri ala bilərsiniz."
                )
            )

            // ---- Quick action: change password ----
            ActionRow(
                icon = Icons.Outlined.Lock,
                title = "Şifrəni dəyişdir",
                subtitle = "Hesab Parametrləri vasitəsilə",
                onClick = { navController.navigate(Screen.AccountSettings.route) }
            )

            // ---- External link: full policy ----
            ActionRow(
                icon = Icons.Outlined.Cloud,
                title = "Tam Məxfilik Siyasəti",
                subtitle = PRIVACY_POLICY_URL,
                trailing = {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = {
                    runCatching {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }
            )

            // ---- External link: contact ----
            ActionRow(
                icon = Icons.Outlined.Visibility,
                title = "Sual və ya şikayət?",
                subtitle = "Dəstək komandamızla əlaqə saxlayın",
                onClick = { navController.navigate(Screen.ContactSupport.route) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Son yenilənmə: 13 May 2026",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoSection(
    icon: ImageVector,
    title: String,
    bullets: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            bullets.forEach { bullet ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = bullet,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit = {
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    },
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}
