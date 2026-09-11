package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

/**
 * High-precision 5-band Equalizer Visualizer Component.
 * Features:
 * - Dynamic real-time response to audio playing in the mixer.
 * - Graphic response curve linking the 5 frequency band control nodes (60Hz, 250Hz, 1kHz, 4kHz, 12kHz).
 * - Multi-segment VU meters for each band responding to instantaneous energy.
 * - Smooth tactile animated faders with haptic feedback.
 */
data class GraphicEqBandDef(
    val index: Int,
    val centerFreq: String,
    val bandName: String,
    val role: String,
    val accentColor: Color
)

val GRAPHIC_EQ_5_BANDS = listOf(
    GraphicEqBandDef(0, "60 Hz", "Sub-Bass", "Bumbo & Baixo Sub", Color(0xFF00E5FF)),
    GraphicEqBandDef(1, "250 Hz", "Low-Mid", "Corpo & Bateria", Color(0xFF00B0FF)),
    GraphicEqBandDef(2, "1 kHz", "Mid-Range", "Voz Principal & Teclado", MoisesEmeraldLight),
    GraphicEqBandDef(3, "4 kHz", "Presence", "Ataque & Clareza", Color(0xFF69F0AE)),
    GraphicEqBandDef(4, "12 kHz", "Air / High", "Pratos & Shimmer", Color(0xFFB9F6CA))
)

@Composable
fun FiveBandGraphicEqVisualizer(
    eqGainsDb: FloatArray,
    visualizerLevels: FloatArray,
    isPlaying: Boolean,
    onEqGainChange: (Int, Float) -> Unit,
    activePreset: String = "Flat",
    onSelectPreset: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("five_band_graphic_eq_visualizer"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoisesSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Live Analyzer Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF00B0FF), Color(0xFF00E676))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Equalizador Gráfico 5 Bandas",
                            tint = Color(0xFF04101E),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "EQUALIZADOR GRÁFICO 5 BANDAS",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = MoisesTextPrimary
                            )
                        }
                        Text(
                            text = "Resposta visual dinâmica em tempo real (-12dB a +12dB)",
                            fontSize = 11.sp,
                            color = MoisesTextSecondary
                        )
                    }
                }

                // Live Audio Status Badge
                Surface(
                    color = if (isPlaying) Color(0xFF072418) else Color(0xFF121B29),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isPlaying) MoisesEmerald else Color(0xFF1F2F45)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isPlaying) MoisesEmerald else Color(0xFF607D8B),
                                    CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPlaying) "RTA ATIVO" else "STANDBY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) MoisesEmeraldLight else MoisesTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------------------
            // Combined RTA & Frequency Response Curve Canvas
            // -------------------------------------------------------------------------
            FiveBandGraphicEqCanvas(
                gainsDb = eqGainsDb,
                liveLevels = visualizerLevels,
                isPlaying = isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF060B12))
                    .border(1.dp, Color(0xFF16253A), RoundedCornerShape(12.dp))
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Preset Quick Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Flat", "Worship Atmosfera", "Vocal Louvor", "Acústico & Piano").forEach { preset ->
                    val isSelected = activePreset == preset
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectPreset(preset) }
                            .testTag("eq_preset_$preset"),
                        color = if (isSelected) Color(0xFF102844) else Color(0xFF0A121F),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MoisesEmerald else Color(0xFF192A40)
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MoisesEmeraldLight else MoisesTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // -------------------------------------------------------------------------
            // 5 Band Individual Controls with Multi-Segment LED VU meter & Animated Slider
            // -------------------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GRAPHIC_EQ_5_BANDS.forEach { band ->
                    val gain = eqGainsDb.getOrElse(band.index) { 0f }
                    val liveLevel = visualizerLevels.getOrElse(band.index) { 0.1f }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Frequency Label
                        Text(
                            text = band.centerFreq,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )

                        // Gain dB readout
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

                        // 6-Step Vertical LED VU Meter
                        FiveBandVuMeter(
                            level = liveLevel,
                            isPlaying = isPlaying,
                            accentColor = band.accentColor,
                            modifier = Modifier
                                .width(22.dp)
                                .height(26.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Tactile Smooth Animated Slider
                        AnimatedMixerSlider(
                            value = gain,
                            onValueChange = { onEqGainChange(band.index, it) },
                            valueRange = -12f..12f,
                            activeColor = band.accentColor,
                            inactiveColor = Color(0xFF142033),
                            thumbColor = band.accentColor,
                            testTag = "graphic_eq_band_slider_${band.index}",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = band.bandName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = MoisesTextSecondary,
                            maxLines = 1
                        )
                        Text(
                            text = band.role,
                            fontSize = 8.sp,
                            color = MoisesTextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 6-segment LED VU Meter for real-time frequency band response.
 */
@Composable
fun FiveBandVuMeter(
    level: Float,
    isPlaying: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color(0xFF09101A), RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF162338), RoundedCornerShape(4.dp))
            .padding(2.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (step in 5 downTo 0) {
            val thresh = (step + 1) / 6f
            val isLit = isPlaying && level >= thresh
            val stepColor = when {
                !isLit -> Color(0xFF101B2B)
                step >= 5 -> Color(0xFFFF5252) // Peak / Red
                step >= 4 -> Color(0xFFFFD740) // High / Yellow
                else -> accentColor           // Nominal / Band Color
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 0.5.dp)
                    .background(stepColor, RoundedCornerShape(1.dp))
            )
        }
    }
}

/**
 * Custom Canvas drawing both:
 * 1. Background real-time audio spectrum bars (RTA) driven by audio engine.
 * 2. Foreground smooth EQ filter response curve connecting the 5 bands.
 */
@Composable
fun FiveBandGraphicEqCanvas(
    gainsDb: FloatArray,
    liveLevels: FloatArray,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        // Draw 0dB Reference Line
        drawLine(
            color = Color(0xFF1E3250),
            start = Offset(0f, midY),
            end = Offset(width, midY),
            strokeWidth = 1f
        )
        // Draw +6dB and -6dB Grid Lines
        val quarterH = height / 4f
        drawLine(
            color = Color(0xFF121F33),
            start = Offset(0f, quarterH),
            end = Offset(width, quarterH),
            strokeWidth = 0.5f
        )
        drawLine(
            color = Color(0xFF121F33),
            start = Offset(0f, height - quarterH),
            end = Offset(width, height - quarterH),
            strokeWidth = 0.5f
        )

        // ---------------------------------------------------------------------
        // 1. Draw 20 RTA audio energy bars in background
        // ---------------------------------------------------------------------
        val numBars = 20
        val barWidth = (width / numBars) * 0.7f
        val barGap = (width / numBars) * 0.3f

        for (i in 0 until numBars) {
            val bandIdx = (i * liveLevels.size / numBars).coerceIn(0, liveLevels.size - 1)
            val baseLevel = if (isPlaying) liveLevels[bandIdx] else 0.05f

            // Add wave curve modulation across spectrum
            val wave = 0.4f + 0.6f * kotlin.math.sin((i.toDouble() / numBars) * Math.PI).toFloat()
            val barH = (height * baseLevel * wave * 0.85f).coerceIn(4f, height - 6f)

            val left = i * (barWidth + barGap) + (barGap / 2f)
            val top = height - barH

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MoisesEmerald.copy(alpha = 0.65f),
                        Color(0xFF00B0FF).copy(alpha = 0.4f),
                        Color(0xFF08182B).copy(alpha = 0.2f)
                    ),
                    startY = top,
                    endY = height
                ),
                topLeft = Offset(left, top),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }

        // ---------------------------------------------------------------------
        // 2. Draw Smooth 5-Band Filter Response Curve
        // ---------------------------------------------------------------------
        val bandPoints = mutableListOf<Offset>()
        for (i in 0 until 5) {
            val x = (i + 0.5f) * (width / 5f)
            val gain = gainsDb.getOrElse(i) { 0f }.coerceIn(-12f, 12f)
            // gain = +12dB -> y = 6f
            // gain = 0dB   -> y = midY
            // gain = -12dB -> y = height - 6f
            val y = midY - (gain / 12f) * (midY - 8f)
            bandPoints.add(Offset(x, y))
        }

        // Build smooth Catmull-Rom / Bezier Path
        val curvePath = Path().apply {
            moveTo(0f, bandPoints.first().y)
            lineTo(bandPoints.first().x, bandPoints.first().y)

            for (i in 0 until bandPoints.size - 1) {
                val p0 = bandPoints[i]
                val p1 = bandPoints[i + 1]
                val ctrl1X = (p0.x + p1.x) / 2f
                val ctrl1Y = p0.y
                val ctrl2X = (p0.x + p1.x) / 2f
                val ctrl2Y = p1.y
                cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, p1.x, p1.y)
            }

            lineTo(width, bandPoints.last().y)
        }

        // Draw EQ curve stroke
        drawPath(
            path = curvePath,
            color = Color(0xFF00E5FF),
            style = Stroke(width = 2.5f)
        )

        // Draw glowing control nodes for each band
        bandPoints.forEachIndexed { idx, pt ->
            val liveLevel = if (isPlaying) liveLevels.getOrElse(idx) { 0.1f } else 0.1f
            val haloRadius = 4f + liveLevel * 5f

            // Pulsing halo
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                radius = haloRadius,
                center = pt
            )
            // Center node
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = pt
            )
        }
    }
}
