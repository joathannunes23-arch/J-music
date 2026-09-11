package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WorshipSongEntity
import com.example.ui.UploadState
import com.example.ui.theme.MoisesCardBorder
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceVariant
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

@Composable
fun SongUploadCard(
    currentSong: WorshipSongEntity?,
    uploadState: UploadState,
    onUploadSong: (Uri, String, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var fileName = "worship_track.mp3"
            var fileSize = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                    if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                }
            }

            onUploadSong(uri, fileName, fileSize)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("upload_worship_song_card"),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MoisesSurfaceVariant, RoundedCornerShape(10.dp))
                            .border(1.dp, MoisesCardBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "Upload",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Importar Áudio / Música",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = "Formatos: MP3, WAV, OGG (máx. 10MB)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MoisesTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Big Button: "Selecionar Música"
            Button(
                onClick = { filePickerLauncher.launch("audio/*") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F264A),
                    contentColor = MoisesEmeraldLight
                ),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, MoisesEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("upload_worship_song_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        tint = MoisesEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Selecionar Música / Áudio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmeraldLight
                    )
                }
            }

            // Upload Progress & Filename display
            AnimatedVisibility(visible = uploadState.isUploading, enter = fadeIn(), exit = fadeOut()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Enviando: ${uploadState.currentFileName}",
                            fontSize = 11.sp,
                            color = MoisesTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(uploadState.progress * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { uploadState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MoisesEmerald,
                        trackColor = Color(0xFF1B2920)
                    )
                }
            }

            // Error display if > 10MB
            if (uploadState.errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFF381418),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Erro",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = uploadState.errorMessage,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }

            // Current Active Song display card
            if (currentSong != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    color = Color(0xFF101914),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22382A))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color(0xFF15261D), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = "Song",
                                tint = MoisesEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentSong.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "${currentSong.artist} • ${currentSong.keyTone} • ${currentSong.bpm} BPM",
                                fontSize = 11.sp,
                                color = MoisesTextSecondary,
                                maxLines = 1
                            )
                        }
                        Surface(
                            color = Color(0xFF182D21),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", currentSong.fileSizeMb)} MB",
                                fontSize = 10.sp,
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
}
