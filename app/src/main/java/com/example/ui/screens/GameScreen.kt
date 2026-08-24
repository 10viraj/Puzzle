package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.model.GameMode
import com.example.ui.components.GameBoard
import com.example.ui.components.GameControls
import com.example.ui.components.GameHeader
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.RewardHintDialog
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameUiState

@Composable
fun GameScreen(
    state: GameUiState,
    onTileClick: (String) -> Unit,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelSelect: () -> Unit,
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
            // Top Header
            GameHeader(
                title = puzzle.title,
                moves = state.movesCount,
                parMoves = puzzle.parMoves,
                timeSeconds = state.timeElapsedSeconds,
                remainingTiles = remainingTiles,
                totalTiles = totalTiles,
                comboCount = state.comboCount,
                theme = theme
            )

            // Centered Responsive Board
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

            // Bottom Controls Bar
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

        // Level Complete Dialog Overlay
        if (state.isLevelCompleted) {
            LevelCompleteDialog(
                gameMode = state.gameMode,
                levelNumber = puzzle.levelNumber,
                starsEarned = state.starsEarned,
                moves = state.movesCount,
                parMoves = puzzle.parMoves,
                timeSeconds = state.timeElapsedSeconds,
                theme = theme,
                onNextLevel = onNextLevel,
                onReplay = onReplay,
                onLevelSelect = onLevelSelect
            )
        }

        // Reward Hint Dialog
        if (state.showRewardHintDialog) {
            RewardHintDialog(
                theme = theme,
                onClaimFreeHints = onClaimHints,
                onDismiss = onDismissReward
            )
        }
    }
}
