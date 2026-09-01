package com.sharethework.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.theme.*

data class NovaMsg(val role: String, val content: String)

@Composable
fun NovaAiScreen(nav: NavController) {
    var input by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<String>()) }
    var messages by remember { mutableStateOf(listOf<NovaMsg>(
        NovaMsg("assistant","Hi, I'm Nova — your AI study assistant. Ask me to explain a topic, summarize notes, or create a quiz! ✨")
    ))}
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg)) {
        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(NovaGlow), contentAlignment = Alignment.Center) { Icon(Icons.Filled.AutoAwesome, null, tint = NovaPurple) }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Nova", fontWeight = FontWeight.Bold, color = White)
                Text("AI Study Assistant • Online", fontSize = 11.sp, color = GreyText)
            }
            IconButton(onClick = { messages = listOf(NovaMsg("assistant","New chat started. How can I help?")); history = emptyList() }) { Icon(Icons.Filled.Refresh, null, tint = GreyText) }
            IconButton(onClick = { /* TODO show history drawer */ }) { Icon(Icons.Filled.History, null, tint = GreyText) }
        }
        // Suggested prompts (show when only welcome msg)
        if (messages.size == 1) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Try asking:", fontSize = 12.sp, color = GreyText, fontWeight = FontWeight.Medium)
                val prompts = listOf("Explain photosynthesis step-by-step","Summarize this chapter for revision","Create a 5-question quiz on algebra","Help me understand this homework")
                prompts.forEach { p ->
                    GlassCard(modifier = Modifier.fillMaxWidth().clickable { input = p }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💡", fontSize = 14.sp); Spacer(Modifier.width(8.dp)); Text(p, fontSize = 12.sp, color = White, modifier = Modifier.weight(1f)); Icon(Icons.Filled.ChevronRight, null, tint = GreyText, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(messages) { m ->
                val isUser = m.role == "user"
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    if (!isUser) Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(NovaGlow), contentAlignment = Alignment.Center) { Text("N", fontSize = 10.sp, color = NovaPurple, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(if (isUser) 0.dp else 8.dp))
                    Box(modifier = Modifier.widthIn(max = 300.dp).clip(RoundedCornerShape(16.dp)).background(if (isUser) White else GreyCard).padding(12.dp)) {
                        Text(m.content, color = if (isUser) BlackBg else White, fontSize = 13.sp)
                    }
                }
            }
            if (loading) item { Row { Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(NovaGlow), contentAlignment = Alignment.Center){ Text("N", fontSize=10.sp, color=NovaPurple, fontWeight=FontWeight.Bold)}; Spacer(Modifier.width(8.dp)); Box(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(GreyCard).padding(12.dp)){ Text("Nova is thinking...", color = GreyText, fontSize=12.sp)}}}
        }
        Divider(color = GreyBorder, thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO pick file for analyze */ }) { Icon(Icons.Filled.AttachFile, null, tint = GreyText) }
            OutlinedTextField(value = input, onValueChange = { input = it }, placeholder = { Text("Ask Nova anything...", fontSize = 13.sp, color = GreyText) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = BlackBg, unfocusedContainerColor = BlackBg))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (input.isBlank() || loading) return@IconButton
                val q = input; input = ""
                messages = messages + NovaMsg("user", q)
                loading = true
                // TODO: call backend /api/ai/chat via ApiService.aiChat (server proxies to OpenAI)
                // Simulated response delay
                // In real impl: viewModel.sendToNova(q) -> collect response
            }, modifier = Modifier.clip(CircleShape).background(White).size(42.dp)) { Icon(Icons.Filled.Send, null, tint = BlackBg, modifier = Modifier.size(18.dp)) }
        }
        // Simulated AI reply after user sends (mock for UI demo without backend)
        LaunchedEffect(messages.size) {
            if (messages.lastOrNull()?.role == "user" && loading) {
                kotlinx.coroutines.delay(1200)
                val last = messages.last().content
                val reply = when {
                    last.contains("photosynthesis", true) -> "Photosynthesis is how plants make food:\n1. Chlorophyll captures sunlight\n2. CO₂ + H₂O → Glucose + O₂\n3. Occurs in chloroplasts. Want a quiz?"
                    last.contains("quiz", true) -> "Here's a quick quiz:\n1. What is the chemical formula for glucose?\n2. Solve: 2x + 5 = 15\nReply with answers and I'll check!"
                    last.contains("summarize", true) -> "Upload or paste your chapter text and I'll create concise bullet notes + key definitions."
                    else -> "Great question! Here's a step-by-step explanation:\n• Break the problem into parts\n• Apply the formula\n• Verify the answer.\nAsk me to go deeper or generate practice Qs."
                }
                messages = messages + NovaMsg("assistant", reply)
                loading = false
            }
        }
    }
}
