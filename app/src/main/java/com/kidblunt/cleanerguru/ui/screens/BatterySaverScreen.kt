@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
package com.kidblunt.cleanerguru.ui.screens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import com.kidblunt.cleanerguru.R

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidblunt.cleanerguru.ui.theme.*
import com.kidblunt.cleanerguru.data.manager.BatterySaverManager
import kotlinx.coroutines.delay

@Composable
fun BatterySaverScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // Initialize BatterySaverManager
    val batterySaverManager = remember { BatterySaverManager(context) }
    val batterySaverState by batterySaverManager.batterySaverState.collectAsState()

    val batteryLevel = remember { getBatteryLevel(context) }
    val batteryStatus = remember { getBatteryStatus(context) }
    val isCharging = remember { isCharging(context) }
    
    var estimatedTime by remember { mutableStateOf("") }
    var remainingTime by remember { mutableStateOf("") }

    // Check for timeout and update remaining time every second
    LaunchedEffect(Unit) {
        while (true) {
            batterySaverManager.checkBatterySaverTimeout()
            remainingTime = batterySaverManager.formatRemainingTime()
            delay(1000)
        }
    }

    // Calculate estimated battery time
    LaunchedEffect(batteryLevel, batterySaverState.isEnabled) {
        estimatedTime = calculateBatteryTime(batteryLevel, batterySaverState.isEnabled)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Battery Monitoring")
                        if (batterySaverState.isEnabled) {
                            Text(
                                text = remainingTime,
                                style = MaterialTheme.typography.caption,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                backgroundColor = VibrantPurple,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Open system battery settings
                            val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings, 
                            contentDescription = "Battery Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            EnhancedBatteryStatusCard(
                batteryLevel = batteryLevel,
                batteryStatus = batteryStatus,
                isCharging = isCharging,
                estimatedTime = estimatedTime,
                isOptimized = batterySaverState.isEnabled,
                remainingTime = remainingTime
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Monitoring Usage",
                    icon = Icons.Default.BatteryAlert,
                    color = WarningOrange,
                    isActive = batterySaverState.isEnabled,
                    onClick = { 
                        if (batterySaverState.isEnabled) {
                            batterySaverManager.disableBatterySaver()
                        } else {
                            batterySaverManager.enableBatterySaver()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Battery Settings",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            EnhancedSettingCard(
                title = "Battery Monitoring",
                description = "Monitor power usage and access power settings (3 hours)",
                icon = Icons.Default.BatteryChargingFull,
                isEnabled = batterySaverState.isEnabled,
                onToggle = { 
                    if (it) {
                        batterySaverManager.enableBatterySaver()
                    } else {
                        batterySaverManager.disableBatterySaver()
                    }
                },
                impact = "High Impact"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsActionCard(
                title = "System Battery Settings",
                description = "Open Android battery optimization settings",
                icon = Icons.Default.Settings,
                onClick = {
                    val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Battery Tips",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BatteryTipCard(
                tip = "Close unused apps running in background",
                icon = Icons.Default.Apps,
                savings = "May help reduce background activity"
            )

            Spacer(modifier = Modifier.height(8.dp))

            BatteryTipCard(
                tip = "Review location settings when not needed",
                icon = Icons.Default.LocationOff,
                savings = "Can help reduce unnecessary background activity"
            )

            Spacer(modifier = Modifier.height(8.dp))

            BatteryTipCard(
                tip = "Use dark mode on supported displays",
                icon = Icons.Default.DarkMode,
                savings = "May help reduce screen power usage"
            )

            Spacer(modifier = Modifier.height(8.dp))

            BatteryTipCard(
                tip = "Turn off unnecessary notifications",
                icon = Icons.Default.NotificationsOff,
                savings = "Can help minimize unnecessary activity"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Disclaimer
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = 4.dp,
                backgroundColor = CardBackground.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Important Note",
                        style = MaterialTheme.typography.h3,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Displayed values are estimates based on system-reported data. Battery tips require user interaction through system settings.",
                        style = MaterialTheme.typography.body2,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CloudBlue,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
fun EnhancedBatteryStatusCard(
    batteryLevel: Int,
    batteryStatus: String,
    isCharging: Boolean,
    estimatedTime: String,
    isOptimized: Boolean = false,
    remainingTime: String = ""
) {
    val batteryColor = when {
        batteryLevel < 20 -> ErrorRed
        batteryLevel < 50 -> WarningOrange
        else -> SuccessGreen
    }

    val animatedProgress by animateFloatAsState(
        targetValue = batteryLevel / 100f,
        animationSpec = tween(durationMillis = 1500)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 6.dp,
        backgroundColor = if (isOptimized) 
            batteryColor.copy(alpha = 0.2f) 
        else 
            batteryColor.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isOptimized) "Monitoring Usage Active" else "Battery Level",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$batteryLevel%",
                        style = MaterialTheme.typography.h1,
                        fontWeight = FontWeight.Bold,
                        color = batteryColor
                    )
                    if (isOptimized && remainingTime.isNotEmpty()) {
                        Text(
                            text = remainingTime,
                            style = MaterialTheme.typography.caption,
                            color = SuccessGreen,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 8.dp,
                        color = batteryColor
                    )
                    Icon(
                        imageVector = if (isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = batteryColor
                    )
                    
                    if (isOptimized) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Monitoring",
                            modifier = Modifier
                                .size(20.dp)
                                .offset(x = 25.dp, y = (-25).dp),
                            tint = SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = when {
                            isCharging -> "Charging"
                            isOptimized -> "Monitoring Usage"
                            else -> batteryStatus
                        },
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isCharging) "Time to Full" else "Estimated remaining time",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Based on system statistics",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                    Text(
                        text = estimatedTime,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = if (isActive) 8.dp else 4.dp,
        backgroundColor = if (isActive) color.copy(alpha = 0.2f) else MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isActive) color else color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) Color.White else color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Medium,
                color = if (isActive) color else MaterialTheme.colors.onSurface
            )

            if (isActive) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.caption,
                    color = color,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun EnhancedSettingCard(
    title: String,
    description: String,
    icon: ImageVector,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    impact: String
) {
    val impactColor = when (impact) {
        "High Impact" -> SuccessGreen
        "Medium Impact" -> WarningOrange
        "Low Impact" -> CloudBlue
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CloudBlue,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Card(
                        backgroundColor = impactColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = impact,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.caption,
                            color = impactColor,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Modern push button style toggle
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp)
                    .clickable { onToggle(!isEnabled) },
                shape = RoundedCornerShape(16.dp),
                backgroundColor = if (isEnabled) SuccessGreen else Color.Gray.copy(alpha = 0.3f),
                elevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        modifier = Modifier
                            .size(28.dp)
                            .padding(2.dp),
                        shape = CircleShape,
                        backgroundColor = Color.White,
                        elevation = 4.dp
                    ) {}
                }
            }
        }
    }
}

@Composable
fun BatteryTipCard(
    tip: String,
    icon: ImageVector,
    savings: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CloudBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip,
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = savings,
                    style = MaterialTheme.typography.caption,
                    color = SuccessGreen,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun getBatteryLevel(context: Context): Int {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}

private fun getBatteryStatus(context: Context): String {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
    return when (status) {
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        else -> "Unknown"
    }
}

private fun isCharging(context: Context): Boolean {
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
    return status == BatteryManager.BATTERY_STATUS_CHARGING
}

private fun calculateBatteryTime(batteryLevel: Int, isOptimized: Boolean): String {
    val baseHours = batteryLevel * 0.12 // Base calculation
    val optimizedHours = if (isOptimized) baseHours * 1.3 else baseHours
    
    val hours = optimizedHours.toInt()
    val minutes = ((optimizedHours - hours) * 60).toInt()
    
    return if (hours > 0) {
        "${hours}h ${minutes}m"
    } else {
        "${minutes}m"
    }
}