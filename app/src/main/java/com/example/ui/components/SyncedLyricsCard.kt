package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SyncedLyricLine
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@Composable
fun SyncedLyricsCard(
    lyrics: List<SyncedLyricLine>,
    currentPositionSec: Int,
    isPlaying: Boolean,
    isSyncEditMode: Boolean,
    onToggleSyncEditMode: () -> Unit,
    onSyncLineToCurrent: (String) -> Unit,
    onSeekToLineTime: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Find active lyric line index based on current playback second
    val activeIndex = lyrics.indexOfLast { it.timeSeconds <= currentPositionSec }.coerceAtLeast(0)

    // Auto-scroll to active line when playback progresses
    LaunchedEffect(activeIndex, isPlaying) {
        if (isPlaying && activeIndex >= 0 && activeIndex < lyrics.size) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("synced_lyrics_card"),
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
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Letra Sincronizada",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Letra & Teleprompter Sincronizado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = if (isSyncEditMode) "Modo Edição: Clique para marcar pontos de sincronia" else "Sincronia ao vivo • Toque na frase para pular",
                            fontSize = 11.sp,
                            color = if (isSyncEditMode) MoisesEmeraldLight else MoisesTextSecondary
                        )
                    }
                }

                Button(
                    onClick = onToggleSyncEditMode,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSyncEditMode) MoisesEmerald else Color(0xFF10233D),
                        contentColor = if (isSyncEditMode) Color.White else MoisesEmeraldLight
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("toggle_lyrics_sync_mode_button")
                ) {
                    Icon(
                        imageVector = if (isSyncEditMode) Icons.Default.Sync else Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSyncEditMode) "Pronto" else "Sincronizar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lyrics List
            if (lyrics.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma letra carregada para esta música", color = MoisesTextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(lyrics, key = { _, line -> line.id }) { index, line ->
                        val isActive = index == activeIndex

                        val bgColor by animateColorAsState(
                            targetValue = if (isActive) Color(0xFF132B4C) else Color(0xFF0F1726),
                            label = "lyric_bg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = if (isActive) MoisesEmerald else Color(0xFF1A2B42),
                            label = "lyric_border"
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeekToLineTime(line.timeSeconds) }
                                .testTag("lyric_line_${line.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Timestamp badge
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isActive) MoisesEmeraldDark else Color(0xFF162438),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = line.timeFormatted,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) MoisesEmeraldLight else MoisesTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        if (line.section.isNotBlank()) {
                                            Text(
                                                text = line.section.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isActive) MoisesEmerald else MoisesTextMuted,
                                                letterSpacing = 1.sp
                                            )
                                        }
                                        Text(
                                            text = line.text,
                                            fontSize = if (isActive) 15.sp else 13.sp,
                                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isActive) MoisesTextPrimary else MoisesTextSecondary
                                        )
                                    }
                                }

                                // If sync edit mode, show "Marcar Tempo" button
                                if (isSyncEditMode) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MoisesEmeraldDark)
                                            .clickable { onSyncLineToCurrent(line.id) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = MoisesEmeraldLight,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Marcar Aqui",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MoisesEmeraldLight
                                            )
                                        }
                                    }
                                } else if (isActive) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Tocando",
                                        tint = MoisesEmerald,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
