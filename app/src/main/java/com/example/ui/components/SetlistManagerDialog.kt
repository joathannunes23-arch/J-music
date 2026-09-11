package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.SetlistEntity
import com.example.data.db.SetlistItemEntity
import com.example.ui.WorshipViewModel
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldDark
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary
import com.example.ui.util.rememberHapticFeedbackHelper

@Composable
fun SetlistManagerDialog(
    viewModel: WorshipViewModel,
    onDismiss: () -> Unit
) {
    val setlists by viewModel.allSetlists.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val haptic = rememberHapticFeedbackHelper()

    var selectedSetlist by remember { mutableStateOf<SetlistEntity?>(null) }
    var isCreatingNew by remember { mutableStateOf(false) }
    var newSetName by remember { mutableStateOf("") }
    var newSetDate by remember { mutableStateOf("") }
    var newSetDesc by remember { mutableStateOf("") }

    // Keep selected setlist updated if it exists in latest list
    val currentSelected = selectedSetlist?.let { sel ->
        setlists.find { it.id == sel.id } ?: sel
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f)
                .testTag("setlist_manager_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF090E17),
            border = BorderStroke(1.dp, Color(0xFF1E3250))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentSelected != null) {
                            IconButton(
                                onClick = {
                                    haptic.performClick()
                                    selectedSetlist = null
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = MoisesTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = "Setlists",
                            tint = MoisesEmerald,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentSelected == null) "GERENCIADOR DE SETLISTS" else currentSelected.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesTextPrimary
                            )
                            Text(
                                text = if (currentSelected == null) "Organize repertórios com tom e volume salvos" else currentSelected.serviceDate,
                                fontSize = 11.sp,
                                color = MoisesTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            haptic.performClick()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("setlist_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MoisesTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentSelected == null) {
                    // =========================================================
                    // LIST OF ALL SETLISTS
                    // =========================================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SUAS SETLISTS DE LOUVOR (${setlists.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmeraldLight,
                            letterSpacing = 0.5.sp
                        )

                        TactileAnimatedButton(
                            onClick = { isCreatingNew = !isCreatingNew },
                            backgroundColor = Color(0xFF102844),
                            borderColor = Color(0xFF1D4A7A),
                            cornerRadius = 8.dp,
                            testTag = "create_setlist_btn"
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isCreatingNew) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = "Nova Setlist",
                                    tint = MoisesEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCreatingNew) "Cancelar" else "Nova Setlist",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MoisesEmeraldLight
                                )
                            }
                        }
                    }

                    // Form to create a new setlist
                    AnimatedVisibility(visible = isCreatingNew) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = Color(0xFF0F1726),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF1F3554))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Criar Novo Repertório de Culto",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MoisesEmeraldLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newSetName,
                                    onValueChange = { newSetName = it },
                                    label = { Text("Nome (Ex: Domingo Noite, Celebração)", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MoisesEmerald,
                                        unfocusedBorderColor = Color(0xFF1F324E),
                                        focusedTextColor = MoisesTextPrimary,
                                        unfocusedTextColor = MoisesTextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newSetDate,
                                    onValueChange = { newSetDate = it },
                                    label = { Text("Data ou Culto (Ex: Dom 19:00)", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MoisesEmerald,
                                        unfocusedBorderColor = Color(0xFF1F324E),
                                        focusedTextColor = MoisesTextPrimary,
                                        unfocusedTextColor = MoisesTextPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        if (newSetName.isNotBlank()) {
                                            viewModel.createSetlist(
                                                name = newSetName,
                                                serviceDate = if (newSetDate.isBlank()) "Próximo Culto" else newSetDate,
                                                description = newSetDesc
                                            )
                                            newSetName = ""
                                            newSetDate = ""
                                            newSetDesc = ""
                                            isCreatingNew = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MoisesEmeraldDark)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Salvar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Salvar Setlist", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (setlists.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma setlist criada ainda.\nClique em 'Nova Setlist' para organizar seus cultos!",
                                color = MoisesTextMuted,
                                fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(setlists) { setlist ->
                                SetlistCardRow(
                                    setlist = setlist,
                                    onOpen = {
                                        haptic.performClick()
                                        selectedSetlist = setlist
                                    },
                                    onDelete = {
                                        haptic.performClick()
                                        viewModel.deleteSetlist(setlist)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    // =========================================================
                    // DETAIL VIEW: SONGS IN SELECTED SETLIST
                    // =========================================================
                    SetlistDetailView(
                        viewModel = viewModel,
                        setlist = currentSelected,
                        onBack = { selectedSetlist = null }
                    )
                }
            }
        }
    }
}

@Composable
fun SetlistCardRow(
    setlist: SetlistEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .testTag("setlist_card_${setlist.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1726)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFF1E3250))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setlist.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MoisesTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${setlist.serviceDate} • ${setlist.description.ifBlank { "Sequência congregacional" }}",
                    fontSize = 11.sp,
                    color = MoisesEmeraldLight
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF162740),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF22446E))
                ) {
                    Text(
                        text = "Abrir Louvores",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Excluir Setlist",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SetlistDetailView(
    viewModel: WorshipViewModel,
    setlist: SetlistEntity,
    onBack: () -> Unit
) {
    val itemsFlow = remember(setlist.id) { viewModel.getItemsForSetlist(setlist.id) }
    val items by itemsFlow.collectAsState(initial = emptyList())
    val currentSong by viewModel.currentSong.collectAsState()
    val haptic = rememberHapticFeedbackHelper()

    var showAddCurrentDialog by remember { mutableStateOf(false) }
    var notesInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Subheader actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOUVORES NO REPERTÓRIO (${items.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MoisesEmeraldLight,
                letterSpacing = 0.5.sp
            )

            // Button to add currently open song to this setlist
            TactileAnimatedButton(
                onClick = {
                    notesInput = "Tom: ${viewModel.currentTransposedKey.value} | Vol: ${(viewModel.playbackState.value.masterVolume * 100).toInt()}%"
                    showAddCurrentDialog = true
                },
                backgroundColor = Color(0xFF102844),
                borderColor = Color(0xFF1D4A7A),
                cornerRadius = 8.dp,
                testTag = "add_current_song_to_setlist_btn"
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar Louvor Atual",
                        tint = MoisesEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ Salvar Atual",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MoisesEmeraldLight
                    )
                }
            }
        }

        // Dialog to confirm adding current song
        if (showAddCurrentDialog && currentSong != null) {
            Dialog(onDismissRequest = { showAddCurrentDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F1726),
                    border = BorderStroke(1.dp, Color(0xFF1F3554)),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Salvar na Setlist",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Música: ${currentSong?.title} (${currentSong?.artist})",
                            fontSize = 12.sp,
                            color = MoisesEmerald
                        )
                        Text(
                            text = "Tom atual: ${viewModel.currentTransposedKey.value} • BPM: ${viewModel.currentBpm.value}",
                            fontSize = 11.sp,
                            color = MoisesTextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Anotações de Arranjo / Ministração", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MoisesEmerald,
                                unfocusedBorderColor = Color(0xFF1F324E),
                                focusedTextColor = MoisesTextPrimary,
                                unfocusedTextColor = MoisesTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showAddCurrentDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF192538))
                            ) {
                                Text("Cancelar", color = MoisesTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.addCurrentSongToSetlist(setlist.id, notesInput)
                                    showAddCurrentDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MoisesEmeraldDark)
                            ) {
                                Text("Salvar com Configs", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma música nesta setlist ainda.\nClique em '+ Salvar Atual' para adicionar o louvor atual com suas configurações de tom e volume!",
                    color = MoisesTextMuted,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    SetlistItemRow(
                        item = item,
                        onLoad = {
                            haptic.performClick()
                            viewModel.loadSetlistItemIntoStudio(item)
                        },
                        onDelete = {
                            haptic.performClick()
                            viewModel.deleteSetlistItem(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SetlistItemRow(
    item: SetlistItemEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("setlist_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1726)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF1E3250))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF162740), CircleShape)
                            .border(1.dp, Color(0xFF22446E), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${item.orderIndex + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = item.songTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesTextPrimary
                        )
                        Text(
                            text = item.artist,
                            fontSize = 11.sp,
                            color = MoisesTextMuted
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Load into Studio Button
                    TactileAnimatedButton(
                        onClick = onLoad,
                        backgroundColor = Color(0xFF0C2418),
                        borderColor = Color(0xFF1E6040),
                        cornerRadius = 8.dp,
                        testTag = "load_setlist_item_btn_${item.id}"
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Carregar Louvor",
                                tint = MoisesEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Carregar",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MoisesEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Excluir Música da Setlist",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Presets Badges (Key, BPM, Master Volume, Stem details)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Key Badge
                Surface(
                    color = Color(0xFF112236),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E3F66))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MoisesEmerald,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = item.keyTone,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MoisesEmeraldLight
                        )
                    }
                }

                // BPM Badge
                Surface(
                    color = Color(0xFF112236),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E3F66))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = Color(0xFF64B5F6),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${item.bpm} BPM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF90CAF9)
                        )
                    }
                }

                // Volume Preset Badge
                Surface(
                    color = Color(0xFF112236),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E3F66))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Vol ${(item.masterVolume * 100).toInt()}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC80)
                        )
                    }
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Arranjo: ${item.notes}",
                    fontSize = 10.sp,
                    color = MoisesTextMuted,
                    maxLines = 2
                )
            }
        }
    }
}
