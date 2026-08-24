package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GameMode
import com.example.model.ThemePalette

@Composable
fun LevelCompleteDialog(
    gameMode: GameMode,
    levelNumber: Int,
    starsEarned: Int,
    moves: Int,
    parMoves: Int,
    timeSeconds: Int,
    theme: ThemePalette,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelSelect: () -> Unit
) {
    val star1Scale = remember { Animatable(0f) }
    val star2Scale = remember { Animatable(0f) }
    val star3Scale = remember { Animatable(0f) }

    LaunchedEffect(starsEarned) {
        if (starsEarned >= 1) {
            star1Scale.animateTo(
                1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        if (starsEarned >= 2) {
            star2Scale.animateTo(
                1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        if (starsEarned >= 3) {
            star3Scale.animateTo(
                1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
    }

    Dialog(
        onDismissRequest = { /* Modal */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Confetti Layer
            ConfettiView()

            // Dialog Card
            Box(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(theme.tileBackground)
                    .border(
                        width = 1.5.dp,
                        color = theme.boardBorder,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (gameMode) {
                            GameMode.CLASSIC -> "LEVEL $levelNumber COMPLETE!"
                            GameMode.DAILY_CHALLENGE -> "DAILY CHALLENGE SOLVED!"
                            GameMode.TIME_ATTACK -> "STAGE CLEARED!"
                            GameMode.ENDLESS -> "PUZZLE SOLVED!"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Stars Display
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Star 1
                        Icon(
                            imageVector = if (starsEarned >= 1) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (starsEarned >= 1) Color(0xFFE6A700) else theme.textSecondary.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(44.dp)
                                .scale(if (starsEarned >= 1) star1Scale.value else 1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Star 2 (Middle, slightly elevated/larger)
                        Icon(
                            imageVector = if (starsEarned >= 2) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (starsEarned >= 2) Color(0xFFE6A700) else theme.textSecondary.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(56.dp)
                                .scale(if (starsEarned >= 2) star2Scale.value else 1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Star 3
                        Icon(
                            imageVector = if (starsEarned >= 3) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (starsEarned >= 3) Color(0xFFE6A700) else theme.textSecondary.copy(alpha = 0.25f),
                            modifier = Modifier
                                .size(44.dp)
                                .scale(if (starsEarned >= 3) star3Scale.value else 1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats breakdown row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(theme.pillBackground)
                            .border(1.dp, theme.boardBorder, RoundedCornerShape(18.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "MOVES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = theme.textSecondary
                            )
                            Text(
                                text = "$moves",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.textPrimary
                            )
                            Text(
                                text = "Par $parMoves",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = theme.textSecondary.copy(alpha = 0.7f)
                            )
                        }

                        val mins = timeSeconds / 60
                        val secs = timeSeconds % 60
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "TIME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = theme.textSecondary
                            )
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.textPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Action Button (Next Level)
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = theme.primaryArrowColor
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = theme.primaryArrowColor.copy(alpha = 0.35f))
                            .testTag("dialog_next_level_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (gameMode == GameMode.CLASSIC) "Next Level" else "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary action buttons (Replay & Menu)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onReplay,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.pillBackground
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("dialog_replay_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = theme.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Replay",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary
                                )
                            }
                        }

                        Button(
                            onClick = onLevelSelect,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.pillBackground
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("dialog_menu_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = null,
                                    tint = theme.textPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Levels",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
