package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.EndlessScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TimeAttackScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArrowPuzzleApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ArrowPuzzleApp(viewModel: GameViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle system back navigation
    BackHandler(enabled = state.currentScreen != AppScreen.HOME) {
        when (state.currentScreen) {
            AppScreen.GAME -> viewModel.navigateTo(AppScreen.LEVEL_SELECT)
            AppScreen.LEVEL_SELECT,
            AppScreen.DAILY_CHALLENGE,
            AppScreen.TIME_ATTACK,
            AppScreen.ENDLESS,
            AppScreen.STATISTICS,
            AppScreen.SETTINGS -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.HOME -> { /* Exit app */ }
        }
    }

    Crossfade(targetState = state.currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            AppScreen.HOME -> {
                HomeScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onContinuePlay = { viewModel.startClassicLevel(state.maxUnlockedLevel) }
                )
            }
            AppScreen.LEVEL_SELECT -> {
                LevelSelectScreen(
                    state = state,
                    getLevelScore = { viewModel.getLevelScore(it) },
                    onLevelSelected = { viewModel.startClassicLevel(it) },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.GAME -> {
                GameScreen(
                    state = state,
                    onTileClick = { viewModel.onTileClicked(it) },
                    onUndoClick = { viewModel.undo() },
                    onResetClick = { viewModel.resetLevel() },
                    onHintClick = { viewModel.useHint() },
                    onBackClick = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                    onNextLevel = { viewModel.nextLevel() },
                    onReplay = { viewModel.replayCurrentLevel() },
                    onLevelSelect = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                    onClaimHints = { viewModel.addFreeHints() },
                    onDismissReward = { viewModel.dismissRewardDialog() }
                )
            }
            AppScreen.DAILY_CHALLENGE -> {
                DailyChallengeScreen(
                    state = state,
                    onTileClick = { viewModel.onTileClicked(it) },
                    onUndoClick = { viewModel.undo() },
                    onResetClick = { viewModel.resetLevel() },
                    onHintClick = { viewModel.useHint() },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onReplay = { viewModel.replayCurrentLevel() },
                    onClaimHints = { viewModel.addFreeHints() },
                    onDismissReward = { viewModel.dismissRewardDialog() }
                )
            }
            AppScreen.TIME_ATTACK -> {
                TimeAttackScreen(
                    state = state,
                    onTileClick = { viewModel.onTileClicked(it) },
                    onUndoClick = { viewModel.undo() },
                    onResetClick = { viewModel.resetLevel() },
                    onHintClick = { viewModel.useHint() },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onPlayAgain = { viewModel.startTimeAttack() },
                    onClaimHints = { viewModel.addFreeHints() },
                    onDismissReward = { viewModel.dismissRewardDialog() }
                )
            }
            AppScreen.ENDLESS -> {
                EndlessScreen(
                    state = state,
                    onDifficultySelect = { viewModel.startEndless(it) },
                    onTileClick = { viewModel.onTileClicked(it) },
                    onUndoClick = { viewModel.undo() },
                    onResetClick = { viewModel.resetLevel() },
                    onHintClick = { viewModel.useHint() },
                    onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                    onNextPuzzle = { viewModel.nextEndlessPuzzle() },
                    onReplay = { viewModel.replayCurrentLevel() },
                    onClaimHints = { viewModel.addFreeHints() },
                    onDismissReward = { viewModel.dismissRewardDialog() }
                )
            }
            AppScreen.STATISTICS -> {
                StatsScreen(
                    state = state,
                    totalPuzzlesSolved = viewModel.prefs.totalPuzzlesSolved,
                    totalMoves = viewModel.prefs.totalMoves,
                    timeAttackHighScore = viewModel.prefs.timeAttackHighScore,
                    endlessHighStreak = viewModel.prefs.endlessHighStreak,
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    state = state,
                    onToggleSound = { viewModel.toggleSound() },
                    onToggleHaptics = { viewModel.toggleHaptics() },
                    onSelectTheme = { viewModel.selectTheme(it) },
                    onResetAllProgress = { viewModel.resetAllGameProgress() },
                    onBack = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
        }
    }
}
