package com.kidblunt.cleanerguru.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kidblunt.cleanerguru.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedSplashScreen(
    onAnimationComplete: @Composable () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(3000) // Show splash for 3 seconds
        showContent = true
    }
    
    if (showContent) {
        onAnimationComplete()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DarkBackground,
                            VibrantPurple.copy(alpha = 0.3f),
                            DarkBackground
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 3D Wheel Spanner Animation
                WheelSpannerAnimation3D()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Device Tuner Pro 26",
                    style = MaterialTheme.typography.h2,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 24.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Monitoring Device usage",
                    style = MaterialTheme.typography.body1,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun WheelSpannerAnimation3D() {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Wheel rotation
    val wheelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // Spanner rotation (loosening motion)
    val spannerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // 3D depth animation
    val depthScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Bolt loosening animation with 3D effect
    val boltOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Bolt rotation while falling
    val boltRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1080f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // 3D perspective tilt
    val perspectiveTilt by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier.size(250.dp)
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val wheelRadius = size.width * 0.3f
        
        // Apply 3D transformations
        scale(depthScale, center) {
            rotate(perspectiveTilt, center) {
                // Draw 3D tire/wheel with depth
                drawTire3D(center, wheelRadius, wheelRotation, depthScale)
                
                // Draw 3D spanner with metallic effect
                drawSpanner3D(center, spannerRotation, depthScale)
                
                // Draw loosening bolts with 3D depth
                drawBolts3D(center, boltOffset, boltRotation, depthScale)
                
                // Add sparks effect
                drawSparks(center, spannerRotation)
            }
        }
    }
}

private fun DrawScope.drawTire3D(center: Offset, radius: Float, rotation: Float, depth: Float) {
    rotate(rotation, center) {
        // Shadow/depth layer
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = radius * 1.1f,
            center = Offset(center.x + 5f, center.y + 5f)
        )
        
        // Outer tire with gradient for 3D effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Gray,
                    Color.Black,
                    Color.DarkGray
                ),
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
                radius = radius
            ),
            radius = radius,
            center = center
        )
        
        // Inner rim with metallic gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.LightGray,
                    Color.Gray,
                    Color.DarkGray
                ),
                center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
                radius = radius * 0.7f
            ),
            radius = radius * 0.7f,
            center = center
        )
        
        // 3D spokes with depth
        for (i in 0..5) {
            val angle = (i * 60f) * (kotlin.math.PI / 180f)
            val spokeStart = Offset(
                center.x + cos(angle).toFloat() * radius * 0.25f,
                center.y + sin(angle).toFloat() * radius * 0.25f
            )
            val spokeEnd = Offset(
                center.x + cos(angle).toFloat() * radius * 0.65f,
                center.y + sin(angle).toFloat() * radius * 0.65f
            )
            
            // Spoke shadow
            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(spokeStart.x + 2f, spokeStart.y + 2f),
                end = Offset(spokeEnd.x + 2f, spokeEnd.y + 2f),
                strokeWidth = 12f
            )
            
            // Main spoke with gradient
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(Color.LightGray, Color.DarkGray),
                    start = spokeStart,
                    end = spokeEnd
                ),
                start = spokeStart,
                end = spokeEnd,
                strokeWidth = 10f
            )
        }
        
        // Center hub with 3D effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.LightGray,
                    Color.Gray,
                    Color.Black
                ),
                center = Offset(center.x - radius * 0.1f, center.y - radius * 0.1f),
                radius = radius * 0.2f
            ),
            radius = radius * 0.2f,
            center = center
        )
    }
}

private fun DrawScope.drawSpanner3D(center: Offset, rotation: Float, depth: Float) {
    val spannerCenter = Offset(center.x + 100f, center.y - 100f)
    
    rotate(rotation, spannerCenter) {
        // Spanner shadow
        val shadowPath = Path().apply {
            moveTo(spannerCenter.x - 75f + 3f, spannerCenter.y - 12f + 3f)
            lineTo(spannerCenter.x + 75f + 3f, spannerCenter.y - 12f + 3f)
            lineTo(spannerCenter.x + 75f + 3f, spannerCenter.y + 12f + 3f)
            lineTo(spannerCenter.x - 75f + 3f, spannerCenter.y + 12f + 3f)
            close()
        }
        
        drawPath(
            path = shadowPath,
            color = Color.Black.copy(alpha = 0.3f)
        )
        
        // Spanner handle with metallic gradient
        val handlePath = Path().apply {
            moveTo(spannerCenter.x - 75f, spannerCenter.y - 12f)
            lineTo(spannerCenter.x + 75f, spannerCenter.y - 12f)
            lineTo(spannerCenter.x + 75f, spannerCenter.y + 12f)
            lineTo(spannerCenter.x - 75f, spannerCenter.y + 12f)
            close()
        }
        
        drawPath(
            path = handlePath,
            brush = Brush.linearGradient(
                colors = listOf(
                    NeonPink.copy(alpha = 0.8f),
                    NeonPink,
                    NeonPink.copy(alpha = 0.6f)
                ),
                start = Offset(spannerCenter.x, spannerCenter.y - 12f),
                end = Offset(spannerCenter.x, spannerCenter.y + 12f)
            )
        )
        
        // Spanner head shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = 25f,
            center = Offset(spannerCenter.x + 75f + 2f, spannerCenter.y + 2f)
        )
        
        // Spanner head with 3D gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    NeonPink.copy(alpha = 0.9f),
                    NeonPink,
                    NeonPink.copy(alpha = 0.7f)
                ),
                center = Offset(spannerCenter.x + 75f - 8f, spannerCenter.y - 8f),
                radius = 25f
            ),
            radius = 25f,
            center = Offset(spannerCenter.x + 75f, spannerCenter.y)
        )
        
        // Spanner opening with depth
        drawCircle(
            color = Color.Black.copy(alpha = 0.8f),
            radius = 15f,
            center = Offset(spannerCenter.x + 75f, spannerCenter.y)
        )
        
        drawCircle(
            color = Color.Black,
            radius = 12f,
            center = Offset(spannerCenter.x + 75f, spannerCenter.y)
        )
    }
}

private fun DrawScope.drawBolts3D(center: Offset, offset: Float, rotation: Float, depth: Float) {
    for (i in 0..2) {
        val boltAngle = i * 120f
        val boltPosition = Offset(
            center.x + offset * cos((boltAngle + 45f) * kotlin.math.PI / 180f).toFloat(),
            center.y + offset * sin((boltAngle + 45f) * kotlin.math.PI / 180f).toFloat() + offset
        )
        
        rotate(rotation + i * 120f, boltPosition) {
            // Bolt shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.4f),
                radius = 10f,
                center = Offset(boltPosition.x + 2f, boltPosition.y + 2f)
            )
            
            // Bolt head with 3D effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricBlue.copy(alpha = 0.9f),
                        ElectricBlue,
                        ElectricBlue.copy(alpha = 0.7f)
                    ),
                    center = Offset(boltPosition.x - 3f, boltPosition.y - 3f),
                    radius = 10f
                ),
                radius = 10f,
                center = boltPosition
            )
            
            // Bolt threads with depth
            for (j in 1..4) {
                val threadY = boltPosition.y + j * 15f
                
                // Thread shadow
                drawCircle(
                    color = Color.Black.copy(alpha = 0.3f),
                    radius = 8f - j,
                    center = Offset(boltPosition.x + 1f, threadY + 1f)
                )
                
                // Thread with gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.8f - j * 0.1f),
                            ElectricBlue.copy(alpha = 0.6f - j * 0.1f)
                        ),
                        radius = 8f - j
                    ),
                    radius = 8f - j,
                    center = Offset(boltPosition.x, threadY)
                )
            }
        }
    }
}

private fun DrawScope.drawSparks(center: Offset, spannerRotation: Float) {
    val sparkCenter = Offset(center.x + 100f, center.y - 100f)
    
    for (i in 0..8) {
        val sparkAngle = (i * 45f + spannerRotation * 2f) * (kotlin.math.PI / 180f)
        val sparkLength = 20f + (i % 3) * 10f
        val sparkStart = Offset(
            sparkCenter.x + cos(sparkAngle).toFloat() * 30f,
            sparkCenter.y + sin(sparkAngle).toFloat() * 30f
        )
        val sparkEnd = Offset(
            sparkCenter.x + cos(sparkAngle).toFloat() * (30f + sparkLength),
            sparkCenter.y + sin(sparkAngle).toFloat() * (30f + sparkLength)
        )
        
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    SunsetOrange,
                    SunsetOrange.copy(alpha = 0.7f),
                    Color.Transparent
                ),
                start = sparkStart,
                end = sparkEnd
            ),
            start = sparkStart,
            end = sparkEnd,
            strokeWidth = 3f
        )
    }
}