package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary
import com.example.ui.util.rememberHapticFeedbackHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TapTempoCard(
    currentBpm: Int,
    tappedBpm: Int?,
    isMetronomeFlash: Boolean,
    onRecordTap: () -> Unit,
    onAdjustBpm: (Int) -> Unit,
    onApplyBpm: () -> Unit,
    modifier: Modifier = Modifier,
    // Enhanced Visual Metronome with Time Signatures and Strong Beat Indication
    timeSignature: String = "4/4",
    currentBeatIndex: Int = 1,
    isStrongBeat: Boolean = false,
    isMetronomeHapticEnabled: Boolean = true,
    onTimeSignatureChange: (String) -> Unit = {},
    onToggleMetronomeHaptic: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isTapButtonPressed by remember { mutableStateOf(false) }
    val haptic = rememberHapticFeedbackHelper()

    // Trigger rhythmic haptic feedback on beat pulse if enabled
    LaunchedEffect(isMetronomeFlash, isStrongBeat) {
        if (isMetronomeFlash && isMetronomeHapticEnabled) {
            haptic.performBeatPulse(isDownbeat = isStrongBeat)
        }
    }

    val tapBtnBgColor by animateColorAsState(
        targetValue = if (isTapButtonPressed) MoisesEmeraldLight else MoisesEmeraldDark,
        animationSpec = tween(durationMillis = 120),
        label = "tap_color"
    )

    val beatsInBar = when (timeSignature) {
        "3/4" -> 3
        "6/8" -> 6
        else -> 4
    }

    val worshipTempoDescription = when {
        currentBpm < 60 -> "Largo • Lento & Intimista"
        currentBpm in 60..74 -> "Adagio • Balada Suave"
        currentBpm in 75..90 -> "Andante • Ritmo Moderado"
        currentBpm in 91..115 -> "Moderato • Andamento Médio"
        else -> "Allegro • Ritmo Rápido & Enérgico"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tap_tempo_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoisesSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF102038), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Tap Tempo",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Metrônomo Visual & Tap Tempo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Compasso $timeSignature com indicação de tempo forte",
                            fontSize = 11.sp,
                            color = MoisesEmeraldLight
                        )
                    }
                }

                // Haptic Toggle Button
                TactileAnimatedButton(
                    onClick = onToggleMetronomeHaptic,
                    backgroundColor = if (isMetronomeHapticEnabled) Color(0xFF133320) else Color(0xFF131D2E),
                    borderColor = if (isMetronomeHapticEnabled) MoisesEmerald else Color(0xFF1F324E),
                    cornerRadius = 8.dp,
                    testTag = "metronome_haptic_toggle"
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibração Tátil",
                            tint = if (isMetronomeHapticEnabled) MoisesEmerald else MoisesTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMetronomeHapticEnabled) "Tátil ON" else "Tátil OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMetronomeHapticEnabled) MoisesEmeraldLight else MoisesTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // VISUAL METRONOME: TIME SIGNATURE SELECTOR + BEAT DOTS (STRONG BEAT ACCENT)
            // =========================================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF09121F),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1C2E49))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ASSINATURA DE TEMPO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextMuted,
                            letterSpacing = 0.5.sp
                        )

                        // Selectors for 4/4, 3/4, 6/8
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("4/4", "3/4", "6/8").forEach { sig ->
                                val isSelected = sig == timeSignature
                                TactileAnimatedButton(
                                    onClick = { onTimeSignatureChange(sig) },
                                    backgroundColor = if (isSelected) MoisesEmeraldDark else Color(0xFF121F33),
                                    borderColor = if (isSelected) MoisesEmerald else Color(0xFF203657),
                                    cornerRadius = 6.dp,
                                    testTag = "time_signature_$sig"
                                ) {
                                    Text(
                                        text = sig,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MoisesTextSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rhythmic Beat Dots Display (Step 1 is Strong Downbeat / Tempo Forte)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (beat in 1..beatsInBar) {
                            val isCurrentBeat = isMetronomeFlash && (currentBeatIndex == beat)
                            val isDownbeat = (beat == 1)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Animated visual light for this beat
                                Box(
                                    modifier = Modifier
                                        .size(if (isDownbeat) 32.dp else 26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCurrentBeat && isDownbeat -> Color(0xFFFF3B30) // Strong Beat Red/Accent
                                                isCurrentBeat -> MoisesEmerald // Regular beat emerald
                                                isDownbeat -> Color(0xFF331515) // Inactive strong
                                                else -> Color(0xFF101B2B) // Inactive normal
                                            }
                                        )
                                        .border(
                                            width = if (isDownbeat) 2.dp else 1.dp,
                                            color = when {
                                                isCurrentBeat && isDownbeat -> Color(0xFFFF6961)
                                                isCurrentBeat -> MoisesEmeraldLight
                                                isDownbeat -> Color(0xFF662222)
                                                else -> Color(0xFF1F324E)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$beat",
                                        fontSize = if (isDownbeat) 13.sp else 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            isCurrentBeat -> Color.White
                                            isDownbeat -> Color(0xFFFF8A80)
                                            else -> MoisesTextMuted
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isDownbeat) "FORTE" else "fraco",
                                    fontSize = 8.sp,
                                    fontWeight = if (isDownbeat) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDownbeat) Color(0xFFFF8A80) else MoisesTextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Content: Tap Button + Digital BPM readout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BIG TACTILE TAP TEMPO BUTTON
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(tapBtnBgColor, Color(0xFF0A1830))
                            )
                        )
                        .border(3.dp, MoisesEmerald, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            haptic.performClick()
                            isTapButtonPressed = true
                            onRecordTap()
                            coroutineScope.launch {
                                delay(120)
                                isTapButtonPressed = false
                            }
                        }
                        .testTag("tap_tempo_big_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TAP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isTapButtonPressed) Color.White else MoisesTextPrimary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "TEMPO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTapButtonPressed) Color.White else MoisesEmeraldLight,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Digital Display & Stepper
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color(0xFF0C1320),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$currentBpm",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = MoisesEmeraldLight,
                                lineHeight = 38.sp
                            )
                            Text(
                                text = "BPM (BATIDAS POR MINUTO)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesTextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = worshipTempoDescription,
                                fontSize = 10.sp,
                                color = MoisesTextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stepper row with tactile feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TactileAnimatedButton(
                            onClick = { onAdjustBpm(-5) },
                            backgroundColor = MoisesSurfaceElevated,
                            borderColor = Color(0xFF223550),
                            cornerRadius = 8.dp,
                            modifier = Modifier.size(32.dp),
                            testTag = "bpm_minus_5"
                        ) {
                            Text("-5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MoisesTextPrimary)
                        }

                        TactileAnimatedButton(
                            onClick = { onAdjustBpm(-1) },
                            backgroundColor = MoisesSurfaceElevated,
                            borderColor = Color(0xFF223550),
                            cornerRadius = 8.dp,
                            modifier = Modifier.size(32.dp),
                            testTag = "bpm_minus_1"
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "-1", tint = MoisesTextPrimary, modifier = Modifier.size(16.dp))
                        }

                        TactileAnimatedButton(
                            onClick = { onAdjustBpm(+1) },
                            backgroundColor = MoisesSurfaceElevated,
                            borderColor = Color(0xFF223550),
                            cornerRadius = 8.dp,
                            modifier = Modifier.size(32.dp),
                            testTag = "bpm_plus_1"
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "+1", tint = MoisesTextPrimary, modifier = Modifier.size(16.dp))
                        }

                        TactileAnimatedButton(
                            onClick = { onAdjustBpm(+5) },
                            backgroundColor = MoisesSurfaceElevated,
                            borderColor = Color(0xFF223550),
                            cornerRadius = 8.dp,
                            modifier = Modifier.size(32.dp),
                            testTag = "bpm_plus_5"
                        ) {
                            Text("+5", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MoisesTextPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apply Button
            TactileAnimatedButton(
                onClick = onApplyBpm,
                backgroundColor = Color(0xFF0F264A),
                borderColor = Color(0xFF1D467E),
                cornerRadius = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                testTag = "apply_tapped_bpm_button"
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MoisesEmerald, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fixar $currentBpm BPM na Música", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MoisesEmeraldLight)
                }
            }
        }
    }
}
