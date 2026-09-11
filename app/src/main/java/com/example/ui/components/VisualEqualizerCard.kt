package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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

data class EqBandInfo(
    val index: Int,
    val label: String,
    val freqLabel: String,
    val desc: String
)

val EQ_BANDS = listOf(
    EqBandInfo(0, "Sub", "60 Hz", "Graves / Kick"),
    EqBandInfo(1, "Low-Mid", "250 Hz", "Corpo / Calor"),
    EqBandInfo(2, "Mid", "1 kHz", "Voz / Presença"),
    EqBandInfo(3, "Presence", "4 kHz", "Ataque / Definição"),
    EqBandInfo(4, "Air", "12 kHz", "Shimmer / Brilho")
)

val PRESETS = listOf(
    "Flat",
    "Worship Atmosfera",
    "Vocal Louvor",
    "Acústico & Piano"
)

@Composable
fun VisualEqualizerCard(
    eqGainsDb: FloatArray,
    activePreset: String,
    visualizerLevels: FloatArray,
    onGainChange: (Int, Float) -> Unit,
    onSelectPreset: (String) -> Unit,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false
) {
    FiveBandGraphicEqVisualizer(
        eqGainsDb = eqGainsDb,
        visualizerLevels = visualizerLevels,
        isPlaying = isPlaying,
        onEqGainChange = onGainChange,
        activePreset = activePreset,
        onSelectPreset = onSelectPreset,
        modifier = modifier
    )
}

@Composable
fun AnimatedAudioSpectrum(
    levels: FloatArray,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val numBars = 24
        val totalWidth = size.width
        val barWidth = (totalWidth / numBars) * 0.7f
        val gap = (totalWidth / numBars) * 0.3f

        for (i in 0 until numBars) {
            val bandIdx = (i * levels.size / numBars).coerceIn(0, levels.size - 1)
            val baseLevel = levels[bandIdx]

            // Add wave curve modulation across spectrum
            val wave = 0.5f + 0.5f * kotlin.math.sin((i.toDouble() / numBars) * Math.PI).toFloat()
            val heightFraction = (baseLevel * wave * 1.3f).coerceIn(0.08f, 0.95f)

            val barHeight = size.height * heightFraction
            val left = i * (barWidth + gap)
            val top = size.height - barHeight

            val brush = Brush.verticalGradient(
                colors = listOf(
                    MoisesEmerald,
                    MoisesEmeraldDark,
                    Color(0xFF0D2545)
                ),
                startY = top,
                endY = size.height
            )

            drawRoundRect(
                brush = brush,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
