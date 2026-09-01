package com.sharethework.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.sharethework.navigation.Routes
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.components.OfflineBanner
import com.sharethework.ui.components.SectionHeader
import com.sharethework.ui.theme.*

@Composable
fun HomeScreen(nav: NavController) {
    var isOffline by remember { mutableStateOf(false) } // TODO connect to connectivity observer
    LazyColumn(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Good afternoon, Pratyush 👋", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                    Text("Class 8-A  •  Today: 1 Sep 2026", fontSize = 12.sp, color = GreyText)
                }
                IconButton(onClick = { nav.navigate(Routes.NOTIFICATIONS) }) {
                    BadgedBox(badge = { Badge(containerColor = ErrorRed) { Text("3") } }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications", tint = White)
                    }
                }
            }
        }
        if (isOffline) item { OfflineBanner() }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                QuickCard("📚", "Homework", "2 due today", Modifier.weight(1f)) { nav.navigate(Routes.HOMEWORK) }
                QuickCard("📝", "Classwork", "3 new notes", Modifier.weight(1f)) { nav.navigate(Routes.CLASSWORK) }
            }
        }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth().clickable { nav.navigate(Routes.AI) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(NovaGlow), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = NovaPurple)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nova AI", fontWeight = FontWeight.SemiBold, color = White)
                        Text("Ask anything, get step-by-step help", fontSize = 12.sp, color = GreyText)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = GreyText)
                }
            }
        }
        item { SectionHeader("Today") }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                HomeworkRow("Mathematics", "Algebra: Linear Equations", "Due tomorrow • Mr. Sharma", true)
                Divider(color = GreyBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 10.dp))
                HomeworkRow("Science", "Photosynthesis Worksheet", "Due today • Ms. Verma", false)
            }
        }
        item { SectionHeader("Announcements", "View all") { } }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                AnnouncementRow("📢", "PTM on 5th September", "Parent-teacher meeting for 8-A at 10 AM. Attendance required.", important = true)
                Spacer(Modifier.height(10.dp))
                AnnouncementRow("🎉", "Science Exhibition", "Submit your project ideas by 3rd Sep.", important = false)
            }
        }
        item { SectionHeader("Recent activity") }
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                ActivityRow("💬", "New message in 8-A group")
                ActivityRow("✅", "You marked Maths homework as completed")
                ActivityRow("📝", "New classwork: English Grammar Notes")
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun QuickCard(icon: String, title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    GlassCard(modifier = modifier.clickable(onClick = onClick)) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = White)
        Text(subtitle, fontSize = 11.sp, color = GreyText)
    }
}

@Composable
private fun HomeworkRow(subject: String, title: String, meta: String, important: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(if (important) ErrorRed else Success))
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(subject, fontSize = 11.sp, color = GreyText, letterSpacing = 0.5.sp)
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = White)
            Text(meta, fontSize = 11.sp, color = GreyText)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = GreyText, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun AnnouncementRow(icon: String, title: String, body: String, important: Boolean) {
    Row {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = White, modifier = Modifier.weight(1f))
                if (important) Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ErrorRed.copy(alpha = 0.15f)).padding(horizontal = 6.dp, vertical = 2.dp)) { Text("IMPORTANT", fontSize = 9.sp, color = ErrorRed, fontWeight = FontWeight.Bold) }
            }
            Text(body, fontSize = 12.sp, color = GreyText)
        }
    }
}

@Composable
private fun ActivityRow(icon: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 14.sp)
        Spacer(Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = GreyText)
    }
}
