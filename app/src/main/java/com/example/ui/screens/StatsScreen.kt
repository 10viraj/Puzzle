package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LevelRepository
import com.example.model.ThemePalette
import com.example.viewmodel.GameUiState

@Composable
fun StatsScreen(
    state: GameUiState,
    totalPuzzlesSolved: Int,
    totalMoves: Int,
    timeAttackHighScore: Int,
    endlessHighStreak: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    val scrollState = rememberScrollState()

    val totalPossibleStars = LevelRepository.TOTAL_CLASSIC_LEVELS * 3
    val starProgress = (state.totalStars.toFloat() / totalPossibleStars).coerceIn(0f, 1f)

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
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(theme.pillBackground)
                        .border(1.dp, theme.boardBorder, CircleShape)
                        .testTag("stats_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.textPrimary
                    )
                }

                Text(
                    text = "Player Statistics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )

                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stars Progress Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = theme.tileBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFE6A700),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Classic Stars",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = theme.textPrimary
                            )
                        }

                        Text(
                            text = "${state.totalStars} / $totalPossibleStars",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFE6A700)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { starProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFFE6A700),
                        trackColor = theme.pillBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metric Tiles Grid (2 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Solved",
                    value = "$totalPuzzlesSolved",
                    subtitle = "Total puzzles",
                    icon = Icons.Default.CheckCircle,
                    accentColor = theme.primaryArrowColor,
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Moves",
                    value = "$totalMoves",
                    subtitle = "Arrow shots",
                    icon = Icons.Default.TouchApp,
                    accentColor = theme.secondaryArrowColor,
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Daily Streak",
                    value = "${state.dailyStreak} Days",
                    subtitle = if (state.isDailyCompletedToday) "Done today" else "Pending today",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = Color(0xFFD97706),
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Classic Lv",
                    value = "${state.maxUnlockedLevel - 1}",
                    subtitle = "Levels cleared",
                    icon = Icons.Default.GridOn,
                    accentColor = theme.primaryArrowColor,
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Time Attack",
                    value = "$timeAttackHighScore",
                    subtitle = "High score pts",
                    icon = Icons.Default.Timer,
                    accentColor = Color(0xFFBA1A1A),
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Endless Streak",
                    value = "$endlessHighStreak",
                    subtitle = "Longest run",
                    icon = Icons.Default.Bolt,
                    accentColor = Color(0xFF6750A4),
                    theme = theme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    theme: ThemePalette,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = theme.tileBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = theme.textSecondary
            )
        }
    }
}
