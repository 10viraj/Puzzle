package com.example.model

enum class ArrowDirection(val deltaRow: Int, val deltaCol: Int, val angleDegrees: Float) {
    UP(-1, 0, 270f),
    RIGHT(0, 1, 0f),
    DOWN(1, 0, 90f),
    LEFT(0, -1, 180f);

    companion object {
        fun fromAngle(degrees: Float): ArrowDirection {
            return when (((degrees % 360) + 360) % 360) {
                0f -> RIGHT
                90f -> DOWN
                180f -> LEFT
                270f -> UP
                else -> RIGHT
            }
        }
    }
}

enum class ArrowColorType {
    CYAN,
    CORAL,
    AMBER,
    EMERALD,
    PURPLE,
    INDIGO
}

data class ArrowTile(
    val id: String,
    val row: Int,
    val col: Int,
    val direction: ArrowDirection,
    val colorType: ArrowColorType = ArrowColorType.CYAN,
    val isExiting: Boolean = false,
    val isBlocked: Boolean = false,
    val isHinted: Boolean = false
)

enum class GameMode {
    CLASSIC,
    DAILY_CHALLENGE,
    TIME_ATTACK,
    ENDLESS
}

enum class Difficulty(val title: String, val gridSize: Int, val description: String) {
    EASY("Easy", 4, "4×4 Grid • Ideal for beginners"),
    MEDIUM("Medium", 5, "5×5 Grid • Strategic unblocking"),
    HARD("Hard", 6, "6×6 Grid • Deep arrow webs"),
    EXPERT("Expert", 8, "8×8 Grid • Master mind benders")
}

data class LevelScore(
    val levelNumber: Int,
    val stars: Int,
    val bestMoves: Int,
    val bestTimeSeconds: Int,
    val isCompleted: Boolean = false
)

data class BoardSnapshot(
    val tiles: List<ArrowTile>,
    val movesCount: Int,
    val lastMovedTileId: String? = null
)
