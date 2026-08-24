package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemePalette

@Composable
fun GameControls(
    canUndo: Boolean,
    hintsRemaining: Int,
    theme: ThemePalette,
    onUndoClick: () -> Unit,
    onResetClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Exit Pill Button
        Box(
            modifier = Modifier
                .size(54.dp)
                .testTag("control_back_button")
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(20.dp))
                .background(theme.pillBackground)
                .border(1.dp, theme.boardBorder, RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = theme.textPrimary),
                    onClick = onBackClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Exit",
                tint = theme.textPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        // Undo Pill Button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .testTag("control_undo_button")
                .alpha(if (canUndo) 1f else 0.45f)
                .shadow(elevation = if (canUndo) 2.dp else 0.dp, shape = RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(20.dp))
                .background(theme.pillBackground)
                .border(1.dp, theme.boardBorder, RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = theme.textPrimary),
                    enabled = canUndo,
                    onClick = onUndoClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Undo,
                    contentDescription = "Undo",
                    tint = theme.textPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "UNDO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = theme.textPrimary
                )
            }
        }

        // Reset Pill Button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .testTag("control_reset_button")
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                .clip(RoundedCornerShape(20.dp))
                .background(theme.pillBackground)
                .border(1.dp, theme.boardBorder, RoundedCornerShape(20.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = theme.textPrimary),
                    onClick = onResetClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = theme.textPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RESET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = theme.textPrimary
                )
            }
        }

        // Get Hint Action Button (Primary Highlight)
        Box(
            modifier = Modifier
                .weight(1.35f)
                .height(54.dp)
                .testTag("control_hint_button")
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    spotColor = theme.primaryArrowColor.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(20.dp))
                .background(theme.primaryArrowColor)
                .border(
                    width = 1.dp,
                    color = theme.primaryArrowColor.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = Color.White),
                    onClick = onHintClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Hint",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hintsRemaining > 0) "HINT ($hintsRemaining)" else "GET HINT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}

