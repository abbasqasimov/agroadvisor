package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ram.agroadvisor.ui.navigation.LocalNavController
import com.ram.agroadvisor.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDetailsScreen() {
    val navController = LocalNavController.current
    val onCompleteClick = {
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Welcome.route) { inclusive = true }
        }
    }
    val onBackClick = { navController.popBackStack() }
    val scrollState = rememberScrollState()

    var fieldName by remember { mutableStateOf("") }
    var fieldSize by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var irrigationType by remember { mutableStateOf("") }
    var currentCrop by remember { mutableStateOf("") }

    // Validation states
    var fieldNameError by remember { mutableStateOf<String?>(null) }
    var fieldSizeError by remember { mutableStateOf<String?>(null) }
    var soilTypeError by remember { mutableStateOf<String?>(null) }
    var irrigationError by remember { mutableStateOf<String?>(null) }
    var cropError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Touched states
    var fieldNameTouched by remember { mutableStateOf(false) }
    var fieldSizeTouched by remember { mutableStateOf(false) }
    var soilTypeTouched by remember { mutableStateOf(false) }
    var irrigationTouched by remember { mutableStateOf(false) }
    var cropTouched by remember { mutableStateOf(false) }

    // Validation functions
    fun validateFieldName(name: String): String? {
        return when {
            name.isBlank() -> "Sahə adı tələb olunur"
            name.length < 3 -> "Ad ən az 3 simvol olmalıdır"
            else -> null
        }
    }

    fun validateFieldSize(size: String): String? {
        return when {
            size.isBlank() -> "Sahə ölçüsü tələb olunur"
            else -> null
        }
    }

    fun validateSoilType(soil: String): String? {
        return when {
            soil.isBlank() -> "Torpaq növü tələb olunur"
            else -> null
        }
    }

    fun validateIrrigation(irrigation: String): String? {
        return when {
            irrigation.isBlank() -> "Suvarma üsulu tələb olunur"
            else -> null
        }
    }

    fun validateCrop(crop: String): String? {
        return when {
            crop.isBlank() -> "Cari bitki tələb olunur"
            else -> null
        }
    }

    // Real-time validation
    LaunchedEffect(fieldName, fieldNameTouched) {
        if (fieldNameTouched) fieldNameError = validateFieldName(fieldName)
    }

    LaunchedEffect(fieldSize, fieldSizeTouched) {
        if (fieldSizeTouched) fieldSizeError = validateFieldSize(fieldSize)
    }

    LaunchedEffect(soilType, soilTypeTouched) {
        if (soilTypeTouched) soilTypeError = validateSoilType(soilType)
    }

    LaunchedEffect(irrigationType, irrigationTouched) {
        if (irrigationTouched) irrigationError = validateIrrigation(irrigationType)
    }

    LaunchedEffect(currentCrop, cropTouched) {
        if (cropTouched) cropError = validateCrop(currentCrop)
    }

    // Handle complete
    fun handleComplete() {
        fieldNameTouched = true
        fieldSizeTouched = true
        soilTypeTouched = true
        irrigationTouched = true
        cropTouched = true

        fieldNameError = validateFieldName(fieldName)
        fieldSizeError = validateFieldSize(fieldSize)
        soilTypeError = validateSoilType(soilType)
        irrigationError = validateIrrigation(irrigationType)
        cropError = validateCrop(currentCrop)

        if (fieldNameError == null && fieldSizeError == null &&
            soilTypeError == null && irrigationError == null && cropError == null) {
            isLoading = true

            // No backend - just loading and navigate
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                isLoading = false
                onCompleteClick()
            }
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Sahə Məlumatı",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Zəhmət olmasa təsərrüfat sahənizin təfərrüatlarını daxil edin",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Field Name
            OutlinedTextField(
                value = fieldName,
                onValueChange = {
                    fieldName = it
                    if (!fieldNameTouched) fieldNameTouched = true
                },
                label = { Text("Sahə adı (məs. Aşağı sahə)") },
                singleLine = true,
                isError = fieldNameError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                supportingText = {
                    if (fieldNameError != null) {
                        Text(fieldNameError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (fieldNameError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Field Size
            OutlinedTextField(
                value = fieldSize,
                onValueChange = {
                    fieldSize = it
                    if (!fieldSizeTouched) fieldSizeTouched = true
                },
                label = { Text("Sahə ölçüsü (məs. 10 hektar)") },
                singleLine = true,
                isError = fieldSizeError != null,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                supportingText = {
                    if (fieldSizeError != null) {
                        Text(fieldSizeError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (fieldSizeError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Soil Type
            OutlinedTextField(
                value = soilType,
                onValueChange = {
                    soilType = it
                    if (!soilTypeTouched) soilTypeTouched = true
                },
                label = { Text("Torpaq növü (məs. Gilli, Qumlu)") },
                singleLine = true,
                isError = soilTypeError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                supportingText = {
                    if (soilTypeError != null) {
                        Text(soilTypeError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (soilTypeError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Irrigation Type
            OutlinedTextField(
                value = irrigationType,
                onValueChange = {
                    irrigationType = it
                    if (!irrigationTouched) irrigationTouched = true
                },
                label = { Text("Suvarma üsulu (məs. Damcı, Yağış)") },
                singleLine = true,
                isError = irrigationError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                supportingText = {
                    if (irrigationError != null) {
                        Text(irrigationError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (irrigationError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Current Crop
            OutlinedTextField(
                value = currentCrop,
                onValueChange = {
                    currentCrop = it
                    if (!cropTouched) cropTouched = true
                },
                label = { Text("Cari bitki (məs. Buğda, Pomidor)") },
                singleLine = true,
                isError = cropError != null,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                ),
                supportingText = {
                    if (cropError != null) {
                        Text(cropError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                trailingIcon = {
                    if (cropError != null) {
                        Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { handleComplete() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Qeydiyyatı tamamla",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            TextButton(
                onClick = { onBackClick() },
                enabled = !isLoading,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Geri qayıt", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}