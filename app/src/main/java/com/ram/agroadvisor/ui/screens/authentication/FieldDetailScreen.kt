package com.ram.agroadvisor.ui.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDetailsScreen(
    onCompleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    var fieldName by remember { mutableStateOf("") }
    var fieldSize by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }
    var irrigationType by remember { mutableStateOf("") }
    var currentCrop by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Field Information",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Please provide details about your farm area",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        FieldTextField(
            value = fieldName,
            onValueChange = { fieldName = it },
            label = "Field Name (e.g. Lower field)"
        )

        FieldTextField(
            value = fieldSize,
            onValueChange = { fieldSize = it },
            label = "Field Size (e.g. 10 hectare)",
            keyboardType = KeyboardType.Number
        )

        FieldTextField(
            value = soilType,
            onValueChange = { soilType = it },
            label = "Soil Type (e.g. Clay, Sandy)"
        )

        FieldTextField(
            value = irrigationType,
            onValueChange = { irrigationType = it },
            label = "Irrigation Method (e.g. Drip, Rain)"
        )

        FieldTextField(
            value = currentCrop,
            onValueChange = { currentCrop = it },
            label = "Current Crop (e.g. Wheat, Tomato)"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onCompleteClick() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                "Finish Registration",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        TextButton(
            onClick = { onBackClick() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Go Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FieldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}