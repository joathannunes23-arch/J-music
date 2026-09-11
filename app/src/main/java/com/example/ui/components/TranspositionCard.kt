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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun TranspositionCard(
    originalKey: String,
    transposedKey: String,
    transposeSemitones: Int,
    originalChords: String,
    transposedChords: String,
    onSetSemitones: (Int) -> Unit,
    onOpenExportDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transposition_card"),
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
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Mudar Tom",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Transposição de Tom & Pitch",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Mude o tom para a sua voz ou exporte no novo tom",
                            fontSize = 11.sp,
                            color = MoisesTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Key Tone Comparison Box
            Surface(
                color = Color(0xFF0C1320),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Original Key
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOM ORIGINAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MoisesTextMuted, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(originalKey, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MoisesTextPrimary)
                    }

                    // Arrow
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MoisesEmerald,
                        modifier = Modifier.size(18.dp)
                    )

                    // Transposed Key
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NOVO TOM", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MoisesEmeraldLight, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(transposedKey, fontSize = 18.sp, fontWeight = FontWeight.Black, color = MoisesEmerald)
                            if (transposeSemitones != 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(MoisesEmeraldDark, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (transposeSemitones > 0) "+$transposeSemitones" else "$transposeSemitones",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MoisesEmeraldLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Semitones Selector Row (-6 to +6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSetSemitones((transposeSemitones - 1).coerceAtLeast(-6)) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MoisesSurfaceElevated, RoundedCornerShape(10.dp))
                        .testTag("transpose_minus_button")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir Tom", tint = MoisesTextPrimary)
                }

                // Quick Semitone Buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(-2, -1, 0, 1, 2).forEach { st ->
                        val isSelected = transposeSemitones == st
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MoisesEmerald else Color(0xFF0E1624))
                                .border(1.dp, if (isSelected) MoisesEmeraldLight else Color(0xFF1B2C44), RoundedCornerShape(8.dp))
                                .clickable { onSetSemitones(st) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (st == 0) "Orig." else if (st > 0) "+$st" else "$st",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MoisesTextSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { onSetSemitones((transposeSemitones + 1).coerceAtMost(6)) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MoisesSurfaceElevated, RoundedCornerShape(10.dp))
                        .testTag("transpose_plus_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar Tom", tint = MoisesTextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transposed Chords preview banner
            Surface(
                color = Color(0xFF0C1320),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Cifra Transposta:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesTextMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transposedChords,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Export with this Key Button
            Button(
                onClick = onOpenExportDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_export_with_transposition_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoisesEmerald,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exportar Música em $transposedKey",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
