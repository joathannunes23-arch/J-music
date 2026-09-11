package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StemChannel
import com.example.ui.StemState
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary
import com.example.ui.util.rememberHapticFeedbackHelper
import kotlin.math.log10

@Composable
fun StemMixerCard(
    stemStates: Map<StemChannel, StemState>,
    isSeparating: Boolean,
    separationProgress: Float,
    separationStep: String,
    onStemVolumeChange: (StemChannel, Float) -> Unit,
    onToggleMute: (StemChannel) -> Unit,
    onToggleSolo: (StemChannel) -> Unit,
    onTriggerSeparation: () -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onApplyPreset: (String) -> Unit = {},
    onResetStems: () -> Unit = {},
    onOpenSetlists: () -> Unit = {},
    // Key Transposer Integration
    originalKey: String = "C Maior",
    transposedKey: String = "C Maior",
    transposeSemitones: Int = 0,
    onSetSemitones: (Int) -> Unit = {},
    // 5-Band Visual Equalizer Integration
    eqGainsDb: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f),
    activeEqPreset: String = "Flat",
    visualizerLevels: FloatArray = floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f),
    onEqGainChange: (Int, Float) -> Unit = { _, _ -> },
    onSelectEqPreset: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stem_mixer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoisesSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main Mixer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFF102038), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Mixer de Stems",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Mixer Visual & Estúdio J-MUSIC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Faders de Stems • Transposição em Tempo Real • Equalizador 5 Bandas",
                            fontSize = 11.sp,
                            color = MoisesTextSecondary
                        )
                    }
                }

                Button(
                    onClick = onTriggerSeparation,
                    enabled = !isSeparating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MoisesEmeraldDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("separate_stems_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSeparating) "Separando..." else "Separar IA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // AI Progress Bar if active
            AnimatedVisibility(visible = isSeparating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Color(0xFF0C1728), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF1D3760), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = separationStep,
                            fontSize = 12.sp,
                            color = MoisesEmeraldLight,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(separationProgress * 100).toInt()}%",
                            fontSize = 12.sp,
                            color = MoisesEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { separationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MoisesEmerald,
                        trackColor = Color(0xFF162944)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // 1. KEY TRANSPOSER (SELETOR DE TOM EM TEMPO REAL)
            // =========================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mixer_key_transposer"),
                color = Color(0xFF0A121F),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D324E))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header of Transposer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Transpositor de Tom",
                                tint = MoisesEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SELETOR DE TOM (KEY TRANSPOSER)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = MoisesEmeraldLight
                            )
                        }

                        // Real-time Badge
                        Surface(
                            color = Color(0xFF102844),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF224E82))
                        ) {
                            Text(
                                text = "⚡ Tempo Real • Todas as Tracks",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Key Comparison Display (Original -> Transposed)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF070C15), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF16263B), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Original Key
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "TOM ORIGINAL",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesTextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = originalKey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesTextSecondary
                            )
                        }

                        // Arrow
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MoisesEmerald,
                            modifier = Modifier.size(16.dp)
                        )

                        // Transposed Key
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "NOVO TOM ATIVO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesEmeraldLight,
                                letterSpacing = 0.5.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = transposedKey,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MoisesEmerald
                                )
                                if (transposeSemitones != 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = if (transposeSemitones > 0) Color(0xFF0F3A66) else Color(0xFF381C30),
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (transposeSemitones > 0) Color(0xFF2979FF) else Color(0xFFF43F5E)
                                        )
                                    ) {
                                        Text(
                                            text = if (transposeSemitones > 0) "+$transposeSemitones st" else "$transposeSemitones st",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (transposeSemitones > 0) Color(0xFF82B1FF) else Color(0xFFFF80AB),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step Controls & Semitone Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Descer Tom Button (-)
                        TactileAnimatedButton(
                            onClick = { onSetSemitones((transposeSemitones - 1).coerceAtLeast(-6)) },
                            backgroundColor = Color(0xFF142236),
                            borderColor = Color(0xFF1F3554),
                            cornerRadius = 8.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            testTag = "btn_transpose_down"
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Remove, contentDescription = "Descer Tom", tint = MoisesTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Descer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MoisesTextPrimary)
                            }
                        }

                        // Reset / Tom Original
                        TactileAnimatedButton(
                            onClick = { onSetSemitones(0) },
                            backgroundColor = if (transposeSemitones == 0) Color(0xFF162D4A) else Color(0xFF0F1826),
                            borderColor = if (transposeSemitones == 0) MoisesEmerald else Color(0xFF1F324E),
                            cornerRadius = 8.dp,
                            modifier = Modifier.height(38.dp),
                            testTag = "btn_transpose_reset"
                        ) {
                            Text(
                                text = if (transposeSemitones == 0) "Original" else "Resetar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (transposeSemitones == 0) MoisesEmeraldLight else MoisesTextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }

                        // Subir Tom Button (+)
                        TactileAnimatedButton(
                            onClick = { onSetSemitones((transposeSemitones + 1).coerceAtMost(6)) },
                            backgroundColor = Color(0xFF142236),
                            borderColor = Color(0xFF1F3554),
                            cornerRadius = 8.dp,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp),
                            testTag = "btn_transpose_up"
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = "Subir Tom", tint = MoisesTextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Subir", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MoisesTextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Semitones Chips (-6 to +6)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (st in -6..6) {
                            val isSelected = transposeSemitones == st
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSetSemitones(st) }
                                    .testTag("chip_st_$st"),
                                color = if (isSelected) MoisesEmerald else Color(0xFF0E1624),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) MoisesEmeraldLight else Color(0xFF1A2A40)
                                )
                            ) {
                                Text(
                                    text = if (st > 0) "+$st" else "$st",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MoisesTextSecondary,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // 2. EQUALIZADOR GRÁFICO DE 5 BANDAS COM RESPOSTA VISUAL AO ÁUDIO
            // =========================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mixer_graphic_equalizer"),
                color = Color(0xFF0A121F),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1D324E))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header of Equalizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Equalizer,
                                contentDescription = "Equalizador 5 Bandas",
                                tint = MoisesEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EQUALIZADOR GRÁFICO (5 BANDAS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = MoisesEmeraldLight
                            )
                        }

                        // Animated live audio indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        if (isPlaying) Color(0xFF00E5FF) else Color.Gray,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPlaying) "Áudio Ativo" else "Espectro Standby",
                                fontSize = 9.sp,
                                color = if (isPlaying) Color(0xFF00E5FF) else MoisesTextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5-Band Graphic Equalizer Visualizer: Dynamic Audio Spectrum + EQ Filter Response Curve
                    FiveBandGraphicEqCanvas(
                        gainsDb = eqGainsDb,
                        liveLevels = visualizerLevels,
                        isPlaying = isPlaying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF060B12))
                            .border(1.dp, Color(0xFF16253A), RoundedCornerShape(8.dp))
                            .padding(vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // EQ Presets Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Curvas:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextMuted
                        )

                        listOf("Flat", "Worship Atmosfera", "Vocal Louvor", "Acústico & Piano").forEach { preset ->
                            val isSelected = activeEqPreset == preset
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSelectEqPreset(preset) },
                                color = if (isSelected) Color(0xFF152A4A) else Color(0xFF0E1624),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) MoisesEmerald else Color(0xFF1A2A40)
                                )
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MoisesEmeraldLight else MoisesTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 5 Band Controls with individual visual response
                    val eqBandsMeta = listOf(
                        Triple(0, "60 Hz", "Sub / Kick"),
                        Triple(1, "250 Hz", "Low-Mid"),
                        Triple(2, "1 kHz", "Mid / Voz"),
                        Triple(3, "4 kHz", "Presence"),
                        Triple(4, "12 kHz", "Air / Brilho")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        eqBandsMeta.forEach { (bandIndex, freqLabel, desc) ->
                            val gain = eqGainsDb.getOrElse(bandIndex) { 0f }
                            val liveLevel = visualizerLevels.getOrElse(bandIndex) { 0.1f }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Freq label
                                Text(
                                    text = freqLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MoisesTextPrimary
                                )

                                // Gain readout
                                Text(
                                    text = String.format("%+.1f dB", gain),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when {
                                        gain > 0.1f -> MoisesEmeraldLight
                                        gain < -0.1f -> Color(0xFFFF8A80)
                                        else -> MoisesTextMuted
                                    }
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Multi-step LED VU indicator responding to live band audio
                                FiveBandVuMeter(
                                    level = liveLevel,
                                    isPlaying = isPlaying,
                                    accentColor = when (bandIndex) {
                                        0 -> Color(0xFF00E5FF)
                                        1 -> Color(0xFF00B0FF)
                                        2 -> MoisesEmeraldLight
                                        3 -> Color(0xFF69F0AE)
                                        else -> Color(0xFFB9F6CA)
                                    },
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height(20.dp)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Animated Tactile Slider for the band (-12dB to +12dB)
                                AnimatedMixerSlider(
                                    value = gain,
                                    onValueChange = { onEqGainChange(bandIndex, it) },
                                    valueRange = -12f..12f,
                                    activeColor = MoisesEmerald,
                                    inactiveColor = Color(0xFF142033),
                                    thumbColor = MoisesEmerald,
                                    testTag = "mixer_eq_slider_$bandIndex",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 1.dp)
                                )

                                Text(
                                    text = desc,
                                    fontSize = 8.sp,
                                    color = MoisesTextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // 3. STEM INSTRUMENT TRACKS (FAIXAS COM FADER, VU, MUTE & SOLO)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FAIXAS SEPARADAS (STEMS)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MoisesTextMuted
                )

                // Quick Presets Bar
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Padrão", "Sem Voz", "Acústico", "Base Forte").forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onApplyPreset(preset) },
                            color = Color(0xFF101B2B),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F324D))
                        ) {
                            Text(
                                text = preset,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MoisesEmeraldLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onResetStems() },
                        color = Color(0xFF141F30),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223552))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = "Resetar",
                                tint = MoisesTextMuted,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Reset",
                                fontSize = 9.sp,
                                color = MoisesTextMuted
                            )
                        }
                    }

                    // Setlist Manager Shortcut
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenSetlists() }
                            .testTag("mixer_open_setlists_button"),
                        color = Color(0xFF0F264A),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = "Setlist",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Setlist",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stem Channels with Visual Fader + VU Meter + Mute + Solo
            val hasActiveSolo = stemStates.values.any { it.isSolo }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StemChannel.entries.forEach { channel ->
                    val state = stemStates[channel] ?: StemState(channel)
                    StemFaderRow(
                        state = state,
                        hasActiveSolo = hasActiveSolo,
                        isPlaying = isPlaying,
                        onVolumeChange = { onStemVolumeChange(channel, it) },
                        onToggleMute = { onToggleMute(channel) },
                        onToggleSolo = { onToggleSolo(channel) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedMixerAudioSpectrum(
    levels: FloatArray,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val numBars = 28
        val totalWidth = size.width
        val barWidth = (totalWidth / numBars) * 0.70f
        val gap = (totalWidth / numBars) * 0.30f

        for (i in 0 until numBars) {
            val bandIdx = (i * levels.size / numBars).coerceIn(0, levels.size - 1)
            val baseLevel = levels[bandIdx]

            // Dynamic curve modulation across frequency spectrum
            val wave = 0.5f + 0.5f * kotlin.math.sin((i.toDouble() / numBars) * Math.PI).toFloat()
            val effectiveLevel = if (isPlaying) (baseLevel * wave * 1.4f) else 0.08f
            val heightFraction = effectiveLevel.coerceIn(0.06f, 0.98f)

            val barHeight = size.height * heightFraction
            val left = i * (barWidth + gap)
            val top = size.height - barHeight

            val brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00E5FF),
                    MoisesEmerald,
                    MoisesEmeraldDark
                ),
                startY = top,
                endY = size.height
            )

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(3f, 3f)
            )
        }
    }
}

@Composable
fun StemFaderRow(
    state: StemState,
    hasActiveSolo: Boolean,
    isPlaying: Boolean,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleSolo: () -> Unit
) {
    val channel = state.channel
    val accentColor = Color(channel.colorHex)

    // Check if channel is effectively audible
    val isAudible = if (hasActiveSolo) {
        state.isSolo && !state.isMuted
    } else {
        !state.isMuted
    }

    // Dynamic dB calculation for audio console realism
    val volumeDb = if (state.volume <= 0.001f) {
        "-∞"
    } else {
        val db = 20f * log10(state.volume)
        if (db >= -0.1f) "0.0 dB" else String.format("%.1f dB", db)
    }

    // Subtle VU meter oscillation when song is playing and channel audible
    val infiniteTransition = rememberInfiniteTransition(label = "vu_${channel.id}")
    val vuOscillation by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 380 + (channel.ordinal * 85),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "vu_anim"
    )

    val currentVuLevel = if (isPlaying && isAudible) {
        (state.volume * vuOscillation).coerceIn(0f, 1f)
    } else {
        0f
    }

    val cardBorderColor by animateColorAsState(
        targetValue = when {
            state.isSolo -> Color(0xFFFFB300)
            state.isMuted -> Color(0xFF381C1C)
            !isAudible -> Color(0xFF131A26)
            else -> Color(0xFF1E2D44)
        },
        label = "border_color"
    )

    Surface(
        color = MoisesSurfaceElevated,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon & Color Indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = if (isAudible) 0.20f else 0.08f), RoundedCornerShape(8.dp))
                    .border(1.dp, accentColor.copy(alpha = if (isAudible) 0.5f else 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = channel.icon, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Label, dB and percentage
            Column(modifier = Modifier.width(96.dp)) {
                Text(
                    text = channel.shortLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isAudible) MoisesTextMuted else MoisesTextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            state.isMuted -> "MUDO"
                            state.isSolo -> "SOLO"
                            else -> "${(state.volume * 100).toInt()}%"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            state.isMuted -> Color(0xFFFF5252)
                            state.isSolo -> Color(0xFFFFB300)
                            else -> accentColor
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = volumeDb,
                        fontSize = 9.sp,
                        color = MoisesTextMuted
                    )
                }
            }

            // Visual VU Meter (5 segment LED bar)
            Column(
                modifier = Modifier
                    .width(10.dp)
                    .height(28.dp)
                    .background(Color(0xFF090E17), RoundedCornerShape(2.dp))
                    .padding(1.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (step in 4 downTo 0) {
                    val threshold = (step + 1) / 5f
                    val isLit = currentVuLevel >= (threshold - 0.15f)
                    val segColor = when (step) {
                        4 -> Color(0xFFFF3B30) // Red clip
                        3 -> Color(0xFFFF9500) // Amber
                        else -> Color(0xFF00E5FF) // Blue / Cyan
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                if (isLit) segColor else segColor.copy(alpha = 0.15f),
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Animated Tactile Fader Slider with Haptics & Smooth Springs
            AnimatedMixerSlider(
                value = state.volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                activeColor = if (!isAudible) Color.DarkGray else accentColor,
                inactiveColor = Color(0xFF142236),
                thumbColor = if (!isAudible) Color.Gray else accentColor,
                testTag = "stem_slider_${channel.id}",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tactile Mute Button (M)
            TactileAnimatedButton(
                onClick = onToggleMute,
                backgroundColor = if (state.isMuted) Color(0xFFFF3B30) else Color(0xFF121E30),
                borderColor = if (state.isMuted) Color(0xFFFF5252) else Color(0xFF1E3250),
                cornerRadius = 8.dp,
                modifier = Modifier.size(30.dp),
                testTag = "mute_button_${channel.id}"
            ) {
                Text(
                    text = "M",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (state.isMuted) Color.White else MoisesTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Tactile Solo Button (S)
            TactileAnimatedButton(
                onClick = onToggleSolo,
                backgroundColor = if (state.isSolo) Color(0xFFFFB300) else Color(0xFF121E30),
                borderColor = if (state.isSolo) Color(0xFFFFD54F) else Color(0xFF1E3250),
                cornerRadius = 8.dp,
                modifier = Modifier.size(30.dp),
                testTag = "solo_button_${channel.id}"
            ) {
                Text(
                    text = "S",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (state.isSolo) Color(0xFF1A1200) else MoisesTextSecondary
                )
            }
        }
    }
}
