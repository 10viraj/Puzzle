package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Trophy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.GameBoard
import com.example.ui.components.GameControls
import com.example.ui.components.RewardHintDialog
import com.example.viewmodel.GameUiState

@Composable
fun TimeAttackScreen(
    state: GameUiState,
    onTileClick: (String) -> Unit,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit,
    onPlayAgain: () -> Unit,
    onClaimHints: () -> Unit,
    onDismissReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    val puzzle = state.currentPuzzle ?: return

    val progress by animateFloatAsState(
        targetValue = (state.timeAttackSecondsRemaining / 60f).coerceIn(0f, 1f),
        label = "time_progress"
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            .testTag("time_attack_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary
                        )
                    }

                    Text(
                        text = "Time Attack • Stage ${state.timeAttackStage}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary
                    )

                    // Score Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(theme.pillHighlight)
                            .border(1.dp, theme.primaryArrowColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${state.timeAttackScore} pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.primaryArrowColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown Timer Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (state.timeAttackSecondsRemaining <= 10) Color(0xFFBA1A1A) else theme.primaryArrowColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (state.timeAttackSecondsRemaining <= 10) Color(0xFFBA1A1A) else theme.primaryArrowColor,
                        trackColor = theme.pillBackground
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${state.timeAttackSecondsRemaining}s",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = if (state.timeAttackSecondsRemaining <= 10) Color(0xFFBA1A1A) else theme.textPrimary
                    )
                }
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

        // Time's Up / Game Over Dialog
        if (state.isTimeAttackOver) {
            Dialog(onDismissRequest = { /* Modal */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(theme.tileBackground)
                        .border(1.5.dp, theme.boardBorder, RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TIME'S UP!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFBA1A1A)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Final Score",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = theme.textSecondary
                        )

                        Text(
                            text = "${state.timeAttackScore}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = theme.textPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Stages Cleared: ${state.timeAttackStage - 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.primaryArrowColor
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onPlayAgain,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.primaryArrowColor
                            ),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = theme.primaryArrowColor.copy(alpha = 0.35f))
                                .testTag("time_attack_play_again_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Play Again",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = theme.pillBackground
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("time_attack_exit_button")
                        ) {
                            Text(
                                text = "Exit to Menu",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.textPrimary
                            )
                        }
                    }
                }
            }
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
