package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ExportedSongEntity
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportGalleryDialog(
    exportedSongs: List<ExportedSongEntity>,
    onLoadSong: (ExportedSongEntity) -> Unit,
    onDeleteSong: (ExportedSongEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

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
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = "Galeria de Músicas Exportadas",
                        tint = MoisesEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Galeria de Músicas Exportadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MoisesTextPrimary
                    )
                    Text(
                        text = "${exportedSongs.size} gravações e mixes salvos",
                        fontSize = 11.sp,
                        color = MoisesTextSecondary
                    )
                }
            }
        },
        text = {
            if (exportedSongs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = MoisesTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhuma música exportada ainda.",
                            fontSize = 13.sp,
                            color = MoisesTextMuted
                        )
                        Text(
                            text = "Use o botão Exportar para salvar seus mixes e tons personalizados!",
                            fontSize = 11.sp,
                            color = MoisesTextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(exportedSongs, key = { it.id }) { song ->
                        ExportedSongItemCard(
                            song = song,
                            dateString = dateFormat.format(Date(song.exportedDate)),
                            onLoad = {
                                onLoadSong(song)
                                onDismiss()
                            },
                            onShare = {
                                shareExportedSong(context, song)
                            },
                            onDelete = { onDeleteSong(song) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoisesEmerald,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Fechar", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun ExportedSongItemCard(
    song: ExportedSongEntity,
    dateString: String,
    onLoad: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Color(0xFF0C1320),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Title and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesTextPrimary
                    )
                    Text(
                        text = "${song.artist} • $dateString",
                        fontSize = 11.sp,
                        color = MoisesTextSecondary
                    )
                }

                Row {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.IosShare,
                            contentDescription = "Compartilhar",
                            tint = MoisesEmeraldLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color(0xFFFF6E6E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Key Tone & Transpose Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF101C2B), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Tom: ${song.exportedKey}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmerald
                    )
                }

                if (song.transposeSemitones != 0) {
                    Box(
                        modifier = Modifier
                            .background(MoisesEmeraldDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (song.transposeSemitones > 0) "+${song.transposeSemitones} st" else "${song.transposeSemitones} st",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmeraldLight
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFF101C2B), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${song.bpm} BPM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chords preview
            Text(
                text = "Cifra: ${song.chords}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MoisesEmeraldLight
            )

            // Notes
            if (song.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.notes,
                    fontSize = 10.sp,
                    color = MoisesTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Load into Studio Button
            Button(
                onClick = onLoad,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10233D),
                    contentColor = MoisesEmeraldLight
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Carregar Mix no Estúdio", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun shareExportedSong(context: Context, song: ExportedSongEntity) {
    val shareText = """
        🎵 J-MUSIC - MÚSICA EXPORTADA DA GALERIA 🎵
        
        Música: ${song.title}
        Artista: ${song.artist}
        Tom Original: ${song.originalKey}
        Tom Final: ${song.exportedKey} (${if (song.transposeSemitones >= 0) "+${song.transposeSemitones}" else "${song.transposeSemitones}"} semitons)
        Andamento: ${song.bpm} BPM
        Acordes: ${song.chords}
        
        Balanço dos Instrumentos:
        • Voz: ${(song.vocalVolume * 100).toInt()}%
        • Bateria: ${(song.drumsVolume * 100).toInt()}%
        • Baixo: ${(song.bassVolume * 100).toInt()}%
        • Teclado/Pad: ${(song.padVolume * 100).toInt()}%
        • Violão/Guitarra: ${(song.guitarVolume * 100).toInt()}%
        • Ambiência/Shimmer: ${(song.ambientVolume * 100).toInt()}%
        • Swell: ${(song.swellLevel * 100).toInt()}%
        
        Notas: ${song.notes}
        Salvo via J-MUSIC.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Música Exportada: ${song.title}")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar da Galeria"))
}
