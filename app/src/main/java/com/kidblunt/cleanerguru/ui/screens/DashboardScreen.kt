@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
package com.kidblunt.cleanerguru.ui.screens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.kidblunt.cleanerguru.R

import android.app.ActivityManager
import android.content.Context
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidblunt.cleanerguru.ui.theme.*
import com.kidblunt.cleanerguru.ui.components.TwinklingStarsBackground
import com.kidblunt.cleanerguru.ui.components.HeartRateMonitor
import com.kidblunt.cleanerguru.manager.GamingModeManager
import com.kidblunt.cleanerguru.manager.BatterySaverManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    onNavigateToBatterySaver: () -> Unit,
    onNavigateToGamingMode: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Optimization states (NO persistence)
    var cpuOptimizationActive by remember { mutableStateOf(false) }
    var powerSaverActive by remember { mutableStateOf(false) }
    var gamingModeActive by remember { mutableStateOf(false) }

    // Interaction tracking
    var lastInteractionTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    // Prevent snackbar spam
    var inactivityHandled by remember { mutableStateOf(false) }

    fun registerInteraction() {
        lastInteractionTime = System.currentTimeMillis()
        inactivityHandled = false
    }

    fun turnOffAllOptimizations() {
        cpuOptimizationActive = false
        powerSaverActive = false
        gamingModeActive = false
    }

    // Auto-off after 1 hour inactivity
    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000) // check every minute
            val inactiveMinutes =
                (System.currentTimeMillis() - lastInteractionTime) / (1000 * 60)

            if (inactiveMinutes >= 60 && !inactivityHandled) {
                turnOffAllOptimizations()
                inactivityHandled = true

                snackbarHostState.showSnackbar(
                    message = "Optimizations turned off after 1 hour of inactivity"
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            /* ================= Device Score ================= */

            Text(
                text = stringResource(id = R.string.device_score),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(id = R.string.device_score_info),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ================= Quick Optimize ================= */

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // ONE-CLICK ACTION (no toggle)
                    cpuOptimizationActive = true
                    powerSaverActive = true
                    registerInteraction()
                }
            ) {
                Text(text = stringResource(id = R.string.quick_optimize))
            }

            Text(
                text = stringResource(id = R.string.quick_optimize_desc),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            /* ================= Storage Cleanup ================= */

            DashboardItem(
                title = stringResource(id = R.string.storage_cleanup),
                subtitle = stringResource(id = R.string.storage_cleanup_privacy),
                onClick = {
                    registerInteraction()
                    // navigate to cleanup
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ================= Battery Saver ================= */

            DashboardItem(
                title = stringResource(id = R.string.battery_saver),
                subtitle = if (powerSaverActive)
                    stringResource(id = R.string.battery_saver_regular_desc)
                else
                    stringResource(id = R.string.battery_optimization),
                onClick = {
                    registerInteraction()
                    onNavigateToBatterySaver()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            /* ================= Gaming Mode ================= */

            DashboardItem(
                title = stringResource(id = R.string.gaming_mode),
                subtitle = stringResource(id = R.string.gaming_mode_desc),
                onClick = {
                    registerInteraction()
                    onNavigateToGamingMode()
                }
            )
        }
    }
}
