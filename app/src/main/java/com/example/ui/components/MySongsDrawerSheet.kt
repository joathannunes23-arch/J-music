package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WorshipSongEntity
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySongsDrawerSheet(
    isOpen: Boolean,
    songs: List<WorshipSongEntity>,
    currentSongId: Long?,
    onSelectSong: (WorshipSongEntity) -> Unit,
    onDeleteSong: (WorshipSongEntity) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF0A1019),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFF1E2E44), CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("minhas_musicas_sheet")
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
                            .size(38.dp)
                            .background(Color(0xFF102038), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = "Minhas Músicas",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Minhas Músicas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "${songs.size} faixas salvas localmente no banco Room",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoisesTextSecondary
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = MoisesTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF1B2C44))
            Spacer(modifier = Modifier.height(12.dp))

            // Songs List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(songs, key = { it.id }) { song ->
                    val isSelected = currentSongId == song.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectSong(song)
                                onDismiss()
                            }
                            .testTag("song_item_${song.id}"),
                        color = if (isSelected) Color(0xFF12243F) else Color(0xFF0C1320),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MoisesEmerald else Color(0xFF1B2C44)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSelected) MoisesEmerald else Color(0xFF142236),
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.PlayArrow else Icons.Default.MusicNote,
                                    contentDescription = "Música",
                                    tint = if (isSelected) Color.White else MoisesEmeraldLight,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MoisesTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${song.artist} • ${song.keyTone} • ${song.bpm} BPM",
                                    fontSize = 11.sp,
                                    color = MoisesTextSecondary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Acordes: ${song.chords}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MoisesEmeraldLight,
                                    maxLines = 1
                                )
                            }

                            if (!song.isSample) {
                                IconButton(
                                    onClick = { onDeleteSong(song) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = Color(0xFFF87171),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            } else {
                                Surface(
                                    color = Color(0xFF12233B),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "PADRÃO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MoisesEmeraldLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
