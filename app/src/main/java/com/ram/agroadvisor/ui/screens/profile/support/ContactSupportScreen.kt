package com.ram.agroadvisor.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactSupportScreen(onBackClick: () -> Unit) {
    var message by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact Support") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Send us a message",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("Tell us how we can help...") },
                label = { Text("Message") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Handle send */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Send, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Request")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Other Ways to Reach Us", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ListItem(
                headlineContent = { Text("Email Support") },
                supportingContent = { Text("qasimovabbas009@gmail.com") },
                leadingContent = { Icon(Icons.Default.Email, null) }
            )
            ListItem(
                headlineContent = { Text("Phone Support") },
                supportingContent = { Text("+994 60 480 9000") },
                leadingContent = { Icon(Icons.Default.Phone, null) }
            )
        }
    }
}