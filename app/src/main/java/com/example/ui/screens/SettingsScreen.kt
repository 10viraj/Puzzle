package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArrowDirection
import com.example.model.ThemePalette
import com.example.ui.components.ArrowGraphic
import com.example.viewmodel.GameUiState

@Composable
fun SettingsScreen(
    state: GameUiState,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onSelectTheme: (ThemePalette) -> Unit,
    onResetAllProgress: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = state.selectedTheme
    val scrollState = rememberScrollState()
    var showResetConfirmation by remember { mutableStateOf(false) }

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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(theme.pillBackground)
                        .border(1.dp, theme.boardBorder, CircleShape)
                        .testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.textPrimary
                    )
                }

                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = theme.textPrimary
                )

                Spacer(modifier = Modifier.size(42.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Audio & Haptics Section
            Text(
                text = "AUDIO & HAPTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = theme.textSecondary.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = theme.tileBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Sound FX Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = theme.primaryArrowColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Sound Effects",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary
                                )
                                Text(
                                    text = "Arrow slides, clicks, and fanfares",
                                    fontSize = 12.sp,
                                    color = theme.textSecondary
                                )
                            }
                        }

                        Switch(
                            checked = state.soundEnabled,
                            onCheckedChange = { onToggleSound() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = theme.primaryArrowColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Haptics Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = theme.secondaryArrowColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Haptic Vibrations",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = theme.textPrimary
                                )
                                Text(
                                    text = "Tactile bump on block or escape",
                                    fontSize = 12.sp,
                                    color = theme.textSecondary
                                )
                            }
                        }

                        Switch(
                            checked = state.hapticsEnabled,
                            onCheckedChange = { onToggleHaptics() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = theme.primaryArrowColor
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Themes Section
            Text(
                text = "BOARD THEMES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = theme.textSecondary.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemePalette.values().forEach { palette ->
                    val isSelected = state.selectedTheme == palette
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(palette.tileBackground)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) palette.primaryArrowColor else palette.boardBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(bounded = true, color = palette.primaryArrowColor),
                                onClick = { onSelectTheme(palette) }
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Color swatches row
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(palette.primaryArrowColor)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(palette.secondaryArrowColor)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(palette.pillHighlight)
                                            .border(0.5.dp, palette.primaryArrowColor.copy(alpha = 0.5f), CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = palette.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = palette.textPrimary
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(palette.primaryArrowColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // How to Play Card
            Text(
                text = "HOW TO PLAY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = theme.textSecondary.copy(alpha = 0.7f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = theme.tileBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, theme.boardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RuleItem(
                        number = "1",
                        title = "Tap to Move",
                        desc = "Tap any arrow to launch it forward in the direction it points.",
                        theme = theme
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleItem(
                        number = "2",
                        title = "Clear Path Required",
                        desc = "Arrows only exit if no other arrow blocks the path to the board edge.",
                        theme = theme
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleItem(
                        number = "3",
                        title = "Clear the Board",
                        desc = "Plan your sequence carefully to untangle the board in the fewest moves!",
                        theme = theme
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reset Game Progress Button
            Button(
                onClick = { showResetConfirmation = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBA1A1A).copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBA1A1A).copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("settings_reset_progress_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color(0xFFBA1A1A),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reset All Progress",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA1A1A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // Reset Confirmation Dialog
        if (showResetConfirmation) {
            AlertDialog(
                onDismissRequest = { showResetConfirmation = false },
                title = {
                    Text(
                        text = "Reset Progress?",
                        fontWeight = FontWeight.Black,
                        color = theme.textPrimary
                    )
                },
                text = {
                    Text(
                        text = "This will erase all completed levels, stars, high scores, and stats. This cannot be undone.",
                        color = theme.textSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onResetAllProgress()
                            showResetConfirmation = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBA1A1A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Everything", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showResetConfirmation = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", color = theme.textPrimary)
                    }
                },
                containerColor = theme.tileBackground,
                shape = RoundedCornerShape(22.dp)
            )
        }
    }
}

@Composable
fun RuleItem(number: String, title: String, desc: String, theme: ThemePalette) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(theme.pillBackground)
                .border(0.5.dp, theme.boardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = theme.textPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = theme.textSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
