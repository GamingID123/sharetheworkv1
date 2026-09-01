package com.sharethework.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.sharethework.navigation.Routes
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.components.GlowButton
import com.sharethework.ui.theme.*

@Composable
fun ProfileScreen(nav: NavController) {
    var name by remember { mutableStateOf("Pratyush") }
    var email by remember { mutableStateOf("pratyush@school.edu") }
    var classSec by remember { mutableStateOf("8-A") }
    var role by remember { mutableStateOf("Student") }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { nav.navigate(Routes.ADMIN) }) { Icon(Icons.Filled.AdminPanelSettings, null, tint = GreyText) }
        }
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(White), contentAlignment = Alignment.Center) { Text(name.take(1), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = BlackBg) }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = White)
                    Text(email, fontSize = 12.sp, color = GreyText)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BadgeBox("Class $classSec"); BadgeBox(role); BadgeBox("Active")
                    }
                }
                IconButton(onClick = { /* TODO pick image */ }) { Icon(Icons.Filled.Edit, null, tint = GreyText, modifier = Modifier.size(16.dp)) }
            }
        }
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Settings", fontWeight = FontWeight.SemiBold, color = White)
            Spacer(Modifier.height(10.dp))
            SettingRow(Icons.Filled.Person, "Edit profile", "Name, class, photo") { /* TODO edit dialog */ }
            SettingRow(Icons.Filled.Lock, "Change password", "Update your password") { }
            SettingRow(Icons.Filled.Notifications, "Notifications", "Push & in-app prefs") { nav.navigate(Routes.NOTIFICATIONS) }
            SettingRow(Icons.Filled.Shield, "Privacy", "Blocked users, reports") { }
            SettingRow(Icons.Filled.Palette, "Theme", "Black / White (dark only for now)") { }
            Divider(color = GreyBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
            SettingRow(Icons.Filled.AdminPanelSettings, "Admin dashboard", "Manage users & content") { nav.navigate(Routes.ADMIN) }
        }
        GlowButton(text = "Log out", onClick = { nav.navigate(Routes.LOGIN) { popUpTo(Routes.HOME) { inclusive = true } } })
        Text("ShareTheWork v1.0.0 • Learn. Share. Connect.", fontSize = 11.sp, color = GreyText, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BadgeBox(text: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(GreyCard).padding(horizontal = 8.dp, vertical = 4.dp)) { Text(text, fontSize = 10.sp, color = GreyText, fontWeight = FontWeight.Medium) }
}

@Composable
private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(GreyCard), contentAlignment = Alignment.Center) { Icon(icon, null, tint = White, modifier = Modifier.size(18.dp)) }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) { Text(title, fontSize = 13.sp, color = White, fontWeight = FontWeight.Medium); Text(subtitle, fontSize = 11.sp, color = GreyText) }
        Icon(Icons.Filled.ChevronRight, null, tint = GreyText, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun NotificationScreen(nav: NavController) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null, tint = White) }
            Text("Notifications", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        val items = listOf(
            Triple("📚 New Homework","Mathematics — Linear Equations due tomorrow","2h ago"),
            Triple("📝 New Classwork","Science notes uploaded by Ms. Verma","5h ago"),
            Triple("📢 Announcement","PTM on 5th September","1d ago"),
            Triple("💬 New message","Aarav sent you a message","2d ago"),
        )
        items.forEach { (t,b,time) ->
            GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Row {
                    Text(t.substring(0,2), fontSize = 18.sp); Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) { Text(t.substring(2).trim(), fontSize = 13.sp, color = White, fontWeight = FontWeight.Medium); Text(b, fontSize = 11.sp, color = GreyText) }
                    Text(time, fontSize = 10.sp, color = GreyText)
                }
            }
        }
    }
}

@Composable
fun AdminScreen(nav: NavController) {
    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null, tint = White) }
            Text("Admin Dashboard", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(12.dp))
        // Stats
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("Total Users","248", Modifier.weight(1f)); StatCard("Homework","86", Modifier.weight(1f)); StatCard("Reports","3", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("User management", fontWeight = FontWeight.SemiBold, color = White)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("Search users...", fontSize = 12.sp) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = BlackBg, unfocusedContainerColor = BlackBg))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = BlackBg), shape = RoundedCornerShape(12.dp)) { Text("Search") }
            }
            Spacer(Modifier.height(8.dp))
            listOf("Pratyush (8-A) — Student — Active","Aarav (8-B) — Student — Active","Mr. Sharma — Moderator — Active","Admin — Admin — Active").forEach { u ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(u, fontSize = 12.sp, color = GreyText, modifier = Modifier.weight(1f))
                    TextButton(onClick = {}) { Text("Manage", fontSize = 11.sp) }
                }
                Divider(color = GreyBorder, thickness = 0.3.dp)
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Moderation queue", fontWeight = FontWeight.SemiBold, color = White)
            Spacer(Modifier.height(6.dp))
            Text("Reported: \"spam message\" • by 8-A group • Action: Hide / Delete / Warn / Suspend", fontSize = 11.sp, color = GreyText)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = ErrorRed), shape = RoundedCornerShape(10.dp)) { Text("Delete", fontSize = 11.sp) }
                OutlinedButton(onClick = {}) { Text("Warn", fontSize = 11.sp) }
                OutlinedButton(onClick = {}) { Text("Dismiss", fontSize = 11.sp) }
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Moderator dashboard (scoped)", fontWeight = FontWeight.SemiBold, color = White)
            Spacer(Modifier.height(4.dp))
            Text("Moderators only see/manage their assigned class/section. They can create homework/classwork/announcements and moderate their class chats.", fontSize = 11.sp, color = GreyText)
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier) {
    GlassCard(modifier = modifier) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = White)
        Text(title, fontSize = 11.sp, color = GreyText)
    }
}
