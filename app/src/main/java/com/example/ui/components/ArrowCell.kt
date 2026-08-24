package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.ArrowColorType
import com.example.model.ArrowDirection
import com.example.model.ArrowTile
import com.example.model.ThemePalette
import kotlin.math.roundToInt

@Composable
fun ArrowCell(
    tile: ArrowTile,
    theme: ThemePalette,
    cellSizePx: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Exit Slide Animation
    val exitOffset = remember { Animatable(0f) }
    val exitAlpha = remember { Animatable(1f) }
    val exitScale = remember { Animatable(1f) }

    // Blocked Shake Animation
    val shakeOffset = remember { Animatable(0f) }

    // Infinite breathing hint animation
    val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
    val hintScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_scale"
    )

    // Trigger exit animation
    LaunchedEffect(tile.isExiting) {
        if (tile.isExiting) {
            val travelDistance = cellSizePx * 4f
            kotlinx.coroutines.launch {
                exitOffset.animateTo(
                    targetValue = travelDistance,
                    animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                )
            }
            kotlinx.coroutines.launch {
                exitScale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = tween(durationMillis = 150)
                )
                exitScale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = tween(durationMillis = 110)
                )
            }
            kotlinx.coroutines.launch {
                delaySafe(120)
                exitAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 140)
                )
            }
        } else {
            exitOffset.snapTo(0f)
            exitAlpha.snapTo(1f)
            exitScale.snapTo(1f)
        }
    }

    // Trigger blocked bump animation
    LaunchedEffect(tile.isBlocked) {
        if (tile.isBlocked) {
            val bumpDist = 18f
            shakeOffset.animateTo(bumpDist, animationSpec = tween(60))
            shakeOffset.animateTo(-bumpDist * 0.6f, animationSpec = tween(60))
            shakeOffset.animateTo(bumpDist * 0.3f, animationSpec = tween(60))
            shakeOffset.animateTo(0f, animationSpec = spring(dampingRatio = 0.6f))
        }
    }

    if (tile.isExiting && exitAlpha.value <= 0.01f) {
        // Completely disappeared
        return
    }

    val tileColor = when (tile.colorType) {
        ArrowColorType.CYAN -> theme.primaryArrowColor
        ArrowColorType.CORAL -> theme.secondaryArrowColor
        ArrowColorType.AMBER -> theme.amberArrowColor
        ArrowColorType.EMERALD -> theme.accentArrowColor
        ArrowColorType.PURPLE -> if (theme.isLight) Color(0xFF6B4FA0) else Color(0xFFB388FF)
        ArrowColorType.INDIGO -> if (theme.isLight) Color(0xFF2C5282) else Color(0xFF448AFF)
    }

    // Calculate translation offset based on direction and animations
    val totalOffset = exitOffset.value + shakeOffset.value
    val offsetX = when (tile.direction) {
        ArrowDirection.RIGHT -> totalOffset
        ArrowDirection.LEFT -> -totalOffset
        else -> 0f
    }
    val offsetY = when (tile.direction) {
        ArrowDirection.DOWN -> totalOffset
        ArrowDirection.UP -> -totalOffset
        else -> 0f
    }

    val currentScale = if (tile.isHinted) hintScale else exitScale.value

    Box(
        modifier = modifier
            .testTag("arrow_tile_${tile.row}_${tile.col}")
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .scale(currentScale)
            .alpha(exitAlpha.value)
            .padding(3.dp)
            .shadow(
                elevation = if (tile.isHinted) 10.dp else if (theme.isLight) 4.dp else 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (tile.isHinted) theme.hintColor else Color.Black.copy(alpha = if (theme.isLight) 0.15f else 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(theme.tileBackground)
            .then(
                if (tile.isHinted) {
                    Modifier.border(
                        width = 2.5.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                theme.hintColor,
                                Color(0xFFFFD700),
                                theme.hintColor
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else if (tile.isBlocked) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFFBA1A1A),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = if (theme.isLight) theme.boardBorder.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = tileColor),
                enabled = !tile.isExiting,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Arrow Graphic
        ArrowGraphic(
            direction = tile.direction,
            accentColor = tileColor,
            modifier = Modifier
                .fillMaxSize(0.72f)
        )
    }
}

private suspend fun delaySafe(ms: Long) {
    kotlinx.coroutines.delay(ms)
}

@Composable
fun ArrowGraphic(
    direction: ArrowDirection,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.rotate(direction.angleDegrees),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Modern Sharp Arrow Path pointing to the RIGHT (at 0 degrees)
            val arrowPath = Path().apply {
                // Tip
                moveTo(w * 0.88f, h * 0.5f)
                // Top wing
                lineTo(w * 0.44f, h * 0.15f)
                // Top inner neck
                lineTo(w * 0.44f, h * 0.33f)
                // Tail top
                lineTo(w * 0.12f, h * 0.33f)
                // Tail bottom
                lineTo(w * 0.12f, h * 0.67f)
                // Bottom inner neck
                lineTo(w * 0.44f, h * 0.67f)
                // Bottom wing
                lineTo(w * 0.44f, h * 0.85f)
                close()
            }

            // Draw arrow fill
            drawPath(
                path = arrowPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor,
                        accentColor.copy(alpha = 0.88f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Draw crisp arrow outline
            drawPath(
                path = arrowPath,
                color = accentColor,
                style = Stroke(
                    width = 2.5f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Center highlight speed streak line
            drawLine(
                color = Color.White.copy(alpha = 0.5f),
                start = Offset(w * 0.22f, h * 0.5f),
                end = Offset(w * 0.60f, h * 0.5f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )
        }
    }
}

