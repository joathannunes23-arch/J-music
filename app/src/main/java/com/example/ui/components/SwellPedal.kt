package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesMint
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceVariant
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@Composable
fun SwellPedal(
    swellLevel: Float,
    isAutoSwell: Boolean,
    onSwellChange: (Float) -> Unit,
    onToggleAutoSwell: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedSwell by animateFloatAsState(
        targetValue = swellLevel,
        animationSpec = tween(durationMillis = 60),
        label = "swellLevelAnim"
    )

    val ledGlowColor by animateColorAsState(
        targetValue = if (animatedSwell > 0.05f) MoisesEmerald else Color(0xFF152A4A),
        label = "ledColorAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pedal_volume_swell_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MoisesSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                colors = listOf(MoisesEmerald.copy(alpha = 0.5f), MoisesCardBorder)
            ),
            width = 1.2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Title + LED indicator
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
                            imageVector = Icons.Default.Waves,
                            contentDescription = "Swell Icon",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pedal de Volume / Swell",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Fade In/Out Suave & Ambient Pad",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoisesTextSecondary
                        )
                    }
                }

                // Worship Glowing LED
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .shadow(
                                elevation = (animatedSwell * 12).dp,
                                shape = CircleShape,
                                spotColor = MoisesEmerald,
                                ambientColor = MoisesEmerald
                            )
                            .background(
                                color = ledGlowColor,
                                shape = CircleShape
                            )
                            .border(2.dp, Color(0xFF1E3A66), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (animatedSwell > 0.1f) Color.White.copy(alpha = animatedSwell) else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "LED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (animatedSwell > 0.1f) MoisesEmeraldLight else MoisesTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Physical Pedal Body with Tread and Vertical Swell Slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(Color(0xFF0A1019), RoundedCornerShape(16.dp))
                    .border(1.5.dp, Color(0xFF1B2C44), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: Physical Pedal Tread Plate Visual
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF131D2E), Color(0xFF0B111A))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, Color(0xFF1E3250), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stepY = size.height / 12f
                        // Draw metallic pedal treads
                        for (i in 1..11) {
                            val y = i * stepY
                            // Shadow line
                            drawLine(
                                color = Color(0xFF060A10),
                                start = Offset(16f, y + 2f),
                                end = Offset(size.width - 16f, y + 2f),
                                strokeWidth = 5f
                            )
                            // Rubber grip highlight
                            drawLine(
                                color = if (i.toFloat() / 11f <= animatedSwell) MoisesEmerald.copy(alpha = 0.6f) else Color(0xFF1B2C44),
                                start = Offset(16f, y),
                                end = Offset(size.width - 16f, y),
                                strokeWidth = 3f
                            )
                        }

                        // Center metallic logo strip
                        drawRoundRect(
                            color = Color(0xFF111C2E),
                            topLeft = Offset(size.width * 0.35f, size.height * 0.40f),
                            size = androidx.compose.ui.geometry.Size(size.width * 0.30f, size.height * 0.20f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                        )
                    }

                    // Pedal text in center
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "J-MUSIC",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = MoisesEmeraldLight
                        )
                        Text(
                            text = "EXPRESSION",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MoisesTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Right side: Vertical Swell Slider with readout
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Level Readout
                    Surface(
                        color = Color(0xFF0E1726),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
                    ) {
                        Text(
                            text = "${(animatedSwell * 100).toInt()}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MoisesEmerald,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Vertical Slider representation
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Vertical track
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxHeight()
                                .background(Color(0xFF080D14), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFF18263A), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Active level fill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(animatedSwell.coerceIn(0.02f, 1f))
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(MoisesEmerald, MoisesMint)
                                        ),
                                        shape = RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)
                                    )
                            )
                        }
                    }

                    // Quick presets: 0%, 50%, 100%
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(0f to "0", 0.5f to "50", 1f to "MAX").forEach { (valLevel, label) ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSwellChange(valLevel) },
                                color = if (kotlin.math.abs(swellLevel - valLevel) < 0.1f) MoisesEmerald.copy(alpha = 0.2f) else Color(0xFF0F1726),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (kotlin.math.abs(swellLevel - valLevel) < 0.1f) MoisesEmerald else MoisesTextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Slider for precise touch control
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Controle Contínuo do Swell",
                        style = MaterialTheme.typography.bodySmall,
                        color = MoisesTextSecondary
                    )
                    Text(
                        text = if (animatedSwell < 0.1f) "Fechado (Mudo)" else if (animatedSwell > 0.9f) "Aberto (Full)" else "Swell Moderado",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MoisesEmerald
                    )
                }

                Slider(
                    value = swellLevel,
                    onValueChange = onSwellChange,
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MoisesEmerald,
                        activeTrackColor = MoisesEmerald,
                        inactiveTrackColor = Color(0xFF141F30)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("swell_pedal_slider")
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Auto-Swell Toggle Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleAutoSwell() }
                    .testTag("auto_swell_button"),
                color = if (isAutoSwell) MoisesEmerald.copy(alpha = 0.18f) else Color(0xFF0E1624),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAutoSwell) MoisesEmerald else Color(0xFF1B2C44)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Auto Swell",
                        tint = if (isAutoSwell) MoisesEmerald else MoisesTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAutoSwell) "Auto-Swell Ativo (Pulso Automático)" else "Ativar Auto-Swell (Fade In/Out Contínuo)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAutoSwell) MoisesEmerald else MoisesTextPrimary
                    )
                }
            }
        }
    }
}
