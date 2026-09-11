package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiChordFinderView
import com.example.ui.components.ExportDialog
import com.example.ui.components.ExportGalleryDialog
import com.example.ui.components.MySongsDrawerSheet
import com.example.ui.components.PlaybackControlsCard
import com.example.ui.components.SetlistManagerDialog
import com.example.ui.components.SongUploadCard
import com.example.ui.components.StemMixerCard
import com.example.ui.components.SwellPedal
import com.example.ui.components.SyncedLyricsCard
import com.example.ui.components.TapTempoCard
import com.example.ui.components.TranspositionCard
import com.example.ui.components.VisualEqualizerCard
import com.example.ui.components.WorshipKeyboard
import com.example.ui.components.WorshipTopBar
import com.example.ui.theme.MoisesDarkBg
import com.example.ui.theme.MoisesEmerald
import com.example.ui.theme.MoisesEmeraldLight
import com.example.ui.theme.MoisesSurface
import com.example.ui.theme.MoisesSurfaceElevated
import com.example.ui.theme.MoisesTextMuted
import com.example.ui.theme.MoisesTextPrimary
import com.example.ui.theme.MoisesTextSecondary

enum class WorshipStudioTab(val id: String, val label: String, val icon: String) {
    STEMS("stems", "Mixer Stems", "🎛️"),
    EQUALIZER("eq", "Equalizador", "📊"),
    TAP_TEMPO("tempo", "Tap Tempo", "⏱️"),
    LYRICS("lyrics", "Letra Sync", "📜"),
    TRANSPOSE("transpose", "Mudar Tom", "🎵"),
    KEYBOARD("keyboard", "Acordes IA", "🎹")
}

@Composable
fun WorshipMainScreen(
    viewModel: WorshipViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Core States
    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val swellPedalLevel by viewModel.swellPedalLevel.collectAsStateWithLifecycle()
    val isAutoSwell by viewModel.isAutoSwell.collectAsStateWithLifecycle()
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val aiState by viewModel.aiState.collectAsStateWithLifecycle()
    val isMinorKeyboardMode by viewModel.isMinorKeyboardMode.collectAsStateWithLifecycle()
    val activeChordKey by viewModel.activeChordKey.collectAsStateWithLifecycle()
    val enabledLayers by viewModel.enabledLayers.collectAsStateWithLifecycle()
    val mySongs by viewModel.savedSongs.collectAsStateWithLifecycle()
    val exportedSongs by viewModel.exportedSongs.collectAsStateWithLifecycle()
    val allSetlists by viewModel.allSetlists.collectAsStateWithLifecycle()

    // Dialogs
    val isDrawerOpen by viewModel.isDrawerOpen.collectAsStateWithLifecycle()
    val showExportDialog by viewModel.showExportDialog.collectAsStateWithLifecycle()
    val showGalleryDialog by viewModel.showGalleryDialog.collectAsStateWithLifecycle()
    val showSetlistDialog by viewModel.showSetlistDialog.collectAsStateWithLifecycle()

    // New feature states
    val activeStudioTab by viewModel.activeStudioTab.collectAsStateWithLifecycle()
    val stemStates by viewModel.stemStates.collectAsStateWithLifecycle()
    val isSeparatingStems by viewModel.isSeparatingStems.collectAsStateWithLifecycle()
    val stemProgress by viewModel.stemSeparationProgress.collectAsStateWithLifecycle()
    val stemStep by viewModel.stemSeparationStep.collectAsStateWithLifecycle()

    val eqGainsDb by viewModel.eqGainsDb.collectAsStateWithLifecycle()
    val activeEqPreset by viewModel.activeEqPreset.collectAsStateWithLifecycle()
    val visualizerLevels by viewModel.visualizerLevels.collectAsStateWithLifecycle()

    val currentBpm by viewModel.currentBpm.collectAsStateWithLifecycle()
    val tappedBpm by viewModel.tappedBpmHistory.collectAsStateWithLifecycle()
    val isMetronomeFlash by viewModel.isMetronomeFlash.collectAsStateWithLifecycle()
    val timeSignature by viewModel.timeSignature.collectAsStateWithLifecycle()
    val currentBeatIndex by viewModel.currentBeatIndex.collectAsStateWithLifecycle()
    val isStrongBeat by viewModel.isStrongBeat.collectAsStateWithLifecycle()
    val isMetronomeHapticEnabled by viewModel.isMetronomeHapticEnabled.collectAsStateWithLifecycle()

    val transposeSemitones by viewModel.transposeSemitones.collectAsStateWithLifecycle()
    val transposedKey by viewModel.currentTransposedKey.collectAsStateWithLifecycle()
    val transposedChords by viewModel.currentTransposedChords.collectAsStateWithLifecycle()

    val lyricsList by viewModel.lyricsList.collectAsStateWithLifecycle()
    val isLyricsSyncEditMode by viewModel.isLyricsSyncEditMode.collectAsStateWithLifecycle()

    var selectedStudioTab by remember { mutableStateOf(WorshipStudioTab.STEMS) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MoisesDarkBg)
            .statusBarsPadding(),
        topBar = {
            WorshipTopBar(
                songsCount = mySongs.size,
                exportedCount = exportedSongs.size,
                setlistsCount = allSetlists.size,
                onOpenDrawer = { viewModel.setDrawerOpen(true) },
                onOpenGallery = { viewModel.setShowGalleryDialog(true) },
                onOpenExport = { viewModel.setShowExportDialog(true) },
                onOpenSetlists = { viewModel.setShowSetlistDialog(true) }
            )
        },
        containerColor = MoisesDarkBg
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 800.dp

            if (isWideScreen) {
                // DESKTOP / TABLET / WIDE: Two Clear Columns Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // LEFT COLUMN (1): Upload, Playback, Stems Mixer, Equalizer & Swell
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Section Header
                        Surface(
                            color = Color(0xFF0E1624),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎛️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CANAL 1: UPLOAD, REPRODUÇÃO & MIXER DE INSTRUMENTOS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MoisesEmeraldLight
                                )
                            }
                        }

                        // Upload Card
                        SongUploadCard(
                            currentSong = currentSong,
                            uploadState = uploadState,
                            onUploadSong = { uri, name, size ->
                                viewModel.uploadSongFromUri(context, uri, name, size)
                            }
                        )

                        // Playback Controls Card
                        PlaybackControlsCard(
                            playbackState = playbackState,
                            swellPedalLevel = swellPedalLevel,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onStop = { viewModel.stopPlayback() },
                            onSeek = { viewModel.seekToPosition(it) },
                            onVolumeChange = { viewModel.setMasterVolume(it) }
                        )

                        // 6-Stem Multi-Track Mixer with Key Transposer & 5-Band Visual Equalizer
                        StemMixerCard(
                            stemStates = stemStates,
                            isSeparating = isSeparatingStems,
                            separationProgress = stemProgress,
                            separationStep = stemStep,
                            isPlaying = playbackState.isPlaying,
                            originalKey = currentSong?.keyTone ?: "C Maior",
                            transposedKey = transposedKey,
                            transposeSemitones = transposeSemitones,
                            onSetSemitones = { viewModel.setTransposeSemitones(it) },
                            eqGainsDb = eqGainsDb,
                            activeEqPreset = activeEqPreset,
                            visualizerLevels = visualizerLevels,
                            onEqGainChange = { band, gain -> viewModel.setEqGain(band, gain) },
                            onSelectEqPreset = { preset -> viewModel.applyEqPreset(preset) },
                            onStemVolumeChange = { ch, vol -> viewModel.setStemVolume(ch, vol) },
                            onToggleMute = { ch -> viewModel.toggleStemMute(ch) },
                            onToggleSolo = { ch -> viewModel.toggleStemSolo(ch) },
                            onTriggerSeparation = { viewModel.separateStemsWithAi() },
                            onApplyPreset = { viewModel.applyStemPreset(it) },
                            onResetStems = { viewModel.resetAllStems() },
                            onOpenSetlists = { viewModel.setShowSetlistDialog(true) }
                        )

                        // 5-Band Visual Equalizer
                        VisualEqualizerCard(
                            eqGainsDb = eqGainsDb,
                            activePreset = activeEqPreset,
                            visualizerLevels = visualizerLevels,
                            isPlaying = playbackState.isPlaying,
                            onGainChange = { band, gain -> viewModel.setEqGain(band, gain) },
                            onSelectPreset = { preset -> viewModel.applyEqPreset(preset) }
                        )

                        // Swell Pedal
                        SwellPedal(
                            swellLevel = swellPedalLevel,
                            isAutoSwell = isAutoSwell,
                            onSwellChange = { viewModel.setSwellPedalLevel(it) },
                            onToggleAutoSwell = { viewModel.toggleAutoSwell() }
                        )
                    }

                    // RIGHT COLUMN (2): AI Chords, Synced Lyrics, Tap Tempo, Transposition & Keyboard
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Section Header
                        Surface(
                            color = Color(0xFF0E1624),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3250))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎹", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CANAL 2: ACORDES IA, LETRA SINCRONIZADA & PITCH",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = MoisesEmeraldLight
                                )
                            }
                        }

                        // AI Chord Finder View
                        AiChordFinderView(
                            aiState = aiState,
                            activePlayingChord = activeChordKey,
                            onDetectClicked = { viewModel.detectChordsWithAi() },
                            onChordClicked = { chord ->
                                viewModel.playChordKey(chord.name)
                            }
                        )

                        // Synced Lyrics Card
                        SyncedLyricsCard(
                            lyrics = lyricsList,
                            currentPositionSec = playbackState.currentPositionSec,
                            isPlaying = playbackState.isPlaying,
                            isSyncEditMode = isLyricsSyncEditMode,
                            onToggleSyncEditMode = { viewModel.toggleLyricsSyncEditMode() },
                            onSyncLineToCurrent = { id -> viewModel.syncLyricLineToCurrentPlayback(id) },
                            onSeekToLineTime = { sec -> viewModel.seekToLyricTime(sec) }
                        )

                        // Key Transposition Card
                        TranspositionCard(
                            originalKey = currentSong?.keyTone ?: "C Maior",
                            transposedKey = transposedKey,
                            transposeSemitones = transposeSemitones,
                            originalChords = currentSong?.chords ?: "C, G, Am, F",
                            transposedChords = transposedChords,
                            onSetSemitones = { viewModel.setTransposeSemitones(it) },
                            onOpenExportDialog = { viewModel.setShowExportDialog(true) }
                        )

                        // Tap Tempo & Visual Metronome Card
                        TapTempoCard(
                            currentBpm = currentBpm,
                            tappedBpm = tappedBpm,
                            isMetronomeFlash = isMetronomeFlash,
                            timeSignature = timeSignature,
                            currentBeatIndex = currentBeatIndex,
                            isStrongBeat = isStrongBeat,
                            isMetronomeHapticEnabled = isMetronomeHapticEnabled,
                            onTimeSignatureChange = { viewModel.setTimeSignature(it) },
                            onToggleMetronomeHaptic = { viewModel.toggleMetronomeHaptic() },
                            onRecordTap = { viewModel.recordTempoTap() },
                            onAdjustBpm = { delta -> viewModel.adjustBpm(delta) },
                            onApplyBpm = { viewModel.applyTappedBpmToSong() }
                        )

                        // Worship 8-Key Keyboard
                        WorshipKeyboard(
                            isMinorMode = isMinorKeyboardMode,
                            activeChordKey = activeChordKey,
                            enabledLayers = enabledLayers,
                            onToggleMinorMode = { viewModel.toggleKeyboardMinorMode() },
                            onChordPressed = { chordName -> viewModel.playChordKey(chordName) },
                            onChordReleased = { viewModel.releaseChordKey() },
                            onToggleLayer = { layer -> viewModel.toggleInstrumentLayer(layer) }
                        )
                    }
                }
            } else {
                // RESPONSIVE MOBILE VIEW: Clean Tab Selector for Worship Studio Tools
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                ) {
                    // Mobile Studio Sub-Tab Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0C1320),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B2C44))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            WorshipStudioTab.values().forEach { tab ->
                                val isSelected = selectedStudioTab == tab
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedStudioTab = tab }
                                        .testTag("tab_${tab.id}"),
                                    color = if (isSelected) Color(0xFF152A4A) else Color(0xFF0E1624),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MoisesEmerald else Color(0xFF1A283C)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 5.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = tab.icon, fontSize = 13.sp)
                                        Text(
                                            text = tab.label,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MoisesEmeraldLight else MoisesTextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Content Area per Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            when (selectedStudioTab) {
                                WorshipStudioTab.STEMS -> {
                                    // Upload + Playback + 6-Stem Mixer
                                    SongUploadCard(
                                        currentSong = currentSong,
                                        uploadState = uploadState,
                                        onUploadSong = { uri, name, size ->
                                            viewModel.uploadSongFromUri(context, uri, name, size)
                                        }
                                    )

                                    PlaybackControlsCard(
                                        playbackState = playbackState,
                                        swellPedalLevel = swellPedalLevel,
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onStop = { viewModel.stopPlayback() },
                                        onSeek = { viewModel.seekToPosition(it) },
                                        onVolumeChange = { viewModel.setMasterVolume(it) }
                                    )

                                    StemMixerCard(
                                        stemStates = stemStates,
                                        isSeparating = isSeparatingStems,
                                        separationProgress = stemProgress,
                                        separationStep = stemStep,
                                        isPlaying = playbackState.isPlaying,
                                        originalKey = currentSong?.keyTone ?: "C Maior",
                                        transposedKey = transposedKey,
                                        transposeSemitones = transposeSemitones,
                                        onSetSemitones = { viewModel.setTransposeSemitones(it) },
                                        eqGainsDb = eqGainsDb,
                                        activeEqPreset = activeEqPreset,
                                        visualizerLevels = visualizerLevels,
                                        onEqGainChange = { band, gain -> viewModel.setEqGain(band, gain) },
                                        onSelectEqPreset = { preset -> viewModel.applyEqPreset(preset) },
                                        onStemVolumeChange = { ch, vol -> viewModel.setStemVolume(ch, vol) },
                                        onToggleMute = { ch -> viewModel.toggleStemMute(ch) },
                                        onToggleSolo = { ch -> viewModel.toggleStemSolo(ch) },
                                        onTriggerSeparation = { viewModel.separateStemsWithAi() },
                                        onApplyPreset = { viewModel.applyStemPreset(it) },
                                        onResetStems = { viewModel.resetAllStems() },
                                        onOpenSetlists = { viewModel.setShowSetlistDialog(true) }
                                    )
                                }
                                WorshipStudioTab.EQUALIZER -> {
                                    // 5-Band Visual Equalizer + Audio Spectrum + Swell Pedal
                                    VisualEqualizerCard(
                                        eqGainsDb = eqGainsDb,
                                        activePreset = activeEqPreset,
                                        visualizerLevels = visualizerLevels,
                                        isPlaying = playbackState.isPlaying,
                                        onGainChange = { band, gain -> viewModel.setEqGain(band, gain) },
                                        onSelectPreset = { preset -> viewModel.applyEqPreset(preset) }
                                    )

                                    SwellPedal(
                                        swellLevel = swellPedalLevel,
                                        isAutoSwell = isAutoSwell,
                                        onSwellChange = { viewModel.setSwellPedalLevel(it) },
                                        onToggleAutoSwell = { viewModel.toggleAutoSwell() }
                                    )
                                }
                                WorshipStudioTab.TAP_TEMPO -> {
                                    // Tactile Tap Tempo Tool + Visual Metronome with Time Signatures & Strong Beat
                                    TapTempoCard(
                                        currentBpm = currentBpm,
                                        tappedBpm = tappedBpm,
                                        isMetronomeFlash = isMetronomeFlash,
                                        timeSignature = timeSignature,
                                        currentBeatIndex = currentBeatIndex,
                                        isStrongBeat = isStrongBeat,
                                        isMetronomeHapticEnabled = isMetronomeHapticEnabled,
                                        onTimeSignatureChange = { viewModel.setTimeSignature(it) },
                                        onToggleMetronomeHaptic = { viewModel.toggleMetronomeHaptic() },
                                        onRecordTap = { viewModel.recordTempoTap() },
                                        onAdjustBpm = { delta -> viewModel.adjustBpm(delta) },
                                        onApplyBpm = { viewModel.applyTappedBpmToSong() }
                                    )

                                    PlaybackControlsCard(
                                        playbackState = playbackState,
                                        swellPedalLevel = swellPedalLevel,
                                        onTogglePlayPause = { viewModel.togglePlayPause() },
                                        onStop = { viewModel.stopPlayback() },
                                        onSeek = { viewModel.seekToPosition(it) },
                                        onVolumeChange = { viewModel.setMasterVolume(it) }
                                    )
                                }
                                WorshipStudioTab.LYRICS -> {
                                    // Synchronized Lyrics Teleprompter & Sync Editor
                                    SyncedLyricsCard(
                                        lyrics = lyricsList,
                                        currentPositionSec = playbackState.currentPositionSec,
                                        isPlaying = playbackState.isPlaying,
                                        isSyncEditMode = isLyricsSyncEditMode,
                                        onToggleSyncEditMode = { viewModel.toggleLyricsSyncEditMode() },
                                        onSyncLineToCurrent = { id -> viewModel.syncLyricLineToCurrentPlayback(id) },
                                        onSeekToLineTime = { sec -> viewModel.seekToLyricTime(sec) }
                                    )
                                }
                                WorshipStudioTab.TRANSPOSE -> {
                                    // Pitch Transposition & Export with Key Change
                                    TranspositionCard(
                                        originalKey = currentSong?.keyTone ?: "C Maior",
                                        transposedKey = transposedKey,
                                        transposeSemitones = transposeSemitones,
                                        originalChords = currentSong?.chords ?: "C, G, Am, F",
                                        transposedChords = transposedChords,
                                        onSetSemitones = { viewModel.setTransposeSemitones(it) },
                                        onOpenExportDialog = { viewModel.setShowExportDialog(true) }
                                    )

                                    SwellPedal(
                                        swellLevel = swellPedalLevel,
                                        isAutoSwell = isAutoSwell,
                                        onSwellChange = { viewModel.setSwellPedalLevel(it) },
                                        onToggleAutoSwell = { viewModel.toggleAutoSwell() }
                                    )
                                }
                                WorshipStudioTab.KEYBOARD -> {
                                    // AI Chord Detection + Worship 8-Key Keyboard
                                    AiChordFinderView(
                                        aiState = aiState,
                                        activePlayingChord = activeChordKey,
                                        onDetectClicked = { viewModel.detectChordsWithAi() },
                                        onChordClicked = { chord ->
                                            viewModel.playChordKey(chord.name)
                                        }
                                    )

                                    WorshipKeyboard(
                                        isMinorMode = isMinorKeyboardMode,
                                        activeChordKey = activeChordKey,
                                        enabledLayers = enabledLayers,
                                        onToggleMinorMode = { viewModel.toggleKeyboardMinorMode() },
                                        onChordPressed = { chordName -> viewModel.playChordKey(chordName) },
                                        onChordReleased = { viewModel.releaseChordKey() },
                                        onToggleLayer = { layer -> viewModel.toggleInstrumentLayer(layer) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }

                    // Persistent Mini Player Bottom Bar on Mobile
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1C2C44)),
                        color = Color(0xFF0A1019)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (playbackState.isPlaying) MoisesEmerald else Color(0xFF142033),
                                            CircleShape
                                        )
                                        .clickable { viewModel.togglePlayPause() }
                                        .testTag("mini_player_play_pause"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Tocar",
                                        tint = if (playbackState.isPlaying) Color.White else MoisesEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = currentSong?.title ?: "J-MUSIC",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MoisesTextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = if (activeChordKey != null) "Acorde: $activeChordKey • $transposedKey" else "$currentBpm BPM • Tom: $transposedKey",
                                        fontSize = 10.sp,
                                        color = MoisesEmeraldLight
                                    )
                                }
                            }

                            // Quick Swell Knob
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Waves,
                                    contentDescription = null,
                                    tint = MoisesEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${(swellPedalLevel * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MoisesEmerald
                                )
                            }
                        }
                    }
                }
            }
        }

        // Side Sheet: "Minhas Músicas"
        MySongsDrawerSheet(
            isOpen = isDrawerOpen,
            songs = mySongs,
            currentSongId = currentSong?.id,
            onSelectSong = { song -> viewModel.selectSong(song) },
            onDeleteSong = { song -> viewModel.deleteSong(song) },
            onDismiss = { viewModel.setDrawerOpen(false) }
        )

        // Export Dialog (with Transposition Key Selection & custom note)
        if (showExportDialog) {
            ExportDialog(
                song = currentSong,
                swellLevel = swellPedalLevel,
                transposeSemitones = transposeSemitones,
                onSetTransposeSemitones = { viewModel.setTransposeSemitones(it) },
                onConfirmExport = { ctx, notes -> viewModel.exportAndSaveMix(ctx, notes) },
                onDismiss = { viewModel.setShowExportDialog(false) }
            )
        }

        // Exported Songs Gallery Dialog
        if (showGalleryDialog) {
            ExportGalleryDialog(
                exportedSongs = exportedSongs,
                onLoadSong = { exported -> viewModel.loadExportedSongIntoStudio(exported) },
                onDeleteSong = { exported -> viewModel.deleteExportedSong(exported) },
                onDismiss = { viewModel.setShowGalleryDialog(false) }
            )
        }

        // Setlist Manager Dialog
        if (showSetlistDialog) {
            SetlistManagerDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setShowSetlistDialog(false) }
            )
        }
    }
}
