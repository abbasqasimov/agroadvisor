package com.ram.agroadvisor.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ram.agroadvisor.data.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAssistantScreen(
    mainPadding: PaddingValues,
    onBackClick: () -> Unit = {}
) {
    val suggestedQuestions = listOf(
        "What's the best time to plant wheat?",
        "How do I treat crop diseases?",
        "Check current market prices",
        "Irrigation schedule for this week"
    )

    val messages = remember { mutableStateListOf<ChatMessage>() }
    var messageText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Hello! I'm your AI agricultural assistant. How can I help you?", getCurrentTime(), true))
        }
    }

    fun handleSend(text: String) {
        if (text.isNotBlank()) {
            val userText = text.trim()
            messages.add(ChatMessage(userText, getCurrentTime(), false))
            messageText = ""
            coroutineScope.launch {
                delay(100)
                listState.animateScrollToItem(messages.size)
                delay(1000)
                messages.add(ChatMessage("I'm analyzing your question about '$userText'...", getCurrentTime(), true))
                delay(100)
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF5F5F5),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White),
                            contentAlignment = Alignment.Center
                        ) { Text("🤖", fontSize = 24.sp) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("AI Assistant", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Online", fontSize = 12.sp, color = Color.White.copy(0.7f))
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2E7D32), titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask me everything...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = { handleSend(messageText) },
                        containerColor = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg.text, msg.time, msg.isAI)
            }

            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("✨ Suggested questions", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(12.dp))
                    suggestedQuestions.forEach { question ->
                        SuggestedQuestionChip(question) { handleSend(question) }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: String, time: String, isAI: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
    ) {
        if (isAI) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFF4CAF50)), contentAlignment = Alignment.Center) {
                Text("🤖", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(modifier = Modifier.widthIn(max = 280.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = if (isAI) 4.dp else 16.dp, topEnd = if (isAI) 16.dp else 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(if (isAI) Color.White else Color(0xFF4CAF50))
                    .padding(12.dp)
            ) {
                Text(text = message, fontSize = 14.sp, color = if (isAI) Color.Black else Color.White)
            }
            Text(text = time, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }
    }
}

@Composable
fun SuggestedQuestionChip(question: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(text = question, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(16.dp))
    }
}

fun getCurrentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())