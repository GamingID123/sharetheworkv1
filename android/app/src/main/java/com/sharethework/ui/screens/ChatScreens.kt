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
import com.sharethework.data.model.Conversation
import com.sharethework.data.model.Message
import com.sharethework.ui.components.EmptyState
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ChatListScreen(nav: NavController) {
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf(0) } // 0 all, 1 private, 2 groups
    val conversations = remember {
        listOf(
            Conversation("1","8-A (Class Group)", true, false, null, "Pratyush: Homework done?", "10:42 AM", 3, true),
            Conversation("2","Everyone • School", false, true, null, "Admin: PTM reminder", "09:10 AM", 1, false),
            Conversation("3","Aarav Singh", false, false, null, "Yo, send notes?", "Yesterday", 0, true),
            Conversation("4","Ms. Verma", false, false, null, "Great work!", "Yesterday", 0, false),
        )
    }
    val filtered = conversations.filter {
        (query.isBlank() || it.name.contains(query, true) || (it.lastMessage?.contains(query, true) == true)) &&
        when(tab){1 -> !it.isGroup && !it.isCommunity; 2 -> it.isGroup; else -> true}
    }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Chats", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO new chat */ }) { Icon(Icons.Filled.Edit, null, tint = White) }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search conversations") }, leadingIcon = { Icon(Icons.Filled.Search, null, tint = GreyText) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = GreyCard, unfocusedContainerColor = GreyCard))
        Spacer(Modifier.height(10.dp))
        TabRow(selectedTabIndex = tab, containerColor = BlackBg, contentColor = White, divider = {}) {
            listOf("All","Private","Groups").forEachIndexed { i, t ->
                Tab(selected = tab==i, onClick = { tab = i }, text = { Text(t, fontSize = 13.sp) }, selectedContentColor = White, unselectedContentColor = GreyText)
            }
        }
        Spacer(Modifier.height(10.dp))
        if (filtered.isEmpty()) {
            EmptyState("💬","No messages yet","Start a conversation with your class.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(filtered) { c ->
                    GlassCard(modifier = Modifier.fillMaxWidth().clickable { nav.navigate("chat_detail/${c.id}") }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
                                Text(c.name.take(2).uppercase(), color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (c.online) Box(modifier = Modifier.align(Alignment.BottomEnd).size(10.dp).clip(CircleShape).background(Success).padding(1.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(c.name, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = White, modifier = Modifier.weight(1f))
                                    Text(c.lastMessageTime ?: "", fontSize = 11.sp, color = GreyText)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(c.lastMessage ?: "No messages", fontSize = 12.sp, color = GreyText, maxLines = 1, modifier = Modifier.weight(1f))
                                    if (c.unreadCount > 0) Box(modifier = Modifier.clip(CircleShape).background(White).padding(horizontal = 6.dp, vertical = 2.dp)) { Text("${c.unreadCount}", fontSize = 11.sp, color = BlackBg, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDetailScreen(conversationId: String, nav: NavController) {
    var text by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Message?>(null) }
    var messages by remember {
        mutableStateOf(listOf(
            Message("m1", conversationId, "u2","Aarav","Hey, did you finish maths HW?", "10:30 AM", false, true),
            Message("m2", conversationId, "me","You","Almost! Need help with Q5?", "10:31 AM", true, true),
            Message("m3", conversationId, "u2","Aarav","Yes please 🙏", "10:32 AM", false, false),
        ))
    }
    var typing by remember { mutableStateOf(false) }
    // Realtime sync: poll backend every 2.5s (Supabase Realtime would push; polling ensures cross-device sync even offline→online)
    var syncOk by remember { mutableStateOf(true) }
    LaunchedEffect(conversationId) {
        while (true) {
            delay(2500)
            try {
                // TODO: val fresh = api.getMessages(conversationId)
                // messages = fresh
                syncOk = true
            } catch (_: Exception) { syncOk = false }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg)) {
        // Top bar
        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null, tint = White) }
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(White.copy(0.1f)), contentAlignment = Alignment.Center) { Text("8A", fontSize = 10.sp, color = White, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("8-A", color = White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(if (typing) "typing..." else "Online • 24 members", color = Success, fontSize = 11.sp)
            }
            IconButton(onClick = { /* TODO call */ }) { Icon(Icons.Filled.MoreVert, null, tint = White) }
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), reverseLayout = false) {
            items(messages) { m ->
                val isMe = m.isMe
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                    if (m.replyToId != null) Text("↳ replied", fontSize = 10.sp, color = GreyText, modifier = Modifier.padding(bottom = 2.dp))
                    Box(modifier = Modifier.widthIn(max = 300.dp).clip(RoundedCornerShape(16.dp, 16.dp, if (isMe) 4.dp else 16.dp, if (isMe) 16.dp else 4.dp)).background(if (isMe) White else GreyCard).clickable { replyTo = m }.padding(10.dp)) {
                        Column {
                            if (!isMe) Text(m.senderName, fontSize = 11.sp, color = NovaPurple, fontWeight = FontWeight.SemiBold)
                            Text(m.text, color = if (isMe) BlackBg else White, fontSize = 13.sp)
                            Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                                Text(m.timestamp, fontSize = 10.sp, color = if (isMe) BlackBg.copy(0.5f) else GreyText)
                                if (isMe) { Spacer(Modifier.width(4.dp)); Icon(if (m.isRead) Icons.Filled.DoneAll else Icons.Filled.Done, null, tint = if (isMe) BlackBg.copy(0.5f) else GreyText, modifier = Modifier.size(12.dp)) }
                            }
                        }
                    }
                    Row {
                        TextButton(onClick = { replyTo = m }) { Text("Reply", fontSize = 10.sp, color = GreyText) }
                        TextButton(onClick = { /* TODO report */ }) { Text("Report", fontSize = 10.sp, color = GreyText) }
                    }
                }
            }
            if (typing) item { Text("Aarav is typing...", fontSize = 11.sp, color = GreyText, modifier = Modifier.padding(4.dp)) }
        }
        if (replyTo != null) {
            Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Replying to ${replyTo!!.senderName}: ${replyTo!!.text.take(30)}", fontSize = 11.sp, color = GreyText, modifier = Modifier.weight(1f))
                IconButton(onClick = { replyTo = null }) { Icon(Icons.Filled.Close, null, tint = GreyText, modifier = Modifier.size(16.dp)) }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = text, onValueChange = { text = it; typing = it.isNotEmpty() }, placeholder = { Text("Message...", color = GreyText, fontSize = 13.sp) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = BlackBg, unfocusedContainerColor = BlackBg))
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                if (text.isBlank()) return@IconButton
                // TODO ApiService.sendMessage + realtime subscription
                messages = messages + Message("m${messages.size+1}", conversationId, "me","You", text, "now", true, false, replyTo?.id)
                text = ""; replyTo = null; typing = false
            }, modifier = Modifier.clip(CircleShape).background(White).size(44.dp)) { Icon(Icons.Filled.Send, null, tint = BlackBg) }
        }
    }
}
