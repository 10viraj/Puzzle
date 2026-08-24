package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LevelScore
import com.example.model.ThemePalette
import java.time.LocalDate

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("arrow_puzzle_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MAX_UNLOCKED_LEVEL = "max_unlocked_level"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_HINTS_REMAINING = "hints_remaining"
        private const val KEY_TOTAL_PUZZLES_SOLVED = "total_puzzles_solved"
        private const val KEY_TOTAL_MOVES = "total_moves"
        private const val KEY_DAILY_STREAK = "daily_streak"
        private const val KEY_LAST_DAILY_EPOCH = "last_daily_epoch"
        private const val KEY_TIME_ATTACK_HIGH_SCORE = "time_attack_high_score"
        private const val KEY_ENDLESS_HIGH_STREAK = "endless_high_streak"
    }

    var maxUnlockedLevel: Int
        get() = prefs.getInt(KEY_MAX_UNLOCKED_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_MAX_UNLOCKED_LEVEL, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    var selectedThemeId: String
        get() = prefs.getString(KEY_SELECTED_THEME, ThemePalette.EDITORIAL.id) ?: ThemePalette.EDITORIAL.id
        set(value) = prefs.edit().putString(KEY_SELECTED_THEME, value).apply()

    var hintsRemaining: Int
        get() = prefs.getInt(KEY_HINTS_REMAINING, 5)
        set(value) = prefs.edit().putInt(KEY_HINTS_REMAINING, value).apply()

    var totalPuzzlesSolved: Int
        get() = prefs.getInt(KEY_TOTAL_PUZZLES_SOLVED, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_PUZZLES_SOLVED, value).apply()

    var totalMoves: Int
        get() = prefs.getInt(KEY_TOTAL_MOVES, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_MOVES, value).apply()

    var dailyStreak: Int
        get() = prefs.getInt(KEY_DAILY_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_DAILY_STREAK, value).apply()

    var lastDailyEpochDay: Long
        get() = prefs.getLong(KEY_LAST_DAILY_EPOCH, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_DAILY_EPOCH, value).apply()

    var timeAttackHighScore: Int
        get() = prefs.getInt(KEY_TIME_ATTACK_HIGH_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_TIME_ATTACK_HIGH_SCORE, value).apply()

    var endlessHighStreak: Int
        get() = prefs.getInt(KEY_ENDLESS_HIGH_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_ENDLESS_HIGH_STREAK, value).apply()

    fun getLevelScore(levelNumber: Int): LevelScore {
        val stars = prefs.getInt("level_${levelNumber}_stars", 0)
        val bestMoves = prefs.getInt("level_${levelNumber}_best_moves", 0)
        val bestTime = prefs.getInt("level_${levelNumber}_best_time", 0)
        val completed = prefs.getBoolean("level_${levelNumber}_completed", false)
        return LevelScore(levelNumber, stars, bestMoves, bestTime, completed)
    }

    fun saveLevelScore(levelNumber: Int, stars: Int, moves: Int, timeSeconds: Int) {
        val current = getLevelScore(levelNumber)
        val newBestMoves = if (current.bestMoves == 0 || moves < current.bestMoves) moves else current.bestMoves
        val newBestTime = if (current.bestTimeSeconds == 0 || timeSeconds < current.bestTimeSeconds) timeSeconds else current.bestTimeSeconds
        val newStars = maxOf(current.stars, stars)

        prefs.edit()
            .putInt("level_${levelNumber}_stars", newStars)
            .putInt("level_${levelNumber}_best_moves", newBestMoves)
            .putInt("level_${levelNumber}_best_time", newBestTime)
            .putBoolean("level_${levelNumber}_completed", true)
            .apply()

        // Unlock next level if this is the highest unlocked
        if (levelNumber >= maxUnlockedLevel) {
            maxUnlockedLevel = levelNumber + 1
        }
    }

    fun getTotalStarsEarned(): Int {
        var total = 0
        for (i in 1..LevelRepository.TOTAL_CLASSIC_LEVELS) {
            total += prefs.getInt("level_${i}_stars", 0)
        }
        return total
    }

    fun checkAndRecordDailyCompletion(date: LocalDate = LocalDate.now()): Boolean {
        val todayEpoch = date.toEpochDay()
        if (lastDailyEpochDay == todayEpoch) {
            return false // Already completed today
        }
        val isConsecutive = (lastDailyEpochDay == todayEpoch - 1)
        val newStreak = if (isConsecutive) dailyStreak + 1 else 1
        dailyStreak = newStreak
        lastDailyEpochDay = todayEpoch
        hintsRemaining += 2 // bonus hint for daily completion!
        return true
    }

    fun isDailyCompletedToday(date: LocalDate = LocalDate.now()): Boolean {
        return lastDailyEpochDay == date.toEpochDay()
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        hintsRemaining = 5
        maxUnlockedLevel = 1
    }
}
