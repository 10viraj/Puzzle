package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.ui.components.GameBoard
import com.example.ui.components.GameControls
import com.example.ui.components.GameHeader
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.RewardHintDialog
import com.example.viewmodel.GameUiState

@Composable
fun EndlessScreen(
    state: GameUiState,
    onDifficultySelect: (Difficulty) -> Unit,
    onTileClick: (String) -> Unit,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit,
    onNextPuzzle: () -> Unit,
    onReplay: () -> Unit,
    onClaimHints: () -> Unit,
    onDismissReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    val puzzle = state.currentPuzzle ?: return

    val remainingTiles = state.tiles.count { !it.isExiting }
    val totalTiles = state.tiles.size

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(theme.pillBackground)
                            .border(1.dp, theme.boardBorder, CircleShape)
                            .testTag("endless_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary
                        )
                    }

                    // Streak Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.pillHighlight)
                            .border(1.dp, theme.primaryArrowColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = theme.primaryArrowColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Streak: ${state.endlessStreak}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryArrowColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Difficulty Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Difficulty.values().forEach { diff ->
                        val isSelected = state.endlessDifficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) theme.primaryArrowColor else theme.pillBackground
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) theme.primaryArrowColor else theme.boardBorder,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = true, color = Color.White),
                                    onClick = { onDifficultySelect(diff) }
                                )
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${diff.gridSize}×${diff.gridSize}",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = if (isSelected) Color.White else theme.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                GameHeader(
                    title = "Endless #${state.endlessStreak + 1}",
                    moves = state.movesCount,
                    parMoves = puzzle.parMoves,
                    timeSeconds = state.timeElapsedSeconds,
                    remainingTiles = remainingTiles,
                    totalTiles = totalTiles,
                    comboCount = state.comboCount,
                    theme = theme
                )
            }

            // Centered Board
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GameBoard(
                    gridSize = puzzle.gridSize,
                    tiles = state.tiles,
                    theme = theme,
                    onTileClicked = onTileClick
                )
            }

            // Bottom Controls
            GameControls(
                canUndo = state.canUndo,
                hintsRemaining = state.hintsRemaining,
                theme = theme,
                onUndoClick = onUndoClick,
                onResetClick = onResetClick,
                onHintClick = onHintClick,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        // Level Complete Dialog
        if (state.isLevelCompleted) {
            LevelCompleteDialog(
                gameMode = GameMode.ENDLESS,
                levelNumber = state.endlessStreak,
                starsEarned = 3,
                moves = state.movesCount,
                parMoves = puzzle.parMoves,
                timeSeconds = state.timeElapsedSeconds,
                theme = theme,
                onNextLevel = onNextPuzzle,
                onReplay = onReplay,
                onLevelSelect = onBackClick
            )
        }

        if (state.showRewardHintDialog) {
            RewardHintDialog(
                theme = theme,
                onClaimFreeHints = onClaimHints,
                onDismiss = onDismissReward
            )
        }
    }
}
