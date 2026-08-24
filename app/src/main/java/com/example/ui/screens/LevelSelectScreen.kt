package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LevelRepository
import com.example.model.Difficulty
import com.example.model.LevelScore
import com.example.model.ThemePalette
import com.example.viewmodel.GameUiState

@Composable
fun LevelSelectScreen(
    state: GameUiState,
    getLevelScore: (Int) -> LevelScore,
    onLevelSelected: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }

    val levelRange = when (selectedDifficulty) {
        Difficulty.EASY -> 1..15
        Difficulty.MEDIUM -> 16..35
        Difficulty.HARD -> 36..50
        Difficulty.EXPERT -> 51..60
    }

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
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
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
                        .testTag("level_select_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.textPrimary
                    )
                }

                Text(
                    text = "Select Level",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )

                // Total stars badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(theme.pillBackground)
                        .border(1.dp, theme.boardBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFE6A700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${state.totalStars}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.textPrimary
                    )
                }
            }

            // Difficulty Tabs
            val difficulties = Difficulty.values()
            ScrollableTabRow(
                selectedTabIndex = difficulties.indexOf(selectedDifficulty),
                containerColor = Color.Transparent,
                contentColor = theme.primaryArrowColor,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[difficulties.indexOf(selectedDifficulty)]),
                        color = theme.primaryArrowColor,
                        height = 3.dp
                    )
                },
                divider = {}
            ) {
                difficulties.forEach { diff ->
                    val isSelected = selectedDifficulty == diff
                    Tab(
                        selected = isSelected,
                        onClick = { selectedDifficulty = diff },
                        text = {
                            Text(
                                text = "${diff.title} (${diff.gridSize}×${diff.gridSize})",
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) theme.primaryArrowColor else theme.textSecondary.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Difficulty Description Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedDifficulty.description,
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }

            // Levels Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(levelRange.count()) { index ->
                    val levelNum = levelRange.first + index
                    val isUnlocked = levelNum <= state.maxUnlockedLevel
                    val isCurrent = levelNum == state.maxUnlockedLevel
                    val score = getLevelScore(levelNum)

                    LevelCard(
                        levelNumber = levelNum,
                        isUnlocked = isUnlocked,
                        isCurrent = isCurrent,
                        stars = score.stars,
                        bestMoves = score.bestMoves,
                        theme = theme,
                        onClick = {
                            if (isUnlocked) onLevelSelected(levelNum)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    levelNumber: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    stars: Int,
    bestMoves: Int,
    theme: ThemePalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(0.95f)
            .testTag("level_card_$levelNumber")
            .alpha(if (isUnlocked) 1f else 0.4f)
            .shadow(
                elevation = if (isCurrent) 6.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = if (isCurrent) theme.primaryArrowColor.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (isCurrent) theme.pillHighlight else theme.tileBackground
            )
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) theme.primaryArrowColor else theme.boardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true, color = theme.primaryArrowColor),
                enabled = isUnlocked,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = theme.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text(
                    text = "$levelNumber",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCurrent) theme.primaryArrowColor else theme.textPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        val earned = i <= stars
                        Icon(
                            imageVector = if (earned) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (earned) Color(0xFFE6A700) else theme.textSecondary.copy(alpha = 0.25f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                if (bestMoves > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$bestMoves moves",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = theme.textSecondary
                    )
                }
            }
        }
    }
}
