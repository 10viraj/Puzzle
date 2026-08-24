package com.example.engine

import com.example.model.ArrowColorType
import com.example.model.ArrowDirection
import com.example.model.ArrowTile
import com.example.model.Difficulty
import java.util.Random

data class PuzzleConfig(
    val levelNumber: Int,
    val title: String,
    val gridSize: Int,
    val tiles: List<ArrowTile>,
    val parMoves: Int,
    val difficulty: Difficulty,
    val optimalSequence: List<String> = emptyList()
)

object PuzzleGenerator {

    /**
     * Generates a guaranteed solvable puzzle using Reverse Simulation.
     * In reverse simulation, we place arrows onto an empty board backwards.
     * An arrow pointing in direction D can be placed at (r, c) only if the ray
     * from (r, c) along direction D to the board edge is currently empty.
     */
    fun generateSolvablePuzzle(
        levelNumber: Int,
        gridSize: Int,
        densityPercent: Int = 75,
        difficulty: Difficulty = Difficulty.EASY,
        seed: Long = levelNumber.toLong() * 9973L
    ): PuzzleConfig {
        val random = Random(seed)
        val maxCells = gridSize * gridSize
        val targetTileCount = ((maxCells * densityPercent) / 100).coerceIn(gridSize + 1, maxCells - 1)

        val grid = Array(gridSize) { arrayOfNulls<ArrowTile>(gridSize) }
        val placedTiles = mutableListOf<ArrowTile>()
        val reversePlacementOrder = mutableListOf<String>()

        val colors = ArrowColorType.values()
        var attempts = 0
        val maxAttempts = 1500

        while (placedTiles.size < targetTileCount && attempts < maxAttempts) {
            attempts++
            val r = random.nextInt(gridSize)
            val c = random.nextInt(gridSize)

            if (grid[r][c] != null) continue

            // Pick a direction that has a clear path to the edge in current grid
            val possibleDirections = ArrowDirection.values().filter { dir ->
                isPathClearToEdge(grid, gridSize, r, c, dir)
            }

            if (possibleDirections.isNotEmpty()) {
                val dir = possibleDirections[random.nextInt(possibleDirections.size)]
                val color = colors[(placedTiles.size + r + c) % colors.size]
                val id = "tile_${r}_${c}_${placedTiles.size}"
                val tile = ArrowTile(
                    id = id,
                    row = r,
                    col = c,
                    direction = dir,
                    colorType = color
                )
                grid[r][c] = tile
                placedTiles.add(tile)
                reversePlacementOrder.add(id)
            }
        }

        // If for any reason density didn't meet minimum, ensure at least some tiles exist
        if (placedTiles.isEmpty()) {
            for (i in 0 until gridSize) {
                val id = "tile_${i}_${i}"
                val tile = ArrowTile(
                    id = id,
                    row = i,
                    col = i,
                    direction = if (i % 2 == 0) ArrowDirection.UP else ArrowDirection.DOWN,
                    colorType = colors[i % colors.size]
                )
                grid[i][i] = tile
                placedTiles.add(tile)
                reversePlacementOrder.add(id)
            }
        }

        // The forward optimal sequence is the reverse of the reverse placement order
        val optimalSequence = reversePlacementOrder.reversed()

        return PuzzleConfig(
            levelNumber = levelNumber,
            title = "Level $levelNumber",
            gridSize = gridSize,
            tiles = placedTiles,
            parMoves = placedTiles.size,
            difficulty = difficulty,
            optimalSequence = optimalSequence
        )
    }

    /**
     * Checks if moving along direction dir from (r, c) to the boundary hits no obstacles.
     */
    private fun isPathClearToEdge(
        grid: Array<Array<ArrowTile?>>,
        gridSize: Int,
        r: Int,
        c: Int,
        dir: ArrowDirection
    ): Boolean {
        var currR = r + dir.deltaRow
        var currC = c + dir.deltaCol

        while (currR in 0 until gridSize && currC in 0 until gridSize) {
            if (grid[currR][currC] != null) {
                return false
            }
            currR += dir.deltaRow
            currC += dir.deltaCol
        }
        return true
    }

    /**
     * Checks if a tile can exit in forward play.
     */
    fun canTileExit(
        tile: ArrowTile,
        allTiles: List<ArrowTile>,
        gridSize: Int
    ): Boolean {
        val activeTiles = allTiles.filter { !it.isExiting }
        var currR = tile.row + tile.direction.deltaRow
        var currC = tile.col + tile.direction.deltaCol

        while (currR in 0 until gridSize && currC in 0 until gridSize) {
            val isOccupied = activeTiles.any { it.row == currR && it.col == currC }
            if (isOccupied) return false
            currR += tile.direction.deltaRow
            currC += tile.direction.deltaCol
        }
        return true
    }

    /**
     * Finds the tile that is blocking a given tile, if any.
     */
    fun getBlockingTile(
        tile: ArrowTile,
        allTiles: List<ArrowTile>,
        gridSize: Int
    ): ArrowTile? {
        val activeTiles = allTiles.filter { !it.isExiting }
        var currR = tile.row + tile.direction.deltaRow
        var currC = tile.col + tile.direction.deltaCol

        while (currR in 0 until gridSize && currC in 0 until gridSize) {
            val blocker = activeTiles.firstOrNull { it.row == currR && it.col == currC }
            if (blocker != null) return blocker
            currR += tile.direction.deltaRow
            currC += tile.direction.deltaCol
        }
        return null
    }

    /**
     * Returns all tiles that can currently exit freely.
     */
    fun getClearTiles(allTiles: List<ArrowTile>, gridSize: Int): List<ArrowTile> {
        return allTiles.filter { !it.isExiting && canTileExit(it, allTiles, gridSize) }
    }
}
