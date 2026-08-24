package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GamePreferences
import com.example.engine.LevelRepository
import com.example.engine.PuzzleConfig
import com.example.engine.PuzzleGenerator
import com.example.model.ArrowTile
import com.example.model.BoardSnapshot
import com.example.model.Difficulty
import com.example.model.GameMode
import com.example.model.LevelScore
import com.example.model.ThemePalette
import com.example.sound.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppScreen {
    HOME,
    LEVEL_SELECT,
    GAME,
    DAILY_CHALLENGE,
    TIME_ATTACK,
    ENDLESS,
    STATISTICS,
    SETTINGS
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val gameMode: GameMode = GameMode.CLASSIC,
    val currentPuzzle: PuzzleConfig? = null,
    val tiles: List<ArrowTile> = emptyList(),
    val movesCount: Int = 0,
    val timeElapsedSeconds: Int = 0,
    val isGameActive: Boolean = false,
    val isLevelCompleted: Boolean = false,
    val starsEarned: Int = 0,
    val hintsRemaining: Int = 5,
    val isHintActive: Boolean = false,
    val hintedTileId: String? = null,
    val blockedTileId: String? = null,
    val canUndo: Boolean = false,
    val selectedDifficulty: Difficulty = Difficulty.EASY,
    val selectedTheme: ThemePalette = ThemePalette.EDITORIAL,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    // Time Attack specific
    val timeAttackSecondsRemaining: Int = 60,
    val timeAttackScore: Int = 0,
    val timeAttackStage: Int = 1,
    val isTimeAttackOver: Boolean = false,
    // Endless specific
    val endlessStreak: Int = 0,
    val endlessDifficulty: Difficulty = Difficulty.MEDIUM,
    // Daily Challenge
    val isDailyCompletedToday: Boolean = false,
    val dailyStreak: Int = 0,
    // General Stats
    val totalStars: Int = 0,
    val maxUnlockedLevel: Int = 1,
    val comboCount: Int = 0,
    val showRewardHintDialog: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val prefs = GamePreferences(application)
    val soundManager = SoundManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<BoardSnapshot>()
    private var timerJob: Job? = null
    private var timeAttackJob: Job? = null

    init {
        // Sync preferences
        soundManager.isSoundEnabled = prefs.isSoundEnabled
        soundManager.isHapticsEnabled = prefs.isHapticsEnabled

        val currentTheme = ThemePalette.values().firstOrNull { it.id == prefs.selectedThemeId }
            ?: ThemePalette.EDITORIAL

        _uiState.update {
            it.copy(
                selectedTheme = currentTheme,
                soundEnabled = prefs.isSoundEnabled,
                hapticsEnabled = prefs.isHapticsEnabled,
                hintsRemaining = prefs.hintsRemaining,
                maxUnlockedLevel = prefs.maxUnlockedLevel,
                totalStars = prefs.getTotalStarsEarned(),
                dailyStreak = prefs.dailyStreak,
                isDailyCompletedToday = prefs.isDailyCompletedToday()
            )
        }
    }

    fun navigateTo(screen: AppScreen) {
        soundManager.playTap()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectTheme(theme: ThemePalette) {
        prefs.selectedThemeId = theme.id
        _uiState.update { it.copy(selectedTheme = theme) }
        soundManager.playTap()
    }

    fun toggleSound() {
        val next = !prefs.isSoundEnabled
        prefs.isSoundEnabled = next
        soundManager.isSoundEnabled = next
        _uiState.update { it.copy(soundEnabled = next) }
        if (next) soundManager.playTap()
    }

    fun toggleHaptics() {
        val next = !prefs.isHapticsEnabled
        prefs.isHapticsEnabled = next
        soundManager.isHapticsEnabled = next
        _uiState.update { it.copy(hapticsEnabled = next) }
        if (next) soundManager.vibrateShort()
    }

    fun startClassicLevel(levelNumber: Int) {
        val puzzle = LevelRepository.getLevel(levelNumber)
        startPuzzle(puzzle, GameMode.CLASSIC, AppScreen.GAME)
    }

    fun startDailyChallenge() {
        val puzzle = LevelRepository.getDailyChallengePuzzle()
        startPuzzle(puzzle, GameMode.DAILY_CHALLENGE, AppScreen.DAILY_CHALLENGE)
    }

    fun startTimeAttack() {
        val puzzle = LevelRepository.getTimeAttackPuzzle(stage = 1)
        _uiState.update {
            it.copy(
                timeAttackScore = 0,
                timeAttackStage = 1,
                timeAttackSecondsRemaining = 60,
                isTimeAttackOver = false
            )
        }
        startPuzzle(puzzle, GameMode.TIME_ATTACK, AppScreen.TIME_ATTACK)
        startTimeAttackCountdown()
    }

    fun startEndless(difficulty: Difficulty = _uiState.value.endlessDifficulty) {
        val puzzle = LevelRepository.getEndlessPuzzle(difficulty, sequenceId = _uiState.value.endlessStreak + 1)
        _uiState.update { it.copy(endlessDifficulty = difficulty) }
        startPuzzle(puzzle, GameMode.ENDLESS, AppScreen.ENDLESS)
    }

    private fun startPuzzle(puzzle: PuzzleConfig, mode: GameMode, screen: AppScreen) {
        timerJob?.cancel()
        undoStack.clear()

        _uiState.update {
            it.copy(
                currentScreen = screen,
                gameMode = mode,
                currentPuzzle = puzzle,
                tiles = puzzle.tiles,
                movesCount = 0,
                timeElapsedSeconds = 0,
                isGameActive = true,
                isLevelCompleted = false,
                starsEarned = 0,
                isHintActive = false,
                hintedTileId = null,
                blockedTileId = null,
                canUndo = false,
                comboCount = 0
            )
        }

        // Start elapsed timer
        timerJob = viewModelScope.launch {
            while (_uiState.value.isGameActive && !_uiState.value.isLevelCompleted) {
                delay(1000L)
                _uiState.update { it.copy(timeElapsedSeconds = it.timeElapsedSeconds + 1) }
            }
        }
    }

    private fun startTimeAttackCountdown() {
        timeAttackJob?.cancel()
        timeAttackJob = viewModelScope.launch {
            while (_uiState.value.timeAttackSecondsRemaining > 0 && _uiState.value.isGameActive && !_uiState.value.isTimeAttackOver) {
                delay(1000L)
                val remaining = _uiState.value.timeAttackSecondsRemaining - 1
                if (remaining <= 0) {
                    // Time attack over!
                    val finalScore = _uiState.value.timeAttackScore
                    if (finalScore > prefs.timeAttackHighScore) {
                        prefs.timeAttackHighScore = finalScore
                    }
                    _uiState.update {
                        it.copy(
                            timeAttackSecondsRemaining = 0,
                            isTimeAttackOver = true,
                            isGameActive = false
                        )
                    }
                    soundManager.playVictoryFanfare()
                    soundManager.vibrateVictory()
                } else {
                    _uiState.update { it.copy(timeAttackSecondsRemaining = remaining) }
                }
            }
        }
    }

    fun onTileClicked(tileId: String) {
        val state = _uiState.value
        if (!state.isGameActive || state.isLevelCompleted) return

        val tile = state.tiles.firstOrNull { it.id == tileId } ?: return
        if (tile.isExiting) return

        val puzzle = state.currentPuzzle ?: return

        val canExit = PuzzleGenerator.canTileExit(tile, state.tiles, puzzle.gridSize)

        if (canExit) {
            // Save state for undo
            saveSnapshot(state.tiles, state.movesCount, tileId)

            val newMoves = state.movesCount + 1
            prefs.totalMoves = prefs.totalMoves + 1
            val newCombo = state.comboCount + 1

            soundManager.playSuccessMove(pitchStep = newCombo)
            soundManager.vibrateSuccess()

            // Mark tile as exiting
            val updatedTiles = state.tiles.map {
                if (it.id == tileId) it.copy(isExiting = true, isHinted = false)
                else it.copy(isHinted = false)
            }

            _uiState.update {
                it.copy(
                    tiles = updatedTiles,
                    movesCount = newMoves,
                    isHintActive = false,
                    hintedTileId = null,
                    blockedTileId = null,
                    canUndo = true,
                    comboCount = newCombo
                )
            }

            // Check if level complete
            val remainingTiles = updatedTiles.filter { !it.isExiting }
            if (remainingTiles.isEmpty()) {
                onPuzzleSolved()
            }
        } else {
            // Blocked!
            soundManager.playBlocked()
            soundManager.vibrateBlocked()

            // Trigger blocked shake effect
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        blockedTileId = tileId,
                        comboCount = 0,
                        tiles = it.tiles.map { t ->
                            if (t.id == tileId) t.copy(isBlocked = true) else t
                        }
                    )
                }
                delay(350L)
                _uiState.update {
                    it.copy(
                        blockedTileId = null,
                        tiles = it.tiles.map { t ->
                            if (t.id == tileId) t.copy(isBlocked = false) else t
                        }
                    )
                }
            }
        }
    }

    private fun saveSnapshot(tiles: List<ArrowTile>, moves: Int, tileId: String) {
        undoStack.add(BoardSnapshot(tiles = tiles, movesCount = moves, lastMovedTileId = tileId))
        if (undoStack.size > 30) {
            undoStack.removeAt(0)
        }
    }

    fun undo() {
        if (undoStack.isEmpty()) return
        val lastSnapshot = undoStack.removeAt(undoStack.size - 1)

        soundManager.playUndo()
        soundManager.vibrateShort()

        _uiState.update {
            it.copy(
                tiles = lastSnapshot.tiles,
                movesCount = lastSnapshot.movesCount,
                canUndo = undoStack.isNotEmpty(),
                isHintActive = false,
                hintedTileId = null,
                comboCount = 0
            )
        }
    }

    fun resetLevel() {
        val puzzle = _uiState.value.currentPuzzle ?: return
        soundManager.playTap()
        startPuzzle(puzzle, _uiState.value.gameMode, _uiState.value.currentScreen)
    }

    fun useHint() {
        val state = _uiState.value
        if (!state.isGameActive || state.isLevelCompleted) return
        val puzzle = state.currentPuzzle ?: return

        if (state.hintsRemaining <= 0) {
            // Show get more hints reward dialog
            _uiState.update { it.copy(showRewardHintDialog = true) }
            return
        }

        val clearTiles = PuzzleGenerator.getClearTiles(state.tiles, puzzle.gridSize)
        if (clearTiles.isEmpty()) return

        // Pick one clear tile
        val hintTile = clearTiles.first()

        soundManager.playHint()
        soundManager.vibrateShort()

        val remainingHints = state.hintsRemaining - 1
        prefs.hintsRemaining = remainingHints

        _uiState.update {
            it.copy(
                hintsRemaining = remainingHints,
                isHintActive = true,
                hintedTileId = hintTile.id,
                tiles = it.tiles.map { t ->
                    if (t.id == hintTile.id) t.copy(isHinted = true) else t.copy(isHinted = false)
                }
            )
        }
    }

    fun addFreeHints(amount: Int = 3) {
        val newHints = _uiState.value.hintsRemaining + amount
        prefs.hintsRemaining = newHints
        _uiState.update { it.copy(hintsRemaining = newHints, showRewardHintDialog = false) }
        soundManager.playHint()
    }

    fun dismissRewardDialog() {
        _uiState.update { it.copy(showRewardHintDialog = false) }
    }

    private fun onPuzzleSolved() {
        timerJob?.cancel()
        val state = _uiState.value
        val puzzle = state.currentPuzzle ?: return
        prefs.totalPuzzlesSolved = prefs.totalPuzzlesSolved + 1

        soundManager.playVictoryFanfare()
        soundManager.vibrateVictory()

        when (state.gameMode) {
            GameMode.CLASSIC -> {
                val par = puzzle.parMoves
                val moves = state.movesCount
                val stars = when {
                    moves <= par -> 3
                    moves <= par + 2 -> 2
                    else -> 1
                }

                prefs.saveLevelScore(
                    levelNumber = puzzle.levelNumber,
                    stars = stars,
                    moves = moves,
                    timeSeconds = state.timeElapsedSeconds
                )

                _uiState.update {
                    it.copy(
                        isLevelCompleted = true,
                        isGameActive = false,
                        starsEarned = stars,
                        maxUnlockedLevel = prefs.maxUnlockedLevel,
                        totalStars = prefs.getTotalStarsEarned()
                    )
                }
            }
            GameMode.DAILY_CHALLENGE -> {
                val isNew = prefs.checkAndRecordDailyCompletion(LocalDate.now())
                _uiState.update {
                    it.copy(
                        isLevelCompleted = true,
                        isGameActive = false,
                        starsEarned = 3,
                        dailyStreak = prefs.dailyStreak,
                        isDailyCompletedToday = true,
                        hintsRemaining = prefs.hintsRemaining
                    )
                }
            }
            GameMode.TIME_ATTACK -> {
                // Award points: 100 base + 10 * remaining seconds + 50 combo
                val newScore = state.timeAttackScore + 100 + (state.comboCount * 25)
                val newStage = state.timeAttackStage + 1
                val bonusSeconds = 5
                val newRemainingTime = (state.timeAttackSecondsRemaining + bonusSeconds).coerceAtMost(90)

                _uiState.update {
                    it.copy(
                        timeAttackScore = newScore,
                        timeAttackStage = newStage,
                        timeAttackSecondsRemaining = newRemainingTime
                    )
                }

                // Next stage puzzle in time attack
                viewModelScope.launch {
                    delay(300L)
                    val nextPuzzle = LevelRepository.getTimeAttackPuzzle(stage = newStage)
                    startPuzzle(nextPuzzle, GameMode.TIME_ATTACK, AppScreen.TIME_ATTACK)
                }
            }
            GameMode.ENDLESS -> {
                val newStreak = state.endlessStreak + 1
                if (newStreak > prefs.endlessHighStreak) {
                    prefs.endlessHighStreak = newStreak
                }
                _uiState.update {
                    it.copy(
                        endlessStreak = newStreak,
                        isLevelCompleted = true,
                        isGameActive = false,
                        starsEarned = 3
                    )
                }
            }
        }
    }

    fun nextLevel() {
        val current = _uiState.value.currentPuzzle?.levelNumber ?: 1
        val next = current + 1
        if (next <= LevelRepository.TOTAL_CLASSIC_LEVELS) {
            startClassicLevel(next)
        } else {
            navigateTo(AppScreen.LEVEL_SELECT)
        }
    }

    fun replayCurrentLevel() {
        val current = _uiState.value.currentPuzzle ?: return
        startPuzzle(current, _uiState.value.gameMode, _uiState.value.currentScreen)
    }

    fun nextEndlessPuzzle() {
        startEndless(_uiState.value.endlessDifficulty)
    }

    fun getLevelScore(levelNumber: Int): LevelScore {
        return prefs.getLevelScore(levelNumber)
    }

    fun resetAllGameProgress() {
        prefs.resetAllProgress()
        _uiState.update {
            it.copy(
                maxUnlockedLevel = 1,
                totalStars = 0,
                hintsRemaining = 5,
                dailyStreak = 0,
                isDailyCompletedToday = false
            )
        }
    }
}
