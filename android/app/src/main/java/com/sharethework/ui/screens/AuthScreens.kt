package com.sharethework.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.sharethework.R
import com.sharethework.ui.components.GlassCard
import com.sharethework.ui.components.GlowButton
import com.sharethework.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: (Boolean) -> Unit) {
    LaunchedEffect(Unit) { delay(1600); onFinished(false) }
    Box(modifier = Modifier.fillMaxSize().background(BlackBg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.drawable.ic_sharethework_logo), contentDescription = "ShareTheWork logo",
                modifier = Modifier.size(84.dp).clip(RoundedCornerShape(24.dp)))
            Spacer(Modifier.height(16.dp))
            Text("ShareTheWork", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = White)
            Text("Learn. Share. Connect.", fontSize = 13.sp, color = GreyText, letterSpacing = 1.5.sp)
        }
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit, onSignup: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg).verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(40.dp))
        Image(painter = painterResource(id = R.drawable.ic_sharethework_logo), contentDescription = "Logo",
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)))
        Spacer(Modifier.height(12.dp))
        Text("Welcome back", style = MaterialTheme.typography.headlineMedium)
        Text("Sign in to your school account", color = GreyText, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("School Email") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            if (error != null) { Spacer(Modifier.height(8.dp)); Text(error!!, color = ErrorRed, fontSize = 12.sp) }
            Spacer(Modifier.height(16.dp))
            GlowButton(text = if (loading) "Signing in..." else "Sign In", onClick = {
                if (email.isBlank() || password.isBlank()) { error = "Please fill all fields"; return@GlowButton }
                // Basic domain check placeholder: if restricting, check email suffix
                // if (!email.endsWith("@school.edu")) { error="Use school email"; return@GlowButton }
                loading = true; error = null
                // TODO: call ApiService.login via ViewModel + handle JWT storage
                // For now simulate success
                onLogin()
            }, enabled = !loading)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = { /* TODO reset password */ }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Forgot password?", color = GreyText, fontSize = 12.sp) }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("No account? ", color = GreyText, fontSize = 13.sp)
            TextButton(onClick = onSignup) { Text("Sign up", color = White, fontWeight = FontWeight.SemiBold) }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun SignupScreen(onDone: () -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("8") }
    var section by remember { mutableStateOf("A") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(BlackBg).verticalScroll(rememberScrollState()).padding(24.dp)) {
        TextButton(onClick = onBack) { Text("← Back", color = GreyText) }
        Spacer(Modifier.height(8.dp))
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        Text("Join your school community", color = GreyText, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("School Email") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password (min 8)") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = className, onValueChange = { className = it }, label = { Text("Class") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
                OutlinedTextField(value = section, onValueChange = { section = it }, label = { Text("Section") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = White, unfocusedBorderColor = GreyBorder))
            }
            if (error != null) { Spacer(Modifier.height(8.dp)); Text(error!!, color = ErrorRed, fontSize = 12.sp) }
            Spacer(Modifier.height(16.dp))
            GlowButton(text = "Create Account", onClick = {
                if (name.isBlank() || email.isBlank() || password.length < 8) { error = "Check all fields (password ≥8)"; return@GlowButton }
                // TODO: ApiService.register + store token
                onDone()
            })
            Spacer(Modifier.height(8.dp))
            Text("By signing up you agree to school community guidelines.", color = GreyText, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
