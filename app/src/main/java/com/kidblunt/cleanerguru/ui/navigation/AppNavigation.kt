package com.kidblunt.cleanerguru.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kidblunt.cleanerguru.ui.screens.BatterySaverScreen
import com.kidblunt.cleanerguru.ui.screens.DashboardScreen
import com.kidblunt.cleanerguru.ui.screens.GamingModeScreen
import com.kidblunt.cleanerguru.ui.screens.PhotoCleanupScreen
import com.kidblunt.cleanerguru.ui.screens.SettingsScreen
import com.kidblunt.cleanerguru.ui.viewmodel.AuthViewModel
import com.kidblunt.cleanerguru.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object PhotoCleanup : Screen("photo_cleanup")
    object BatterySaver : Screen("battery_saver")
    object GamingMode : Screen("gaming_mode")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ✅ INLINE SPLASH
        composable(Screen.Splash.route) {
            AppSplashScreen(
                onFinished = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToPhotoCleanup = {
                    navController.navigate(Screen.PhotoCleanup.route)
                },
                onNavigateToBatterySaver = {
                    navController.navigate(Screen.BatterySaver.route)
                },
                onNavigateToGamingMode = {
                    navController.navigate(Screen.GamingMode.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.PhotoCleanup.route) {
            PhotoCleanupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.BatterySaver.route) {
            BatterySaverScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.GamingMode.route) {
            GamingModeScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/* =========================
   INLINE SPLASH COMPOSABLE
   ========================= */

@Composable
private fun AppSplashScreen(
    onFinished: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(600) // short, compliant splash
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Device Tuner Pro 26",
            color = Color.White,
            fontSize = 22.sp
        )
    }
}
