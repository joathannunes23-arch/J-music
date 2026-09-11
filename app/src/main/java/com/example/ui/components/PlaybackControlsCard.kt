package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PlaybackState
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceVariant
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@Composable
fun PlaybackControlsCard(
    playbackState: PlaybackState,
    swellPedalLevel: Float,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Int) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    fun formatTime(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return String.format("%02d:%02d", m, s)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("playback_controls_card"),
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
                Text(
                    text = "Modo Playback & Estúdio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MoisesTextPrimary
                )

                // Swell indicator badge
                Surface(
                    color = Color(0xFF102038),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A66))
                ) {
                    Text(
                        text = "Swell: ${(swellPedalLevel * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmeraldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Waveform simulation bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(Color(0xFF090E17), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val barCount = 28
                val currentFraction = if (playbackState.durationSec > 0)
                    playbackState.currentPositionSec.toFloat() / playbackState.durationSec.toFloat()
                else 0f

                for (i in 0 until barCount) {
                    val barFraction = i.toFloat() / barCount
                    val isPast = barFraction <= currentFraction
                    val heightRatio = (0.25f + 0.75f * kotlin.math.sin(i * 0.7f).let { kotlin.math.abs(it) })

                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height((32 * heightRatio).dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isPast) MoisesEmerald
                                else if (playbackState.isPlaying) Color(0xFF1E3250)
                                else Color(0xFF121B2B)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Slider
            Slider(
                value = playbackState.currentPositionSec.toFloat(),
                onValueChange = { onSeek(it.toInt()) },
                valueRange = 0f..(playbackState.durationSec.toFloat().coerceAtLeast(1f)),
                colors = SliderDefaults.colors(
                    thumbColor = MoisesEmerald,
                    activeTrackColor = MoisesEmerald,
                    inactiveTrackColor = Color(0xFF142236)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("playback_seek_slider")
            )

            // Time stamps: current and total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(playbackState.currentPositionSec),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MoisesEmeraldLight
                )
                Text(
                    text = formatTime(playbackState.durationSec),
                    fontSize = 12.sp,
                    color = MoisesTextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Playback Buttons: Stop, Play/Pause
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stop Button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFF101B2B), CircleShape)
                        .border(1.dp, Color(0xFF1F324D), CircleShape)
                        .clickable { onStop() }
                        .testTag("playback_stop_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Parar",
                        tint = MoisesTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(22.dp))

                // Big Center Play / Pause Button
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(12.dp, CircleShape, spotColor = MoisesEmerald)
                        .background(MoisesEmerald, CircleShape)
                        .clickable { onTogglePlayPause() }
                        .testTag("playback_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playbackState.isPlaying) "Pausar" else "Tocar",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volume Master Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = "Volume Baixo",
                    tint = MoisesTextMuted,
                    modifier = Modifier.size(20.dp)
                )

                Slider(
                    value = playbackState.masterVolume,
                    onValueChange = onVolumeChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MoisesTextPrimary,
                        activeTrackColor = MoisesEmerald,
                        inactiveTrackColor = Color(0xFF142236)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("master_volume_slider")
                )

                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume Alto",
                    tint = MoisesEmerald,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${(playbackState.masterVolume * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoisesTextPrimary
                )
            }
        }
    }
}
