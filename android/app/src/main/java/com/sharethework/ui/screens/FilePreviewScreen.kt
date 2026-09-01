package com.sharethework.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.sharethework.data.remote.Config
import com.sharethework.ui.theme.*

@Composable
fun FilePreviewScreen(fileId: String, fileName: String, nav: NavController) {
    val context = LocalContext.current
    val isImage = fileName.lowercase().endsWith(".jpg") || fileName.lowercase().endsWith(".jpeg") || fileName.lowercase().endsWith(".png") || fileName.lowercase().endsWith(".webp")
    val isPdf = fileName.lowercase().endsWith(".pdf")
    val token = "" // TODO: retrieve JWT from DataStore
    // Backend serves via /api/storage/files/{id}/preview (also /api/drive/files/{id}/preview alias) -> Firebase Storage proxy
    // Preview is in-app (image via Coil, PDF via WebView gview), download via DownloadManager below.

    Column(modifier = Modifier.fillMaxSize().background(BlackBg)) {
        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null, tint = White) }
            Column(modifier = Modifier.weight(1f)) {
                Text(fileName, color = White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text(if (isImage) "Image • Preview in-app" else if (isPdf) "PDF • Preview in-app" else "File • Preview in-app", color = GreyText, fontSize = 11.sp)
            }
            IconButton(onClick = { downloadFile(context, fileId, fileName) }) { Icon(Icons.Filled.Download, null, tint = White) }
            IconButton(onClick = { shareFile(context, fileId) }) { Icon(Icons.Filled.Share, null, tint = GreyText) }
        }

        if (isImage) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().background(BlackBg), contentAlignment = Alignment.Center) {
                // Firebase preview streams image via backend proxy
                val previewUrl = "${Config.API_BASE_URL}storage/files/$fileId/preview"
                AsyncImage(
                    model = previewUrl,
                    contentDescription = fileName,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            }
        } else {
            // PDF / Docs preview via WebView using Google Docs viewer or direct stream
            // We load: https://docs.google.com/gview?embedded=true&url=<encoded previewUrl>
            // For auth-required files we use backend inline stream inside WebView with header.
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.allowFileAccess = true
                        webViewClient = WebViewClient()
                    }
                },
                update = { webView ->
                    // Firebase Storage preview endpoint streams PDF inline
                    val previewUrl = "${Config.API_BASE_URL}storage/files/$fileId/preview"
                    if (isPdf) {
                        val gview = "https://docs.google.com/gview?embedded=true&url=" + Uri.encode(previewUrl)
                        // Pass Authorization header if token available
                        if (token.isNotEmpty()) webView.loadUrl(gview, mapOf("Authorization" to "Bearer $token"))
                        else webView.loadUrl(gview)
                    } else {
                        if (token.isNotEmpty()) webView.loadUrl(previewUrl, mapOf("Authorization" to "Bearer $token"))
                        else webView.loadUrl(previewUrl)
                    }
                },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }

        Row(modifier = Modifier.fillMaxWidth().background(GreyCard).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { downloadFile(context, fileId, fileName) },
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = BlackBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) { Icon(Icons.Filled.Download, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Download") }
            OutlinedButton(
                onClick = { downloadFile(context, fileId, fileName) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) { Text("Save to device", color = White) }
        }
    }
}

private fun downloadFile(context: Context, fileId: String, fileName: String) {
    try {
        val url = "${com.sharethework.data.remote.Config.API_BASE_URL}storage/files/$fileId/download"
        val req = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading from ShareTheWork (Firebase)")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setMimeType(if (fileName.endsWith(".pdf")) "application/pdf" else "*/*")
            // Note: DownloadManager cannot add Bearer header; backend should also accept ?token= query for downloads
            // For now we rely on cookie/header via WebView; alternative is to download via OkHttp with auth then save.
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(req)
        Toast.makeText(context, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun shareFile(context: Context, fileId: String) {
    val url = "${com.sharethework.data.remote.Config.API_BASE_URL}storage/files/$fileId/preview"
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_TEXT, url)
    }
    context.startActivity(android.content.Intent.createChooser(intent, "Share file"))
}
