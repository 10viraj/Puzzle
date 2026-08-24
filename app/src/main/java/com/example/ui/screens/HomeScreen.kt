package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowDirection
import com.example.model.ThemePalette
import com.example.ui.components.ArrowGraphic
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameUiState

@Composable
fun HomeScreen(
    state: GameUiState,
    onNavigate: (AppScreen) -> Unit,
    onContinuePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    val scrollState = rememberScrollState()

    // Floating logo arrow animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(theme.backgroundStart, theme.backgroundEnd)
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Stars count & Settings / Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(theme.pillBackground)
                        .border(1.dp, theme.boardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stars",
                        tint = Color(0xFFE6A700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${state.totalStars} Stars",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onNavigate(AppScreen.STATISTICS) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(theme.pillBackground)
                            .border(1.dp, theme.boardBorder, CircleShape)
                            .testTag("home_stats_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Statistics",
                            tint = theme.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.SETTINGS) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(theme.pillBackground)
                            .border(1.dp, theme.boardBorder, CircleShape)
                            .testTag("home_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = theme.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Brand Logo Box
            Box(
                modifier = Modifier
                    .size(116.dp)
                    .scale(pulseScale)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(36.dp),
                        spotColor = theme.primaryArrowColor.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(36.dp))
                    .background(theme.primaryArrowColor)
                    .border(2.dp, theme.exitGlowColor.copy(alpha = 0.5f), RoundedCornerShape(36.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Quad Cross Arrows icon
                Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    ArrowGraphic(
                        direction = ArrowDirection.UP,
                        accentColor = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopCenter)
                    )
                    ArrowGraphic(
                        direction = ArrowDirection.RIGHT,
                        accentColor = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterEnd)
                    )
                    ArrowGraphic(
                        direction = ArrowDirection.DOWN,
                        accentColor = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomCenter)
                    )
                    ArrowGraphic(
                        direction = ArrowDirection.LEFT,
                        accentColor = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ARROW PUZZLE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary,
                letterSpacing = 1.5.sp
            )

            Text(
                text = "Untangle the arrows to clear the board",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary "Continue / Play" Button
            Button(
                onClick = onContinuePlay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.primaryArrowColor
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(22.dp),
                        spotColor = theme.primaryArrowColor.copy(alpha = 0.35f)
                    )
                    .testTag("home_play_continue_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play Level ${state.maxUnlockedLevel}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Game Modes Grid Title
            Text(
                text = "GAME MODES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = theme.textSecondary.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            // Mode 1: Classic Levels
            ModeCard(
                title = "Classic Levels",
                subtitle = "60 handcrafted & procedural puzzle stages",
                badge = "${state.maxUnlockedLevel - 1}/60 Cleared",
                icon = Icons.Default.GridOn,
                accentColor = theme.primaryArrowColor,
                theme = theme,
                onClick = { onNavigate(AppScreen.LEVEL_SELECT) },
                testTag = "mode_classic_card"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 2: Daily Challenge
            ModeCard(
                title = "Daily Challenge",
                subtitle = "Fresh daily board • ${state.dailyStreak} Day Streak",
                badge = if (state.isDailyCompletedToday) "Completed" else "New",
                badgeColor = if (state.isDailyCompletedToday) theme.primaryArrowColor else Color(0xFFE6A700),
                icon = Icons.Default.CalendarMonth,
                accentColor = theme.secondaryArrowColor,
                theme = theme,
                onClick = { onNavigate(AppScreen.DAILY_CHALLENGE) },
                testTag = "mode_daily_card"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 3: Time Attack
            ModeCard(
                title = "Time Attack",
                subtitle = "Fast clears under 60s countdown • Combos",
                badge = "Best: ${state.timeAttackScore}",
                icon = Icons.Default.Timer,
                accentColor = if (theme.isLight) Color(0xFF9E472A) else Color(0xFFFF2A6D),
                theme = theme,
                onClick = { onNavigate(AppScreen.TIME_ATTACK) },
                testTag = "mode_time_attack_card"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mode 4: Endless Mode
            ModeCard(
                title = "Endless Mode",
                subtitle = "Infinite generated grids of any difficulty",
                badge = "${state.endlessDifficulty.title} (4×4 to 8×8)",
                icon = Icons.Default.Bolt,
                accentColor = if (theme.isLight) Color(0xFF2C4A6F) else Color(0xFFB388FF),
                theme = theme,
                onClick = { onNavigate(AppScreen.ENDLESS) },
                testTag = "mode_endless_card"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ModeCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    theme: ThemePalette,
    badgeColor: Color = accentColor,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = accentColor),
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = theme.tileBackground
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(theme.pillBackground)
                    .border(1.dp, theme.boardBorder, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.pillHighlight)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryArrowColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }
        }
    }
}

