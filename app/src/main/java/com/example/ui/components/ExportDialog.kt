package com.example.ui.components

import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TranspositionHelper
import com.example.data.db.WorshipSongEntity
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@Composable
fun ExportDialog(
    song: WorshipSongEntity?,
    swellLevel: Float,
    transposeSemitones: Int,
    onSetTransposeSemitones: (Int) -> Unit,
    onConfirmExport: (Context, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    if (song == null) return

    var customNotes by remember { mutableStateOf("") }

    val originalKey = song.keyTone
    val exportedKey = TranspositionHelper.transposeKey(originalKey, transposeSemitones)
    val exportedChords = TranspositionHelper.transposeChordSequence(song.chords, transposeSemitones)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F1726),
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF102038), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SaveAlt,
                        contentDescription = "Exportar Música",
                        tint = MoisesEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Exportar Música para o App",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MoisesTextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Escolha o tom desejado para a exportação e salve na sua Galeria:",
                    fontSize = 13.sp,
                    color = MoisesTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transposition selector inside Export dialog
                Surface(
                    color = Color(0xFF0C1320),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Mudar Tom da Música Exportada:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextMuted
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Original: $originalKey",
                                fontSize = 12.sp,
                                color = MoisesTextSecondary
                            )

                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = MoisesEmerald,
                                modifier = Modifier.size(14.dp)
                            )

                            Text(
                                text = "Novo Tom: $exportedKey",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesEmerald
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stepper row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onSetTransposeSemitones((transposeSemitones - 1).coerceAtLeast(-6)) },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(MoisesSurfaceElevated, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "-1 st", tint = MoisesTextPrimary, modifier = Modifier.size(14.dp))
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(-2, -1, 0, 1, 2).forEach { st ->
                                    val isSelected = transposeSemitones == st
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) MoisesEmerald else Color(0xFF101B2B))
                                            .clickable { onSetTransposeSemitones(st) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (st == 0) "Orig" else if (st > 0) "+$st" else "$st",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MoisesTextSecondary
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { onSetTransposeSemitones((transposeSemitones + 1).coerceAtMost(6)) },
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(MoisesSurfaceElevated, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "+1 st", tint = MoisesTextPrimary, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Acordes no novo tom: $exportedChords",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MoisesEmeraldLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Note Field
                OutlinedTextField(
                    value = customNotes,
                    onValueChange = { customNotes = it },
                    label = { Text("Nota / Observação (opcional)", fontSize = 11.sp) },
                    placeholder = { Text("Ex: Mix com voz destacada para ensaio", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MoisesEmerald,
                        unfocusedBorderColor = Color(0xFF1B2C44),
                        focusedTextColor = MoisesTextPrimary,
                        unfocusedTextColor = MoisesTextPrimary,
                        focusedLabelColor = MoisesEmerald,
                        unfocusedLabelColor = MoisesTextMuted
                    ),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmExport(context, customNotes)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoisesEmerald,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_export_button")
            ) {
                Icon(
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salvar na Galeria & Compartilhar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar", color = MoisesTextSecondary, fontSize = 12.sp)
            }
        }
    )
}
