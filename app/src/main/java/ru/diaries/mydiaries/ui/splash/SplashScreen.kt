package ru.diaries.mydiaries.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.diaries.mydiaries.ui.theme.DustyRose
import ru.diaries.mydiaries.ui.theme.GoldenHoney
import ru.diaries.mydiaries.ui.theme.LavenderMist
import ru.diaries.mydiaries.ui.theme.Parchment
import ru.diaries.mydiaries.ui.theme.SageGreen
import ru.diaries.mydiaries.ui.theme.SoftCream
import ru.diaries.mydiaries.ui.theme.Terracotta
import ru.diaries.mydiaries.ui.theme.TextPrimary
import ru.diaries.mydiaries.ui.theme.TextSecondary
import ru.diaries.mydiaries.ui.theme.WarmBrown
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Animation states
    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    // Infinite animations for decorative elements
    val infiniteTransition = rememberInfiniteTransition(label = "splash_infinite")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_rotation"
    )

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    // Launch animations sequence
    LaunchedEffect(Unit) {
        // Logo scale and fade in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )

        delay(200)

        // Title fade in
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )

        delay(150)

        // Subtitle fade in
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400)
        )

        // Wait and finish
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Parchment,
                        SoftCream,
                        Parchment
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated background particles
        FloatingParticles(rotation = particleRotation)

        // Decorative rings
        DecorativeRings(pulseScale = pulseScale)

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = floatOffset.dp)
        ) {
            // Logo with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value * pulseScale)
                    .alpha(logoAlpha.value)
            ) {
                // Glow background
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    WarmBrown.copy(alpha = 0.3f),
                                    WarmBrown.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main icon background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    WarmBrown,
                                    Terracotta
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = SoftCream
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App title
            Text(
                text = "My Diaries",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Твой личный дневник",
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = TextSecondary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

@Composable
private fun FloatingParticles(rotation: Float) {
    val particles = remember {
        List(12) {
            Particle(
                color = listOf(
                    DustyRose.copy(alpha = 0.4f),
                    LavenderMist.copy(alpha = 0.3f),
                    GoldenHoney.copy(alpha = 0.35f),
                    SageGreen.copy(alpha = 0.3f)
                ).random(),
                radius = Random.nextFloat() * 20f + 10f,
                distance = Random.nextFloat() * 150f + 100f,
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 0.5f + 0.5f
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        particles.forEach { particle ->
            val angle = Math.toRadians((particle.angle + rotation * particle.speed).toDouble())
            val x = centerX + (particle.distance * cos(angle)).toFloat()
            val y = centerY + (particle.distance * sin(angle)).toFloat()

            drawCircle(
                color = particle.color,
                radius = particle.radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun DecorativeRings(pulseScale: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Outer ring
        drawCircle(
            color = WarmBrown.copy(alpha = 0.08f),
            radius = 200f * pulseScale,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )

        // Middle ring
        drawCircle(
            color = Terracotta.copy(alpha = 0.1f),
            radius = 160f * pulseScale,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
        )

        // Inner ring
        drawCircle(
            color = SageGreen.copy(alpha = 0.12f),
            radius = 120f * pulseScale,
            center = Offset(centerX, centerY),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )
    }
}

private data class Particle(
    val color: Color,
    val radius: Float,
    val distance: Float,
    val angle: Float,
    val speed: Float
)
