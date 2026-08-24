package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import java.util.Random

private data class Particle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val rotationSpeed: Float,
    val initialRotation: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ConfettiView(
    modifier: Modifier = Modifier,
    particleCount: Int = 75
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val random = Random()
        val colors = listOf(
            Color(0xFF00E5FF),
            Color(0xFFFF2A6D),
            Color(0xFFFFB800),
            Color(0xFF05D5FA),
            Color(0xFF38E1A1),
            Color(0xFFFF7F50),
            Color(0xFF7C4DFF),
            Color(0xFFFFFFFF)
        )
        List(particleCount) {
            Particle(
                x = random.nextFloat(),
                y = -0.1f - (random.nextFloat() * 0.3f),
                speedX = (random.nextFloat() - 0.5f) * 0.4f,
                speedY = 0.6f + (random.nextFloat() * 0.8f),
                rotationSpeed = (random.nextFloat() - 0.5f) * 720f,
                initialRotation = random.nextFloat() * 360f,
                size = 14f + random.nextFloat() * 18f,
                color = colors[random.nextInt(colors.size)]
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val t = progress.value

        particles.forEach { p ->
            val curX = (p.x + p.speedX * t) * width
            val curY = (p.y + p.speedY * t) * height
            val alpha = (1f - (t * 0.9f)).coerceIn(0f, 1f)
            val rot = p.initialRotation + p.rotationSpeed * t

            if (curY in -50f..(height + 50f)) {
                rotate(rot, pivot = Offset(curX, curY)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(curX - p.size / 2, curY - p.size / 2),
                        size = Size(p.size, p.size * 0.55f)
                    )
                }
            }
        }
    }
}
