package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemePalette

@Composable
fun GameHeader(
    title: String,
    moves: Int,
    parMoves: Int,
    timeSeconds: Int,
    remainingTiles: Int,
    totalTiles: Int,
    comboCount: Int,
    theme: ThemePalette,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Level Title & Subtitle + Combo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LEVEL PROGRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = theme.textSecondary.copy(alpha = 0.65f)
                )
                Text(
                    text = title.uppercase(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )
            }

            // Combo pill
            AnimatedVisibility(
                visible = comboCount > 1,
                enter = fadeIn() + scaleIn(tween(150)),
                exit = fadeOut() + scaleOut(tween(150))
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(theme.pillHighlight)
                        .border(1.dp, theme.primaryArrowColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "COMBO ×$comboCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = theme.primaryArrowColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stat Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Moves card
            StatPill(
                label = "MOVES",
                value = "$moves",
                subtext = "Par $parMoves",
                isHighlighted = false,
                theme = theme,
                modifier = Modifier.weight(1f)
            )

            // Remaining Arrows card
            StatPill(
                label = "REMAINING",
                value = "$remainingTiles",
                subtext = "of $totalTiles",
                isHighlighted = false,
                theme = theme,
                modifier = Modifier.weight(1f)
            )

            // Time card
            val minutes = timeSeconds / 60
            val seconds = timeSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)
            StatPill(
                label = "TIME",
                value = timeFormatted,
                subtext = null,
                isHighlighted = true,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = theme.primaryArrowColor,
                        modifier = Modifier.size(13.dp)
                    )
                },
                theme = theme,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Star prediction indicator based on current moves
        val starsExpected = when {
            moves <= parMoves -> 3
            moves <= parMoves + 2 -> 2
            else -> 1
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            for (i in 1..3) {
                val isEarned = i <= starsExpected
                Icon(
                    imageVector = if (isEarned) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (isEarned) Color(0xFFE6A700) else theme.textSecondary.copy(alpha = 0.25f),
                    modifier = Modifier.size(20.dp)
                )
                if (i < 3) Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    subtext: String?,
    theme: ThemePalette,
    isHighlighted: Boolean = false,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    val bg = if (isHighlighted) theme.pillHighlight else theme.pillBackground
    val borderCol = if (isHighlighted) theme.primaryArrowColor.copy(alpha = 0.25f) else theme.boardBorder.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = borderCol,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon?.invoke()
                if (icon != null) Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textSecondary.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = theme.textSecondary.copy(alpha = 0.6f)
                )
            }
        }
    }
}

