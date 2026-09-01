package com.sharethework.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharethework.ui.theme.*

@Composable
fun GlowButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(listOf(Color.White, Color(0xFFE0E0E0)))
            )
            .border(0.5.dp, GlowWhite, RoundedCornerShape(16.dp))
    ) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = BlackBg),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GreyCard.copy(alpha = 0.9f))
            .border(1.dp, GreyBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun EmptyState(icon: String, title: String, subtitle: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(icon, fontSize = 48.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = LightText)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = GreyText)
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            GlowButton(text = actionLabel, onClick = onAction, modifier = Modifier.width(180.dp))
        }
    }
}

@Composable
fun OfflineBanner() {
    Box(modifier = Modifier.fillMaxWidth().background(Warning.copy(alpha = 0.15f)).border(1.dp, Warning.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(10.dp), contentAlignment = Alignment.Center) {
        Text("You're offline — showing cached content", color = Warning, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = LightText)
        if (action != null) TextButton(onClick = { onAction?.invoke() }) { Text(action, color = GreyText, fontSize = 13.sp) }
    }
}
