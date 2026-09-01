package com.sharethework.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sharethework.ui.screens.*

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val HOMEWORK = "homework"
    const val CLASSWORK = "classwork"
    const val CHAT = "chat"
    const val CHAT_DETAIL = "chat_detail/{id}"
    const val AI = "ai"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
    const val NOTIFICATIONS = "notifications"
    const val FILE_PREVIEW = "file_preview/{fileId}/{fileName}"
    fun filePreview(fileId: String, fileName: String): String {
        val encId = java.net.URLEncoder.encode(fileId, "UTF-8")
        val encName = java.net.URLEncoder.encode(fileName, "UTF-8")
        return "file_preview/$encId/$encName"
    }
}

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String = Routes.SPLASH) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.SPLASH) { SplashScreen(onFinished = { loggedIn -> navController.navigate(if (loggedIn) Routes.HOME else Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } }) }
        composable(Routes.LOGIN) { LoginScreen(onLogin = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN){inclusive=true} } }, onSignup = { navController.navigate(Routes.SIGNUP) }) }
        composable(Routes.SIGNUP) { SignupScreen(onDone = { navController.navigate(Routes.HOME){ popUpTo(Routes.SIGNUP){inclusive=true} } }, onBack = { navController.popBackStack() }) }
        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.HOMEWORK) { HomeworkScreen(navController) }
        composable(Routes.CLASSWORK) { ClassworkScreen(navController) }
        composable(Routes.CHAT) { ChatListScreen(navController) }
        composable(Routes.CHAT_DETAIL) { backStack -> val id = backStack.arguments?.getString("id") ?: ""; ChatDetailScreen(id, navController) }
        composable(Routes.AI) { NovaAiScreen(navController) }
        composable(Routes.PROFILE) { ProfileScreen(navController) }
        composable(Routes.ADMIN) { AdminScreen(navController) }
        composable(Routes.NOTIFICATIONS) { NotificationScreen(navController) }
        composable(Routes.FILE_PREVIEW) { backStack ->
            val rawId = backStack.arguments?.getString("fileId") ?: ""
            val rawName = backStack.arguments?.getString("fileName") ?: "file"
            val fileId = try { java.net.URLDecoder.decode(rawId, "UTF-8") } catch (_: Exception) { rawId }
            val fileName = try { java.net.URLDecoder.decode(rawName, "UTF-8") } catch (_: Exception) { rawName }
            FilePreviewScreen(fileId, fileName, navController)
        }
    }
}
