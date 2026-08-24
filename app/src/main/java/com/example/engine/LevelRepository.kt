package com.example.engine

import com.example.model.ArrowColorType
import com.example.model.ArrowDirection
import com.example.model.ArrowTile
import com.example.model.Difficulty
import java.time.LocalDate

object LevelRepository {

    const val TOTAL_CLASSIC_LEVELS = 60

    fun getLevel(levelNumber: Int): PuzzleConfig {
        return when (levelNumber) {
            1 -> getHandcraftedLevel1()
            2 -> getHandcraftedLevel2()
            3 -> getHandcraftedLevel3()
            4 -> getHandcraftedLevel4()
            5 -> getHandcraftedLevel5()
            in 6..15 -> {
                PuzzleGenerator.generateSolvablePuzzle(
                    levelNumber = levelNumber,
                    gridSize = 4,
                    densityPercent = 55 + (levelNumber * 2),
                    difficulty = Difficulty.EASY,
                    seed = levelNumber * 1039L
                )
            }
            in 16..35 -> {
                PuzzleGenerator.generateSolvablePuzzle(
                    levelNumber = levelNumber,
                    gridSize = 5,
                    densityPercent = 65 + ((levelNumber - 15) * 1),
                    difficulty = Difficulty.MEDIUM,
                    seed = levelNumber * 2087L
                )
            }
            in 36..50 -> {
                PuzzleGenerator.generateSolvablePuzzle(
                    levelNumber = levelNumber,
                    gridSize = 6,
                    densityPercent = 70 + ((levelNumber - 35) * 1),
                    difficulty = Difficulty.HARD,
                    seed = levelNumber * 3163L
                )
            }
            else -> {
                PuzzleGenerator.generateSolvablePuzzle(
                    levelNumber = levelNumber,
                    gridSize = 8,
                    densityPercent = 75,
                    difficulty = Difficulty.EXPERT,
                    seed = levelNumber * 4271L
                )
            }
        }
    }

    fun getDifficultyForLevel(levelNumber: Int): Difficulty {
        return when {
            levelNumber <= 15 -> Difficulty.EASY
            levelNumber <= 35 -> Difficulty.MEDIUM
            levelNumber <= 50 -> Difficulty.HARD
            else -> Difficulty.EXPERT
        }
    }

    fun getDailyChallengePuzzle(date: LocalDate = LocalDate.now()): PuzzleConfig {
        val epochDay = date.toEpochDay()
        val dayOfYear = date.dayOfYear
        val gridSize = if (dayOfYear % 2 == 0) 5 else 6
        val diff = if (gridSize == 5) Difficulty.MEDIUM else Difficulty.HARD
        return PuzzleGenerator.generateSolvablePuzzle(
            levelNumber = dayOfYear,
            gridSize = gridSize,
            densityPercent = 72,
            difficulty = diff,
            seed = epochDay * 5437L
        ).copy(title = "Daily Challenge • ${date.month.name.take(3)} ${date.dayOfMonth}")
    }

    fun getTimeAttackPuzzle(stage: Int): PuzzleConfig {
        val size = when {
            stage <= 3 -> 4
            stage <= 8 -> 5
            else -> 6
        }
        val diff = when (size) {
            4 -> Difficulty.EASY
            5 -> Difficulty.MEDIUM
            else -> Difficulty.HARD
        }
        return PuzzleGenerator.generateSolvablePuzzle(
            levelNumber = stage,
            gridSize = size,
            densityPercent = 60 + (stage.coerceAtMost(10)),
            difficulty = diff,
            seed = System.currentTimeMillis() + stage * 777L
        ).copy(title = "Stage $stage")
    }

    fun getEndlessPuzzle(difficulty: Difficulty, sequenceId: Int): PuzzleConfig {
        return PuzzleGenerator.generateSolvablePuzzle(
            levelNumber = sequenceId,
            gridSize = difficulty.gridSize,
            densityPercent = when (difficulty) {
                Difficulty.EASY -> 60
                Difficulty.MEDIUM -> 70
                Difficulty.HARD -> 78
                Difficulty.EXPERT -> 82
            },
            difficulty = difficulty,
            seed = System.currentTimeMillis() xor (sequenceId.toLong() * 991L)
        ).copy(title = "Endless #${sequenceId}")
    }

    // Handcrafted introductory levels
    private fun getHandcraftedLevel1(): PuzzleConfig {
        val tiles = listOf(
            ArrowTile("t1", 0, 1, ArrowDirection.UP, ArrowColorType.CYAN),
            ArrowTile("t2", 1, 0, ArrowDirection.LEFT, ArrowColorType.CORAL),
            ArrowTile("t3", 1, 3, ArrowDirection.RIGHT, ArrowColorType.AMBER),
            ArrowTile("t4", 3, 2, ArrowDirection.DOWN, ArrowColorType.EMERALD),
            ArrowTile("t5", 1, 1, ArrowDirection.UP, ArrowColorType.PURPLE),
            ArrowTile("t6", 2, 2, ArrowDirection.DOWN, ArrowColorType.INDIGO)
        )
        return PuzzleConfig(
            levelNumber = 1,
            title = "Level 1 • First Flight",
            gridSize = 4,
            tiles = tiles,
            parMoves = 6,
            difficulty = Difficulty.EASY
        )
    }

    private fun getHandcraftedLevel2(): PuzzleConfig {
        val tiles = listOf(
            ArrowTile("t1", 0, 0, ArrowDirection.RIGHT, ArrowColorType.CYAN),
            ArrowTile("t2", 0, 1, ArrowDirection.RIGHT, ArrowColorType.CORAL),
            ArrowTile("t3", 0, 2, ArrowDirection.RIGHT, ArrowColorType.AMBER),
            ArrowTile("t4", 1, 0, ArrowDirection.DOWN, ArrowColorType.EMERALD),
            ArrowTile("t5", 2, 0, ArrowDirection.DOWN, ArrowColorType.PURPLE),
            ArrowTile("t6", 3, 0, ArrowDirection.DOWN, ArrowColorType.INDIGO),
            ArrowTile("t7", 3, 3, ArrowDirection.UP, ArrowColorType.CYAN),
            ArrowTile("t8", 2, 3, ArrowDirection.UP, ArrowColorType.CORAL)
        )
        return PuzzleConfig(
            levelNumber = 2,
            title = "Level 2 • Clear Runway",
            gridSize = 4,
            tiles = tiles,
            parMoves = 8,
            difficulty = Difficulty.EASY
        )
    }

    private fun getHandcraftedLevel3(): PuzzleConfig {
        val tiles = listOf(
            ArrowTile("t1", 1, 1, ArrowDirection.UP, ArrowColorType.CYAN),
            ArrowTile("t2", 1, 2, ArrowDirection.RIGHT, ArrowColorType.CORAL),
            ArrowTile("t3", 2, 2, ArrowDirection.DOWN, ArrowColorType.AMBER),
            ArrowTile("t4", 2, 1, ArrowDirection.LEFT, ArrowColorType.EMERALD),
            ArrowTile("t5", 0, 1, ArrowDirection.UP, ArrowColorType.PURPLE),
            ArrowTile("t6", 1, 3, ArrowDirection.RIGHT, ArrowColorType.INDIGO),
            ArrowTile("t7", 3, 2, ArrowDirection.DOWN, ArrowColorType.CYAN),
            ArrowTile("t8", 2, 0, ArrowDirection.LEFT, ArrowColorType.CORAL)
        )
        return PuzzleConfig(
            levelNumber = 3,
            title = "Level 3 • Pinwheel",
            gridSize = 4,
            tiles = tiles,
            parMoves = 8,
            difficulty = Difficulty.EASY
        )
    }

    private fun getHandcraftedLevel4(): PuzzleConfig {
        val tiles = listOf(
            ArrowTile("t1", 0, 0, ArrowDirection.DOWN, ArrowColorType.CYAN),
            ArrowTile("t2", 1, 0, ArrowDirection.RIGHT, ArrowColorType.CORAL),
            ArrowTile("t3", 1, 1, ArrowDirection.DOWN, ArrowColorType.AMBER),
            ArrowTile("t4", 2, 1, ArrowDirection.RIGHT, ArrowColorType.EMERALD),
            ArrowTile("t5", 2, 2, ArrowDirection.DOWN, ArrowColorType.PURPLE),
            ArrowTile("t6", 3, 2, ArrowDirection.RIGHT, ArrowColorType.INDIGO),
            ArrowTile("t7", 3, 3, ArrowDirection.DOWN, ArrowColorType.CYAN),
            ArrowTile("t8", 0, 3, ArrowDirection.UP, ArrowColorType.CORAL),
            ArrowTile("t9", 0, 2, ArrowDirection.UP, ArrowColorType.AMBER)
        )
        return PuzzleConfig(
            levelNumber = 4,
            title = "Level 4 • Staircase",
            gridSize = 4,
            tiles = tiles,
            parMoves = 9,
            difficulty = Difficulty.EASY
        )
    }

    private fun getHandcraftedLevel5(): PuzzleConfig {
        val tiles = listOf(
            ArrowTile("t1", 0, 0, ArrowDirection.RIGHT, ArrowColorType.CYAN),
            ArrowTile("t2", 0, 3, ArrowDirection.LEFT, ArrowColorType.CORAL),
            ArrowTile("t3", 3, 0, ArrowDirection.RIGHT, ArrowColorType.AMBER),
            ArrowTile("t4", 3, 3, ArrowDirection.LEFT, ArrowColorType.EMERALD),
            ArrowTile("t5", 1, 1, ArrowDirection.UP, ArrowColorType.PURPLE),
            ArrowTile("t6", 1, 2, ArrowDirection.UP, ArrowColorType.INDIGO),
            ArrowTile("t7", 2, 1, ArrowDirection.DOWN, ArrowColorType.CYAN),
            ArrowTile("t8", 2, 2, ArrowDirection.DOWN, ArrowColorType.CORAL),
            ArrowTile("t9", 0, 1, ArrowDirection.UP, ArrowColorType.AMBER),
            ArrowTile("t10", 0, 2, ArrowDirection.UP, ArrowColorType.EMERALD)
        )
        return PuzzleConfig(
            levelNumber = 5,
            title = "Level 5 • Crossroads",
            gridSize = 4,
            tiles = tiles,
            parMoves = 10,
            difficulty = Difficulty.EASY
        )
    }
}
