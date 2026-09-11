package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.audio.InstrumentLayer
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceVariant
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

data class KeyboardKeyInfo(
    val baseName: String,
    val notesDisplay: String,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorshipKeyboard(
    isMinorMode: Boolean,
    activeChordKey: String?,
    enabledLayers: Set<InstrumentLayer>,
    onToggleMinorMode: () -> Unit,
    onChordPressed: (String) -> Unit,
    onChordReleased: () -> Unit,
    onToggleLayer: (InstrumentLayer) -> Unit,
    modifier: Modifier = Modifier
) {
    // 8 big keys in a row: C • G • D • A • Em • Am • Dm • F
    val worshipKeys = listOf(
        KeyboardKeyInfo("C", "Dó - Mi - Sol", Color(0xFF00E676)),
        KeyboardKeyInfo("G", "Sol - Si - Ré", Color(0xFF4ADE80)),
        KeyboardKeyInfo("D", "Ré - Fá# - Lá", Color(0xFF38BDF8)),
        KeyboardKeyInfo("A", "Lá - Dó# - Mi", Color(0xFFFBBF24)),
        KeyboardKeyInfo("Em", "Mi - Sol - Si", Color(0xFFA855F7)),
        KeyboardKeyInfo("Am", "Lá - Dó - Mi", Color(0xFFF43F5E)),
        KeyboardKeyInfo("Dm", "Ré - Fá - Lá", Color(0xFF6366F1)),
        KeyboardKeyInfo("F", "Fá - Lá - Dó", Color(0xFF10B981))
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("teclado_acordes_worship_card"),
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
            // Header: Title & "Maior / Menor" toggle button
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
                            imageVector = Icons.Default.Piano,
                            contentDescription = "Piano Icon",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Teclado de Acordes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "8 Teclas em Fila • Síntese em Tempo Real",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoisesTextSecondary
                        )
                    }
                }

                // "Maior / Menor" Mode Toggle Button above the keys
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onToggleMinorMode() }
                        .testTag("toggle_maior_menor_button"),
                    color = if (isMinorMode) Color(0xFF251A38) else Color(0xFF102038),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        if (isMinorMode) Color(0xFFA855F7) else MoisesEmerald
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Alternar Modo",
                            tint = if (isMinorMode) Color(0xFFA855F7) else MoisesEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMinorMode) "Modo: MENOR" else "Modo: MAIOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isMinorMode) Color(0xFFE9D5FF) else MoisesEmeraldLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section: "Instrumentos tocando junto"
            Text(
                text = "Camadas de Instrumentos e Texturas:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MoisesTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Multi-Instrument Layer Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InstrumentLayer.entries.forEach { layer ->
                    val isEnabled = enabledLayers.contains(layer)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleLayer(layer) }
                            .testTag("layer_${layer.name}"),
                        color = if (isEnabled) Color(0xFF132845) else Color(0xFF0F1726),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isEnabled) MoisesEmerald.copy(alpha = 0.8f) else Color(0xFF1A2B42)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = layer.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = layer.label,
                                fontSize = 11.sp,
                                fontWeight = if (isEnabled) FontWeight.Bold else FontWeight.Normal,
                                color = if (isEnabled) MoisesEmeraldLight else MoisesTextMuted
                            )
                            if (isEnabled) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(MoisesEmerald, CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 8 Big Keys in a Row
            Text(
                text = "C • G • D • A • Em • Am • Dm • F",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MoisesTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Scrollable / Flexible Row of 8 Big Keys
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                worshipKeys.forEach { keyInfo ->
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val isCurrentlyActive = activeChordKey == keyInfo.baseName

                    val displayName = if (isMinorMode) {
                        when (keyInfo.baseName) {
                            "C" -> "Cm"
                            "G" -> "Gm"
                            "D" -> "Dm"
                            "A" -> "Am"
                            "Em" -> "E"
                            "Am" -> "A"
                            "Dm" -> "D"
                            "F" -> "Fm"
                            else -> keyInfo.baseName
                        }
                    } else {
                        keyInfo.baseName
                    }

                    // Key Background & Glow animations
                    val isHighlight = isPressed || isCurrentlyActive
                    val keyBgColor by animateColorAsState(
                        targetValue = if (isHighlight) keyInfo.color else Color(0xFF0D1420),
                        label = "keyBg"
                    )

                    LaunchedEffect(isPressed) {
                        if (isPressed) {
                            onChordPressed(keyInfo.baseName)
                        } else {
                            onChordReleased()
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(84.dp)
                            .height(140.dp)
                            .shadow(
                                elevation = if (isHighlight) 10.dp else 2.dp,
                                shape = RoundedCornerShape(14.dp),
                                spotColor = keyInfo.color
                            )
                            .clip(RoundedCornerShape(14.dp))
                            .background(keyBgColor)
                            .border(
                                width = if (isHighlight) 2.5.dp else 1.2.dp,
                                color = if (isHighlight) Color.White else keyInfo.color.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onChordPressed(keyInfo.baseName)
                            }
                            .testTag("piano_key_${keyInfo.baseName}")
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top LED line
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(4.dp)
                                    .background(
                                        color = if (isHighlight) Color.White else keyInfo.color,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Large Chord Letter
                            Text(
                                text = displayName,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isHighlight) Color.Black else Color.White
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Note description below
                            Text(
                                text = keyInfo.notesDisplay,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isHighlight) Color.Black.copy(alpha = 0.8f) else MoisesTextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
