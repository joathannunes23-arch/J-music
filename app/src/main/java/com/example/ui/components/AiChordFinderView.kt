package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DetectedChord
import com.example.ui.AiDetectionState
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceVariant
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiChordFinderView(
    aiState: AiDetectionState,
    activePlayingChord: String?,
    onDetectClicked: () -> Unit,
    onChordClicked: (DetectedChord) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_chord_finder_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoisesSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(MoisesEmerald.copy(alpha = 0.4f), MoisesCardBorder)
            ),
            width = 1.2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
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
                            .background(MoisesSurfaceVariant, RoundedCornerShape(10.dp))
                            .border(1.dp, MoisesCardBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Chord Finder",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Chord Finder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Detecção Inteligente de Acordes (J-MUSIC)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoisesTextSecondary
                        )
                    }
                }

                Surface(
                    color = Color(0xFF102038),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A66))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(MoisesEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "IA STUDIO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmeraldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Action Button: "Detectar Acordes com IA"
            Button(
                onClick = onDetectClicked,
                enabled = !aiState.isDetecting,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoisesEmerald,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF121B2A),
                    disabledContentColor = MoisesTextSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .then(if (aiState.isDetecting) Modifier.scale(pulseScale) else Modifier)
                    .testTag("detectar_acordes_ia_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (aiState.isDetecting) Icons.Default.GraphicEq else Icons.Default.AutoAwesome,
                        contentDescription = "Detectar Acordes",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (aiState.isDetecting) "IA Processando Áudio..." else "Detectar Acordes com IA",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Progress bar & status
            AnimatedVisibility(visible = aiState.isDetecting, enter = fadeIn(), exit = fadeOut()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { aiState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MoisesEmerald,
                        trackColor = Color(0xFF1A2620)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = aiState.statusText,
                        fontSize = 11.sp,
                        color = MoisesEmeraldLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Summary row (e.g. C - G - D - Am - F)
            val result = aiState.result
            if (result != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progressão Harmônica Principal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MoisesTextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = Color(0xFF102038),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Tom: ${result.mainKey}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = Color(0xFF182226),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${result.bpm} BPM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast scrollable summary strip (e.g. C - G - D - Am - F)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    result.chordsSummary.forEach { chordName ->
                        val isCurrent = activePlayingChord == chordName
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onChordClicked(
                                        DetectedChord(
                                            id = 0,
                                            name = chordName,
                                            root = chordName.take(1),
                                            type = if (chordName.contains("m")) "Menor" else "Maior",
                                            timestampFormatted = "",
                                            section = "Cadência",
                                            colorHex = 0xFF00E676,
                                            isMinor = chordName.contains("m")
                                        )
                                    )
                                },
                            color = if (isCurrent) MoisesEmerald else Color(0xFF101B2B),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isCurrent) MoisesEmeraldLight else Color(0xFF1C2F48)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = chordName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCurrent) Color.White else MoisesTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Tocar",
                                    tint = if (isCurrent) Color.White else MoisesEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Acordes Detectados (Clique na tecla para ouvir o som real):",
                    fontSize = 12.sp,
                    color = MoisesTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Big colorful keys for each detected chord
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    result.detectedChords.forEach { chord ->
                        val chordColor = Color(chord.colorHex)
                        val isPlaying = activePlayingChord == chord.name

                        Surface(
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onChordClicked(chord) }
                                .testTag("chord_key_${chord.name}"),
                            color = if (isPlaying) chordColor else Color(0xFF0F1726),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isPlaying) 2.5.dp else 1.5.dp,
                                if (isPlaying) Color.White else chordColor.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = chord.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isPlaying) Color.Black else Color.White
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Surface(
                                    color = if (isPlaying) Color.Black.copy(alpha = 0.2f) else chordColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = chord.type.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPlaying) Color.Black else chordColor,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }

                                if (chord.timestampFormatted.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${chord.section} • ${chord.timestampFormatted}",
                                        fontSize = 9.sp,
                                        color = if (isPlaying) Color.Black.copy(alpha = 0.7f) else MoisesTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty state friendly suggestion
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C1320), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1B2C44), RoundedCornerShape(12.dp))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MoisesTextMuted,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhum acorde detectado ainda.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MoisesTextSecondary
                        )
                        Text(
                            text = "Clique no botão acima para a IA analisar as frequências da música.",
                            fontSize = 11.sp,
                            color = MoisesTextMuted
                        )
                    }
                }
            }
        }
    }
}
