package com.ram.agroadvisor.ui.screens.resources

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ram.agroadvisor.data.CalculatorResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    onBackClick: () -> Unit = {},
    calculatorViewModel: CalculatorViewModel = viewModel()
) {
    val uiState by calculatorViewModel.uiState.collectAsState()

    var cropType by remember { mutableStateOf("") }
    var growthStage by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var fieldSize by remember { mutableStateOf("") }

    var cropTypeExpanded by remember { mutableStateOf(false) }
    var growthStageExpanded by remember { mutableStateOf(false) }
    var soilTypeExpanded by remember { mutableStateOf(false) }

    val cropTypes = listOf("Buğda", "Qarğıdalı", "Pomidor")
    val growthStages = listOf("Cücərti", "Sünbülləmə", "Vegetativ", "Saçaqlanma", "Meyvə Bağlama")
    val soilTypes = listOf("Gillicəli", "Qumlu", "Gil")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gübrə Kalkulyatoru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            // Header card
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
                    Text("🌱", fontSize = 40.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "NPK Gübrə Kalkulyatoru",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Bitkiyə lazım olan azot, fosfor və kalium miqdarını hesablayın",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Input card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Məlumatları daxil edin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Bitki növü
                    ExposedDropdownMenuBox(
                        expanded = cropTypeExpanded,
                        onExpandedChange = { cropTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = cropType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bitki növü") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cropTypeExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = cropTypeExpanded,
                            onDismissRequest = { cropTypeExpanded = false }
                        ) {
                            cropTypes.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        cropType = option
                                        cropTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Böyümə mərhələsi
                    ExposedDropdownMenuBox(
                        expanded = growthStageExpanded,
                        onExpandedChange = { growthStageExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = growthStage,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Böyümə mərhələsi") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = growthStageExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = growthStageExpanded,
                            onDismissRequest = { growthStageExpanded = false }
                        ) {
                            growthStages.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        growthStage = option
                                        growthStageExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Torpaq növü
                    ExposedDropdownMenuBox(
                        expanded = soilTypeExpanded,
                        onExpandedChange = { soilTypeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = soilType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Torpaq növü") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilTypeExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = soilTypeExpanded,
                            onDismissRequest = { soilTypeExpanded = false }
                        ) {
                            soilTypes.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        soilType = option
                                        soilTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Sahə ölçüsü
                    OutlinedTextField(
                        value = fieldSize,
                        onValueChange = { fieldSize = it.filter { c -> c.isDigit() || c == '.' } },
                        label = { Text("Sahə ölçüsü (hektar)") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        ),
                        trailingIcon = {
                            Text("ha", modifier = Modifier.padding(end = 8.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Hesabla düyməsi
                    Button(
                        onClick = {
                            val size = fieldSize.toDoubleOrNull() ?: 0.0
                            if (cropType.isNotBlank() && growthStage.isNotBlank() &&
                                soilType.isNotBlank() && size > 0
                            ) {
                                calculatorViewModel.calculate(
                                    cropType = cropType,
                                    growthStage = growthStage,
                                    soilType = soilType,
                                    fieldSizeHectares = size
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = cropType.isNotBlank() && growthStage.isNotBlank() &&
                                soilType.isNotBlank() && fieldSize.isNotBlank()
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hesabla", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Nəticə
            when (val state = uiState) {
                is CalculatorUiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Hesablanır...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                is CalculatorUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                state.message,
                                color = Color(0xFFC62828),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                is CalculatorUiState.Success -> {
                    CalculatorResultCard(result = state.data)
                    OutlinedButton(
                        onClick = { calculatorViewModel.reset() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yenidən Hesabla", fontSize = 16.sp)
                    }
                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CalculatorResultCard(result: CalculatorResponse) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // NPK nəticələri
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "NPK Nəticələri",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${result.cropType} • ${result.growthStage} • ${result.fieldSizeHectares} ha",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NPKCard(
                        label = "Azot (N)",
                        value = "${result.totalNRequired}",
                        unit = "kq",
                        color = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f)
                    )
                    NPKCard(
                        label = "Fosfor (P)",
                        value = "${result.totalPRequired}",
                        unit = "kq",
                        color = Color(0xFF388E3C),
                        modifier = Modifier.weight(1f)
                    )
                    NPKCard(
                        label = "Kalium (K)",
                        value = "${result.totalKRequired}",
                        unit = "kq",
                        color = Color(0xFFF57C00),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Hektara görə baza dəyərləri",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    BaseValueItem("N/ha", "${result.baseNPerHectare} kq")
                    BaseValueItem("P/ha", "${result.basePPerHectare} kq")
                    BaseValueItem("K/ha", "${result.baseKPerHectare} kq")
                }
            }
        }

        // AI Xülasəsi
        if (result.aiSummary.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI Xülasəsi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        result.aiSummary,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // AI Tövsiyələri
        if (result.aiSuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFF57C00),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "AI Tövsiyələri",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    result.aiSuggestions.forEachIndexed { index, suggestion ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                modifier = Modifier.size(24.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "${index + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                suggestion,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NPKCard(
    label: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                unit,
                fontSize = 12.sp,
                color = color.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                fontSize = 11.sp,
                color = color.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BaseValueItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}