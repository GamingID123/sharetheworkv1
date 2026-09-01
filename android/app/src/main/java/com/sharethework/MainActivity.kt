package com.sharethework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sharethework.navigation.AppNavGraph
import com.sharethework.navigation.Routes
import com.sharethework.ui.theme.BlackBg
import com.sharethework.ui.theme.GreyBorder
import com.sharethework.ui.theme.ShareTheWorkTheme

data class BottomItem(val route: String, val label: String, val icon: ImageVector)

val bottomItems = listOf(
    BottomItem(Routes.HOME, "Home", Icons.Filled.Home),
    BottomItem(Routes.HOMEWORK, "Homework", Icons.Filled.MenuBook),
    BottomItem(Routes.CHAT, "Chat", Icons.Filled.ChatBubble),
    BottomItem(Routes.AI, "Nova", Icons.Filled.AutoAwesome),
    BottomItem(Routes.PROFILE, "Profile", Icons.Filled.Person)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShareTheWorkTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val showBottom = currentRoute in bottomItems.map { it.route }

                Scaffold(
                    containerColor = BlackBg,
                    bottomBar = {
                        if (showBottom) {
                            NavigationBar(containerColor = BlackBg, tonalElevation = 0.dp) {
                                bottomItems.forEach { item ->
                                    val selected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                popUpTo(Routes.HOME) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = { Icon(item.icon, contentDescription = item.label) },
                                        label = { Text(item.label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = BlackBg,
                                            selectedTextColor = androidx.compose.ui.graphics.Color.White,
                                            indicatorColor = androidx.compose.ui.graphics.Color.White,
                                            unselectedIconColor = androidx.compose.ui.graphics.Color.Gray,
                                            unselectedTextColor = androidx.compose.ui.graphics.Color.Gray
                                        )
                                    )
                                }
                            }
                            Divider(color = GreyBorder, thickness = 0.5.dp)
                        }
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding).background(BlackBg).fillMaxSize()) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
