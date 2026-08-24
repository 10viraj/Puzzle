package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowTile
import com.example.model.ThemePalette

@Composable
fun GameBoard(
    gridSize: Int,
    tiles: List<ArrowTile>,
    theme: ThemePalette,
    onTileClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    totalInitialTiles: Int = tiles.size
) {
    val remainingCount = tiles.size
    val progress = if (totalInitialTiles > 0) {
        (totalInitialTiles - remainingCount).toFloat() / totalInitialTiles.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "board_progress")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 440.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Board Container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = if (theme.isLight) 6.dp else 12.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = if (theme.isLight) 0.12f else 0.4f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(theme.boardBackground)
                .border(
                    width = 1.5.dp,
                    color = theme.boardBorder,
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(12.dp)
        ) {
            val cellSizeDp = maxWidth / gridSize
            val density = LocalDensity.current
            val cellSizePx = with(density) { cellSizeDp.toPx() }

            // Render empty grid slots for tactile depth
            val emptySlotBg = if (theme.isLight) Color(0xFFF0F1EB) else theme.gridLineColor.copy(alpha = 0.35f)
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    Box(
                        modifier = Modifier
                            .size(cellSizeDp)
                            .offset(
                                x = cellSizeDp * c,
                                y = cellSizeDp * r
                            )
                            .padding(4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(emptySlotBg)
                            .border(
                                width = 1.dp,
                                color = theme.gridLineColor.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                    )
                }
            }

            // Render active arrow tiles
            tiles.forEach { tile ->
                ArrowCell(
                    tile = tile,
                    theme = theme,
                    cellSizePx = cellSizePx,
                    onClick = { onTileClicked(tile.id) },
                    modifier = Modifier
                        .size(cellSizeDp)
                        .offset(
                            x = cellSizeDp * tile.col,
                            y = cellSizeDp * tile.row
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress Bar indicator under the board
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(theme.boardBackground)
                    .border(0.5.dp, theme.boardBorder.copy(alpha = 0.5f), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(CircleShape)
                        .background(theme.primaryArrowColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$remainingCount ARROWS REMAINING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = theme.textSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

