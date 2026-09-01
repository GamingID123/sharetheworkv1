package com.sharethework.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sharethework.data.model.Homework
import com.sharethework.data.model.SubmissionStatus
import com.sharethework.ui.components.EmptyState
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.theme.*

@Composable
fun HomeworkScreen(nav: NavController) {
    var query by remember { mutableStateOf("") }
    var filterSubject by remember { mutableStateOf<String?>(null) }
    // TODO replace with ViewModel + ApiService.getHomework + Room cache
    val sample = remember {
        listOf(
            Homework("1","Mathematics","Linear Equations","Solve Q1-10 from Ex 2.3","8","A","2026-08-31","2026-09-02","Mr. Sharma","t1"),
            Homework("2","Science","Photosynthesis","Worksheet attached, complete diagram","8","A","2026-08-31","2026-09-01","Ms. Verma","t2"),
            Homework("3","English","Essay: Climate Change","500 words, PDF upload","8","A","2026-08-30","2026-09-03","Mr. Singh","t3"),
        )
    }
    var list by remember { mutableStateOf(sample) }
    val filtered = list.filter {
        (filterSubject == null || it.subject == filterSubject) &&
        (query.isBlank() || it.title.contains(query, true) || it.subject.contains(query, true))
    }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Homework", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            IconButton(onClick = { /* TODO moderators: create homework dialog */ }) { Icon(Icons.Filled.Add, contentDescription = "Add", tint = White) }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search homework...") }, leadingIcon = { Icon(Icons.Filled.Search, null, tint = GreyText) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = GreyCard, unfocusedContainerColor = GreyCard))
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(null to "All", "Mathematics" to "Maths", "Science" to "Science", "English" to "English").forEach { (value,label) ->
                FilterChip(selected = filterSubject == value, onClick = { filterSubject = value }, label = { Text(label, fontSize = 12.sp) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = White, selectedLabelColor = BlackBg))
            }
        }
        Spacer(Modifier.height(12.dp))
        if (filtered.isEmpty()) {
            EmptyState(icon = "📚", title = "No homework yet", subtitle = "You're all caught up! Check back later.", actionLabel = "Refresh") { list = sample }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(filtered) { hw ->
                    GlassCard(modifier = Modifier.fillMaxWidth().clickable { /* TODO detail bottom sheet */ }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) { Text(hw.subject.take(1), color = White, fontWeight = FontWeight.Bold) }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(hw.subject, fontSize = 11.sp, color = NovaPurple, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                                Text(hw.title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = White)
                                Text("${hw.description} • Due ${hw.dueDate} • ${hw.teacherName}", fontSize = 11.sp, color = GreyText, maxLines = 2)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        // If homework has Drive attachments, preview in-app else mock demo
                        val hasAttachment = hw.attachments.isNotEmpty()
                        val demoFileId = hw.attachments.firstOrNull()?.fileUrl ?: "demo-${hw.id}"
                        val demoFileName = hw.attachments.firstOrNull()?.fileName ?: "${hw.subject}_${hw.title.take(12)}.pdf"
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = { nav.navigate(com.sharethework.navigation.Routes.filePreview(demoFileId, demoFileName)) }, label = { Text(if(hasAttachment) "Preview" else "Preview", fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.Visibility, null, modifier = Modifier.size(14.dp)) })
                            AssistChip(onClick = { nav.navigate(com.sharethework.navigation.Routes.filePreview(demoFileId, demoFileName)) }, label = { Text("Download", fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.Download, null, modifier = Modifier.size(14.dp)) })
                            AssistChip(onClick = { /* TODO ApiService.markComplete(hw.id) */ }, label = { Text("Mark done", fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(14.dp)) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassworkScreen(nav: NavController) {
    var query by remember { mutableStateOf("") }
    val sample = remember {
        listOf(
            Triple("Science","Photosynthesis Notes","Ms. Verma • Today"),
            Triple("Mathematics","Number System - Worksheet","Mr. Sharma • Yesterday"),
            Triple("English","Grammar: Tenses PDF","Mr. Singh • 29 Aug"),
        )
    }
    Column(modifier = Modifier.fillMaxSize().background(BlackBg).padding(16.dp)) {
        Text("Classwork", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search classwork...") }, leadingIcon = { Icon(Icons.Filled.Search, null, tint = GreyText) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreyBorder, unfocusedBorderColor = GreyBorder, focusedContainerColor = GreyCard, unfocusedContainerColor = GreyCard))
        Spacer(Modifier.height(12.dp))
        val filtered = sample.filter { it.second.contains(query, true) || it.first.contains(query, true) }
        if (filtered.isEmpty()) {
            EmptyState("📝", "No classwork yet", "Notes and worksheets will appear here.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered.size) { i ->
                    val (subj, title, meta) = filtered[i]
                    GlassCard(modifier = Modifier.fillMaxWidth().clickable { }) {
                        Text(subj, fontSize = 11.sp, color = NovaPurple, fontWeight = FontWeight.SemiBold)
                        Text(title, fontWeight = FontWeight.Medium, color = White, fontSize = 14.sp)
                        Text(meta, fontSize = 11.sp, color = GreyText)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = { nav.navigate(com.sharethework.navigation.Routes.filePreview("demo-classwork-$i", "$title.pdf")) }, label = { Text("Preview", fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.Visibility, null, modifier = Modifier.size(14.dp)) })
                            AssistChip(onClick = { nav.navigate(com.sharethework.navigation.Routes.filePreview("demo-classwork-$i", "$title.pdf")) }, label = { Text("Download", fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.Download, null, modifier = Modifier.size(14.dp)) })
                        }
                    }
                }
            }
        }
    }
}
