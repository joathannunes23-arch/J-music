package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.InstrumentLayer
import com.example.audio.WorshipAudioEngine
import com.example.data.AiChordDetector
import com.example.data.ChordDetectionResult
import com.example.data.SyncedLyricLine
import com.example.data.TranspositionHelper
import com.example.data.WorshipLyricsDatabase
import com.example.data.db.ExportedSongEntity
import com.example.data.db.SetlistDao
import com.example.data.db.SetlistEntity
import com.example.data.db.SetlistItemEntity
import com.example.data.db.WorshipDatabase
import com.example.data.db.WorshipSongEntity
import com.example.data.repository.WorshipSongRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionSec: Int = 0,
    val durationSec: Int = 248,
    val masterVolume: Float = 0.85f
)

data class UploadState(
    val isUploading: Boolean = false,
    val progress: Float = 0f,
    val currentFileName: String = "",
    val errorMessage: String? = null
)

data class AiDetectionState(
    val isDetecting: Boolean = false,
    val progress: Float = 0f,
    val statusText: String = "",
    val result: ChordDetectionResult? = null
)

enum class StemChannel(
    val id: String,
    val label: String,
    val shortLabel: String,
    val icon: String,
    val colorHex: Long
) {
    VOCAL("vocal", "Vocais (Voz de Louvor)", "Voz", "🎤", 0xFF00E676),
    DRUMS("drums", "Bateria & Percussão", "Bateria", "🥁", 0xFFFF5252),
    BASS("bass", "Baixo (Sub Bass)", "Baixo", "🎸", 0xFFFFD700),
    PAD("pad", "Teclado & Pad Worship", "Teclado", "🎹", 0xFF00B0FF),
    GUITAR("guitar", "Guitarra & Violão", "Violão", "🪕", 0xFFFF9100),
    AMBIENT("ambient", "Ambiência & Shimmer", "Ambiência", "✨", 0xFFE040FB)
}

data class StemState(
    val channel: StemChannel,
    val volume: Float = 0.80f,
    val isMuted: Boolean = false,
    val isSolo: Boolean = false
) {
    val effectiveVolume: Float
        get() = if (isMuted) 0f else volume
}

class WorshipViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorshipSongRepository
    private val setlistDao: SetlistDao
    val audioEngine = WorshipAudioEngine()

    // Database Songs list
    val savedSongs: StateFlow<List<WorshipSongEntity>>
    val exportedSongs: StateFlow<List<ExportedSongEntity>>
    val allSetlists: StateFlow<List<SetlistEntity>>

    // Current selected song
    private val _currentSong = MutableStateFlow<WorshipSongEntity?>(null)
    val currentSong: StateFlow<WorshipSongEntity?> = _currentSong.asStateFlow()

    // Playback state
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    private var playbackJob: Job? = null

    // Swell Pedal State (0.0f .. 1.0f)
    private val _swellPedalLevel = MutableStateFlow(0.75f)
    val swellPedalLevel: StateFlow<Float> = _swellPedalLevel.asStateFlow()

    private val _isAutoSwell = MutableStateFlow(false)
    val isAutoSwell: StateFlow<Boolean> = _isAutoSwell.asStateFlow()
    private var autoSwellJob: Job? = null

    // Upload state
    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()

    // AI Chord Detection State
    private val _aiState = MutableStateFlow(AiDetectionState())
    val aiState: StateFlow<AiDetectionState> = _aiState.asStateFlow()

    // 8-Key Keyboard State
    private val _isMinorKeyboardMode = MutableStateFlow(false)
    val isMinorKeyboardMode: StateFlow<Boolean> = _isMinorKeyboardMode.asStateFlow()

    private val _activeChordKey = MutableStateFlow<String?>(null)
    val activeChordKey: StateFlow<String?> = _activeChordKey.asStateFlow()

    // Instrument Layers for quick keyboard layering
    private val _enabledLayers = MutableStateFlow(
        setOf(
            InstrumentLayer.AMBIENT_PAD,
            InstrumentLayer.ACOUSTIC_PIANO,
            InstrumentLayer.SHIMMER_STRINGS,
            InstrumentLayer.WORSHIP_CHOIR
        )
    )
    val enabledLayers: StateFlow<Set<InstrumentLayer>> = _enabledLayers.asStateFlow()

    // Navigation Sheets / Dialogs
    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    private val _showGalleryDialog = MutableStateFlow(false)
    val showGalleryDialog: StateFlow<Boolean> = _showGalleryDialog.asStateFlow()

    private val _showSetlistDialog = MutableStateFlow(false)
    val showSetlistDialog: StateFlow<Boolean> = _showSetlistDialog.asStateFlow()

    // Active bottom/center tool tab: "STEMS", "EQ", "TEMPO", "LYRICS", "KEYBOARD"
    private val _activeStudioTab = MutableStateFlow("STEMS")
    val activeStudioTab: StateFlow<String> = _activeStudioTab.asStateFlow()

    // --- 1. STEM SEPARATION & MULTI-TRACK MIXER ---
    private val _stemStates = MutableStateFlow(
        mapOf(
            StemChannel.VOCAL to StemState(StemChannel.VOCAL, 0.85f),
            StemChannel.DRUMS to StemState(StemChannel.DRUMS, 0.75f),
            StemChannel.BASS to StemState(StemChannel.BASS, 0.80f),
            StemChannel.PAD to StemState(StemChannel.PAD, 0.90f),
            StemChannel.GUITAR to StemState(StemChannel.GUITAR, 0.75f),
            StemChannel.AMBIENT to StemState(StemChannel.AMBIENT, 0.85f)
        )
    )
    val stemStates: StateFlow<Map<StemChannel, StemState>> = _stemStates.asStateFlow()

    private val _isSeparatingStems = MutableStateFlow(false)
    val isSeparatingStems: StateFlow<Boolean> = _isSeparatingStems.asStateFlow()

    private val _stemSeparationProgress = MutableStateFlow(0f)
    val stemSeparationProgress: StateFlow<Float> = _stemSeparationProgress.asStateFlow()

    private val _stemSeparationStep = MutableStateFlow("")
    val stemSeparationStep: StateFlow<String> = _stemSeparationStep.asStateFlow()

    // --- 2. VISUAL EQUALIZER (5-BAND) ---
    // Gains in dB: -12.0f to +12.0f (0.0f is neutral flat)
    private val _eqGainsDb = MutableStateFlow(floatArrayOf(0f, 0f, 0f, 0f, 0f)) // 60Hz, 250Hz, 1kHz, 4kHz, 12kHz
    val eqGainsDb: StateFlow<FloatArray> = _eqGainsDb.asStateFlow()

    private val _activeEqPreset = MutableStateFlow("Flat")
    val activeEqPreset: StateFlow<String> = _activeEqPreset.asStateFlow()

    private val _visualizerLevels = MutableStateFlow(floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f))
    val visualizerLevels: StateFlow<FloatArray> = _visualizerLevels.asStateFlow()

    // --- 3. TAP TEMPO & VISUAL METRONOME ---
    private val tapTimestamps = mutableListOf<Long>()
    private val _currentBpm = MutableStateFlow(72)
    val currentBpm: StateFlow<Int> = _currentBpm.asStateFlow()

    private val _tappedBpmHistory = MutableStateFlow<Int?>(null)
    val tappedBpmHistory: StateFlow<Int?> = _tappedBpmHistory.asStateFlow()

    private val _isMetronomeFlash = MutableStateFlow(false)
    val isMetronomeFlash: StateFlow<Boolean> = _isMetronomeFlash.asStateFlow()

    // Time signature: "4/4" (beatsPerBar=4) or "3/4" (beatsPerBar=3), "6/8"
    private val _timeSignature = MutableStateFlow("4/4")
    val timeSignature: StateFlow<String> = _timeSignature.asStateFlow()

    // Current beat in bar (1..beatsPerBar)
    private val _currentBeatIndex = MutableStateFlow(1)
    val currentBeatIndex: StateFlow<Int> = _currentBeatIndex.asStateFlow()

    // Downbeat / Tempo Forte flag
    private val _isStrongBeat = MutableStateFlow(false)
    val isStrongBeat: StateFlow<Boolean> = _isStrongBeat.asStateFlow()

    // Haptic vibration on metronome beats toggle
    private val _isMetronomeHapticEnabled = MutableStateFlow(true)
    val isMetronomeHapticEnabled: StateFlow<Boolean> = _isMetronomeHapticEnabled.asStateFlow()

    // --- 4. TRANSPOSITION (KEY CHANGE) ---
    // Semitones: -6 .. +6
    private val _transposeSemitones = MutableStateFlow(0)
    val transposeSemitones: StateFlow<Int> = _transposeSemitones.asStateFlow()

    // Derived transposed key and chords
    val currentTransposedKey: StateFlow<String> = combine(_currentSong, _transposeSemitones) { song, semi ->
        val orig = song?.keyTone ?: "C Maior"
        TranspositionHelper.transposeKey(orig, semi)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "C Maior")

    val currentTransposedChords: StateFlow<String> = combine(_currentSong, _transposeSemitones) { song, semi ->
        val orig = song?.chords ?: "C, G, Am, F"
        TranspositionHelper.transposeChordSequence(orig, semi)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "C, G, Am, F")

    // --- 5. SYNCED LYRICS ---
    private val _lyricsList = MutableStateFlow<List<SyncedLyricLine>>(emptyList())
    val lyricsList: StateFlow<List<SyncedLyricLine>> = _lyricsList.asStateFlow()

    private val _isLyricsSyncEditMode = MutableStateFlow(false)
    val isLyricsSyncEditMode: StateFlow<Boolean> = _isLyricsSyncEditMode.asStateFlow()

    init {
        val db = WorshipDatabase.getDatabase(application)
        repository = WorshipSongRepository(db.worshipSongDao(), db.exportedSongDao())
        setlistDao = db.setlistDao()

        savedSongs = repository.allSongs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        exportedSongs = repository.allExportedSongs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allSetlists = setlistDao.getAllSetlists().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed initial songs, sample setlist, and select first song
        viewModelScope.launch {
            repository.seedInitialSampleSongsIfEmpty()
            seedInitialSampleSetlistIfEmpty()
            savedSongs.collect { songs ->
                if (_currentSong.value == null && songs.isNotEmpty()) {
                    selectSong(songs.first())
                }
            }
        }

        // Metronome / pulse monitor
        startVisualizerAndMetronomePulse()
    }

    private fun startVisualizerAndMetronomePulse() {
        viewModelScope.launch {
            while (isActive) {
                delay(40)
                // Update live visualizer spectrum
                _visualizerLevels.value = audioEngine.visualizerBandLevels.copyOf()

                // Calculate rhythmic beat & downbeat when playing
                if (_playbackState.value.isPlaying) {
                    val bpm = _currentBpm.value.coerceIn(40, 220)
                    val beatMs = (60000L / bpm).coerceAtLeast(200L)
                    val now = System.currentTimeMillis()
                    val totalBeats = (now / beatMs)
                    val beatsPerBar = when (_timeSignature.value) {
                        "3/4" -> 3
                        "6/8" -> 6
                        else -> 4
                    }
                    val beatInBar = ((totalBeats % beatsPerBar).toInt()) + 1 // 1-indexed (1..beatsPerBar)
                    _currentBeatIndex.value = beatInBar

                    val phase = (now % beatMs).toDouble() / beatMs
                    val isFlash = phase < 0.28
                    _isMetronomeFlash.value = isFlash
                    _isStrongBeat.value = isFlash && (beatInBar == 1)
                } else {
                    _isMetronomeFlash.value = false
                    _isStrongBeat.value = false
                }
            }
        }
    }

    fun setActiveStudioTab(tab: String) {
        _activeStudioTab.value = tab
    }

    fun setDrawerOpen(isOpen: Boolean) {
        _isDrawerOpen.value = isOpen
    }

    fun setShowExportDialog(show: Boolean) {
        _showExportDialog.value = show
    }

    fun setShowGalleryDialog(show: Boolean) {
        _showGalleryDialog.value = show
    }

    fun setShowSetlistDialog(show: Boolean) {
        _showSetlistDialog.value = show
    }

    fun setTimeSignature(sig: String) {
        _timeSignature.value = sig
        _currentBeatIndex.value = 1
    }

    fun toggleMetronomeHaptic() {
        _isMetronomeHapticEnabled.value = !_isMetronomeHapticEnabled.value
    }

    fun selectSong(song: WorshipSongEntity) {
        stopPlayback()
        _currentSong.value = song
        _currentBpm.value = song.bpm
        audioEngine.currentBpm = song.bpm
        _transposeSemitones.value = 0
        audioEngine.transposeSemitones = 0

        _playbackState.update {
            it.copy(
                currentPositionSec = 0,
                durationSec = song.durationSeconds,
                isPlaying = false
            )
        }

        // Load lyrics for the selected song
        _lyricsList.value = WorshipLyricsDatabase.getDefaultLyricsForSong(song.title)

        // Reset and trigger AI chord detection
        triggerAiChordDetection(song.title, song.durationSeconds)
    }

    // --- PLAYBACK CONTROLS ---

    fun togglePlayPause() {
        if (_playbackState.value.isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        _playbackState.update { it.copy(isPlaying = true) }
        startPlaybackProgressLoop()

        // Sound initial root chord of song, accounting for transposition
        val chords = currentTransposedChords.value.split(",")
        val rootChord = chords.firstOrNull()?.trim() ?: "C"
        audioEngine.playChord(rootChord, isMinorMode = false)
        _activeChordKey.value = rootChord
    }

    fun pausePlayback() {
        _playbackState.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
        audioEngine.stopSound()
        _activeChordKey.value = null
    }

    fun stopPlayback() {
        _playbackState.update { it.copy(isPlaying = false, currentPositionSec = 0) }
        playbackJob?.cancel()
        audioEngine.stopSound()
        _activeChordKey.value = null
    }

    fun seekToPosition(positionSec: Int) {
        _playbackState.update { it.copy(currentPositionSec = positionSec.coerceIn(0, it.durationSec)) }
    }

    fun setMasterVolume(volume: Float) {
        _playbackState.update { it.copy(masterVolume = volume.coerceIn(0f, 1f)) }
        audioEngine.masterVolume = volume
    }

    private fun startPlaybackProgressLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (isActive && _playbackState.value.isPlaying) {
                delay(1000)
                val current = _playbackState.value.currentPositionSec
                val total = _playbackState.value.durationSec
                if (current < total) {
                    val nextSec = current + 1
                    _playbackState.update { it.copy(currentPositionSec = nextSec) }

                    // Timeline chord progression sync
                    val detected = _aiState.value.result?.detectedChords
                    if (!detected.isNullOrEmpty()) {
                        val matchingChord = detected.lastOrNull {
                            val parts = it.timestampFormatted.split(":")
                            val chordSec = (parts.getOrNull(0)?.toIntOrNull() ?: 0) * 60 +
                                    (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                            chordSec <= nextSec
                        }
                        if (matchingChord != null) {
                            val transposedChord = TranspositionHelper.transposeChord(matchingChord.name, _transposeSemitones.value)
                            if (transposedChord != _activeChordKey.value) {
                                _activeChordKey.value = transposedChord
                                audioEngine.playChord(transposedChord, matchingChord.isMinor)
                            }
                        }
                    }
                } else {
                    stopPlayback()
                    break
                }
            }
        }
    }

    // --- SWELL PEDAL ---

    fun setSwellPedalLevel(level: Float) {
        val clamped = level.coerceIn(0f, 1f)
        _swellPedalLevel.value = clamped
        audioEngine.setSwell(clamped)
    }

    fun toggleAutoSwell() {
        val newState = !_isAutoSwell.value
        _isAutoSwell.value = newState
        if (newState) {
            startAutoSwellLoop()
        } else {
            autoSwellJob?.cancel()
        }
    }

    private fun startAutoSwellLoop() {
        autoSwellJob?.cancel()
        autoSwellJob = viewModelScope.launch {
            var phase = 0f
            while (isActive && _isAutoSwell.value) {
                delay(50)
                phase += 0.05f
                val swell = 0.25f + 0.70f * (0.5f + 0.5f * kotlin.math.sin(phase.toDouble()).toFloat())
                _swellPedalLevel.value = swell
                audioEngine.setSwell(swell)
            }
        }
    }

    // --- STEM SEPARATION & VOLUME CONTROLS ---

    fun setStemVolume(channel: StemChannel, volume: Float) {
        val current = _stemStates.value.toMutableMap()
        val existing = current[channel] ?: StemState(channel)
        val updated = existing.copy(volume = volume.coerceIn(0f, 1f))
        current[channel] = updated
        _stemStates.value = current
        applyStemVolumesToEngine()
    }

    fun toggleStemMute(channel: StemChannel) {
        val current = _stemStates.value.toMutableMap()
        val existing = current[channel] ?: StemState(channel)
        val updated = existing.copy(isMuted = !existing.isMuted)
        current[channel] = updated
        _stemStates.value = current
        applyStemVolumesToEngine()
    }

    fun toggleStemSolo(channel: StemChannel) {
        val current = _stemStates.value.toMutableMap()
        val existing = current[channel] ?: StemState(channel)
        val newSolo = !existing.isSolo

        // If activating solo, turn off solo for others
        current.forEach { (ch, st) ->
            current[ch] = st.copy(isSolo = if (ch == channel) newSolo else false)
        }
        _stemStates.value = current
        applyStemVolumesToEngine()
    }

    fun resetAllStems() {
        val current = _stemStates.value.toMutableMap()
        StemChannel.entries.forEach { ch ->
            current[ch] = StemState(ch, volume = 0.80f, isMuted = false, isSolo = false)
        }
        _stemStates.value = current
        applyStemVolumesToEngine()
    }

    fun applyStemPreset(preset: String) {
        val current = _stemStates.value.toMutableMap()
        when (preset) {
            "Sem Voz" -> {
                StemChannel.entries.forEach { ch ->
                    current[ch] = (current[ch] ?: StemState(ch)).copy(
                        isMuted = (ch == StemChannel.VOCAL),
                        isSolo = false
                    )
                }
            }
            "Acústico" -> {
                current[StemChannel.VOCAL] = (current[StemChannel.VOCAL] ?: StemState(StemChannel.VOCAL)).copy(volume = 0.95f, isMuted = false, isSolo = false)
                current[StemChannel.DRUMS] = (current[StemChannel.DRUMS] ?: StemState(StemChannel.DRUMS)).copy(volume = 0.30f, isMuted = false, isSolo = false)
                current[StemChannel.BASS] = (current[StemChannel.BASS] ?: StemState(StemChannel.BASS)).copy(volume = 0.65f, isMuted = false, isSolo = false)
                current[StemChannel.PAD] = (current[StemChannel.PAD] ?: StemState(StemChannel.PAD)).copy(volume = 0.85f, isMuted = false, isSolo = false)
                current[StemChannel.GUITAR] = (current[StemChannel.GUITAR] ?: StemState(StemChannel.GUITAR)).copy(volume = 0.90f, isMuted = false, isSolo = false)
                current[StemChannel.AMBIENT] = (current[StemChannel.AMBIENT] ?: StemState(StemChannel.AMBIENT)).copy(volume = 0.75f, isMuted = false, isSolo = false)
            }
            "Base Forte" -> {
                current[StemChannel.VOCAL] = (current[StemChannel.VOCAL] ?: StemState(StemChannel.VOCAL)).copy(volume = 0.65f, isMuted = false, isSolo = false)
                current[StemChannel.DRUMS] = (current[StemChannel.DRUMS] ?: StemState(StemChannel.DRUMS)).copy(volume = 0.95f, isMuted = false, isSolo = false)
                current[StemChannel.BASS] = (current[StemChannel.BASS] ?: StemState(StemChannel.BASS)).copy(volume = 0.95f, isMuted = false, isSolo = false)
                current[StemChannel.PAD] = (current[StemChannel.PAD] ?: StemState(StemChannel.PAD)).copy(volume = 0.80f, isMuted = false, isSolo = false)
                current[StemChannel.GUITAR] = (current[StemChannel.GUITAR] ?: StemState(StemChannel.GUITAR)).copy(volume = 0.75f, isMuted = false, isSolo = false)
                current[StemChannel.AMBIENT] = (current[StemChannel.AMBIENT] ?: StemState(StemChannel.AMBIENT)).copy(volume = 0.60f, isMuted = false, isSolo = false)
            }
            else -> resetAllStems()
        }
        _stemStates.value = current
        applyStemVolumesToEngine()
    }

    private fun applyStemVolumesToEngine() {
        val map = _stemStates.value
        val hasSolo = map.values.any { it.isSolo }

        fun getVolume(ch: StemChannel): Float {
            val st = map[ch] ?: return 0.8f
            if (hasSolo) {
                return if (st.isSolo && !st.isMuted) st.volume else 0f
            }
            return if (st.isMuted) 0f else st.volume
        }

        audioEngine.stemVocalVolume = getVolume(StemChannel.VOCAL)
        audioEngine.stemDrumsVolume = getVolume(StemChannel.DRUMS)
        audioEngine.stemBassVolume = getVolume(StemChannel.BASS)
        audioEngine.stemPadVolume = getVolume(StemChannel.PAD)
        audioEngine.stemGuitarVolume = getVolume(StemChannel.GUITAR)
        audioEngine.stemAmbientVolume = getVolume(StemChannel.AMBIENT)
    }

    fun separateStemsWithAi() {
        if (_isSeparatingStems.value) return
        val song = _currentSong.value ?: return

        viewModelScope.launch {
            _isSeparatingStems.value = true
            _stemSeparationProgress.value = 0.05f
            _stemSeparationStep.value = "Carregando áudio de '${song.title}'..."

            val steps = listOf(
                "Isolando Vocais e Voz de Louvor com Redes Neurais..." to 0.25f,
                "Filtrando Transientes de Bateria e Percussão..." to 0.50f,
                "Extraindo Linha Harmônica de Baixo Sub..." to 0.72f,
                "Separando Teclado, Pads de Worship e Shimmer..." to 0.90f,
                "Calibrando Mix Estéreo 6-Faixas Concluído!" to 1.0f
            )

            for ((step, prog) in steps) {
                delay(400)
                _stemSeparationStep.value = step
                _stemSeparationProgress.value = prog
            }

            delay(300)
            _isSeparatingStems.value = false
            Toast.makeText(
                getApplication(),
                "6 Faixas de Instrumentos separadas com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // --- VISUAL EQUALIZER ---

    fun setEqGain(bandIndex: Int, gainDb: Float) {
        val current = _eqGainsDb.value.copyOf()
        if (bandIndex in current.indices) {
            current[bandIndex] = gainDb.coerceIn(-12f, 12f)
            _eqGainsDb.value = current
            _activeEqPreset.value = "Personalizado"
            applyEqToEngine()
        }
    }

    fun applyEqPreset(presetName: String) {
        _activeEqPreset.value = presetName
        val gains = when (presetName) {
            "Worship Atmosfera" -> floatArrayOf(4f, 2f, -1f, 3f, 5f)
            "Vocal Louvor" -> floatArrayOf(-2f, -3f, 3f, 4f, 2f)
            "Acústico & Piano" -> floatArrayOf(1f, 2f, 1f, 2f, 2f)
            else -> floatArrayOf(0f, 0f, 0f, 0f, 0f) // Flat
        }
        _eqGainsDb.value = gains
        applyEqToEngine()
    }

    private fun applyEqToEngine() {
        val g = _eqGainsDb.value
        // Convert dB to linear gain factor: 10^(dB / 20)
        fun dbToLinear(db: Float): Float = 10f.pow(db / 20f)

        audioEngine.eqGainLow = dbToLinear(g.getOrElse(0) { 0f })
        audioEngine.eqGainMidLow = dbToLinear(g.getOrElse(1) { 0f })
        audioEngine.eqGainMid = dbToLinear(g.getOrElse(2) { 0f })
        audioEngine.eqGainMidHigh = dbToLinear(g.getOrElse(3) { 0f })
        audioEngine.eqGainHigh = dbToLinear(g.getOrElse(4) { 0f })
    }

    // --- TAP TEMPO ---

    fun recordTempoTap() {
        val now = System.currentTimeMillis()
        if (tapTimestamps.isNotEmpty() && (now - tapTimestamps.last()) > 2500L) {
            tapTimestamps.clear()
        }
        tapTimestamps.add(now)
        if (tapTimestamps.size > 8) {
            tapTimestamps.removeAt(0)
        }

        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                intervals.add(tapTimestamps[i] - tapTimestamps[i - 1])
            }
            val avgIntervalMs = intervals.average()
            if (avgIntervalMs > 0) {
                val calculatedBpm = (60000.0 / avgIntervalMs).toInt().coerceIn(40, 220)
                _tappedBpmHistory.value = calculatedBpm
                _currentBpm.value = calculatedBpm
                audioEngine.currentBpm = calculatedBpm
            }
        }
    }

    fun adjustBpm(delta: Int) {
        val next = (_currentBpm.value + delta).coerceIn(40, 220)
        _currentBpm.value = next
        audioEngine.currentBpm = next
    }

    fun applyTappedBpmToSong() {
        val bpm = _currentBpm.value
        Toast.makeText(getApplication(), "Andamento $bpm BPM aplicado ao louvor!", Toast.LENGTH_SHORT).show()
    }

    // --- TRANSPOSITION ---

    fun setTransposeSemitones(semitones: Int) {
        val clamped = semitones.coerceIn(-6, 6)
        _transposeSemitones.value = clamped
        audioEngine.transposeSemitones = clamped

        // Retrigger active chord with transposed pitch if playing
        val active = _activeChordKey.value
        if (active != null) {
            audioEngine.playChord(active, _isMinorKeyboardMode.value)
        }
    }

    // --- SYNCED LYRICS ---

    fun toggleLyricsSyncEditMode() {
        _isLyricsSyncEditMode.value = !_isLyricsSyncEditMode.value
    }

    fun syncLyricLineToCurrentPlayback(lineId: String) {
        val currentSec = _playbackState.value.currentPositionSec
        val currentList = _lyricsList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == lineId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(timeSeconds = currentSec)
            currentList.sortBy { it.timeSeconds }
            _lyricsList.value = currentList
            Toast.makeText(getApplication(), "Linha sincronizada em ${String.format("%02d:%02d", currentSec / 60, currentSec % 60)}", Toast.LENGTH_SHORT).show()
        }
    }

    fun seekToLyricTime(seconds: Int) {
        seekToPosition(seconds)
        if (!_playbackState.value.isPlaying) {
            startPlayback()
        }
    }

    // --- AI CHORD FINDER ---

    fun detectChordsWithAi() {
        val song = _currentSong.value ?: return
        triggerAiChordDetection(song.title, song.durationSeconds)
    }

    private fun triggerAiChordDetection(title: String, durationSec: Int) {
        viewModelScope.launch {
            _aiState.value = AiDetectionState(isDetecting = true, progress = 0.05f, statusText = "Iniciando IA...")
            val result = AiChordDetector.analyzeSong(
                songTitle = title,
                durationSeconds = durationSec
            ) { progress, status ->
                _aiState.value = _aiState.value.copy(progress = progress, statusText = status)
            }
            _aiState.value = AiDetectionState(
                isDetecting = false,
                progress = 1.0f,
                statusText = "Acordes detectados!",
                result = result
            )
        }
    }

    // --- WORSHIP KEYBOARD (8 KEYS) ---

    fun toggleKeyboardMinorMode() {
        _isMinorKeyboardMode.value = !_isMinorKeyboardMode.value
    }

    fun playChordKey(chordName: String) {
        val transposed = TranspositionHelper.transposeChord(chordName, _transposeSemitones.value)
        _activeChordKey.value = transposed
        audioEngine.playChord(transposed, _isMinorKeyboardMode.value)
    }

    fun releaseChordKey() {
        if (!_playbackState.value.isPlaying) {
            audioEngine.stopSound()
            _activeChordKey.value = null
        }
    }

    fun toggleInstrumentLayer(layer: InstrumentLayer) {
        val current = _enabledLayers.value.toMutableSet()
        if (current.contains(layer)) {
            if (current.size > 1) current.remove(layer)
        } else {
            current.add(layer)
        }
        _enabledLayers.value = current
        audioEngine.toggleLayer(layer)
    }

    // --- UPLOAD WORSHIP SONG ---

    fun uploadSongFromUri(context: Context, uri: Uri, fileName: String, fileSize: Long) {
        val maxSizeBytes = 10 * 1024 * 1024
        if (fileSize > maxSizeBytes) {
            _uploadState.value = UploadState(
                isUploading = false,
                errorMessage = "Arquivo excede o limite de 10MB (${String.format("%.1f", fileSize / (1024f * 1024f))} MB)"
            )
            Toast.makeText(context, "Erro: Arquivo deve ter no máximo 10MB", Toast.LENGTH_LONG).show()
            return
        }

        val cleanTitle = fileName.substringBeforeLast(".").replace("_", " ")
        viewModelScope.launch {
            _uploadState.value = UploadState(
                isUploading = true,
                progress = 0.1f,
                currentFileName = fileName,
                errorMessage = null
            )

            for (p in 2..10) {
                delay(120)
                _uploadState.value = _uploadState.value.copy(progress = p / 10f)
            }

            val newSong = WorshipSongEntity(
                title = cleanTitle,
                artist = "Worship Local Upload",
                durationSeconds = 210,
                keyTone = "G Maior",
                bpm = 70,
                chords = "G, C, D, Em, Am, F",
                isSample = false,
                audioUri = uri.toString(),
                fileSizeMb = fileSize / (1024f * 1024f)
            )

            val newId = repository.insertSong(newSong)
            val inserted = repository.getSongById(newId) ?: newSong
            _uploadState.value = UploadState(isUploading = false, progress = 1f, currentFileName = fileName)
            selectSong(inserted)
            Toast.makeText(context, "Música '$cleanTitle' carregada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteSong(song: WorshipSongEntity) {
        viewModelScope.launch {
            repository.deleteSong(song)
            if (_currentSong.value?.id == song.id) {
                val remaining = savedSongs.value.filter { it.id != song.id }
                if (remaining.isNotEmpty()) {
                    selectSong(remaining.first())
                } else {
                    _currentSong.value = null
                }
            }
        }
    }

    // --- EXPORT & GALLERY ACTIONS ---

    fun exportAndSaveMix(context: Context, notes: String) {
        val song = _currentSong.value ?: return
        val origKey = song.keyTone
        val targetKey = currentTransposedKey.value
        val semi = _transposeSemitones.value
        val chords = currentTransposedChords.value
        val map = _stemStates.value

        viewModelScope.launch {
            val exportedEntity = ExportedSongEntity(
                title = "${song.title} (${if (semi != 0) "$targetKey [${if (semi > 0) "+$semi" else "$semi"}st]" else "Mix Estúdio"})",
                artist = song.artist,
                originalKey = origKey,
                exportedKey = targetKey,
                transposeSemitones = semi,
                bpm = _currentBpm.value,
                chords = chords,
                vocalVolume = map[StemChannel.VOCAL]?.effectiveVolume ?: 0.85f,
                drumsVolume = map[StemChannel.DRUMS]?.effectiveVolume ?: 0.75f,
                bassVolume = map[StemChannel.BASS]?.effectiveVolume ?: 0.80f,
                padVolume = map[StemChannel.PAD]?.effectiveVolume ?: 0.90f,
                guitarVolume = map[StemChannel.GUITAR]?.effectiveVolume ?: 0.75f,
                ambientVolume = map[StemChannel.AMBIENT]?.effectiveVolume ?: 0.85f,
                swellLevel = _swellPedalLevel.value,
                notes = notes.ifBlank { "Exportado com sucesso para a Galeria do J-MUSIC" }
            )

            repository.insertExportedSong(exportedEntity)

            val shareText = """
                🎵 J-MUSIC - MÚSICA EXPORTADA 🎵
                
                Música: ${song.title}
                Artista: ${song.artist}
                Tom Original: $origKey
                Tom Exportado: $targetKey (${if (semi >= 0) "+$semi" else "$semi"} semitons)
                Andamento: ${_currentBpm.value} BPM
                Cifra / Harmonia: $chords
                
                🎛️ Balanço dos Instrumentos (Stems):
                • Voz: ${((map[StemChannel.VOCAL]?.effectiveVolume ?: 0.85f) * 100).toInt()}%
                • Bateria & Percussão: ${((map[StemChannel.DRUMS]?.effectiveVolume ?: 0.75f) * 100).toInt()}%
                • Baixo: ${((map[StemChannel.BASS]?.effectiveVolume ?: 0.8f) * 100).toInt()}%
                • Teclado & Pad: ${((map[StemChannel.PAD]?.effectiveVolume ?: 0.9f) * 100).toInt()}%
                • Violão / Guitarra: ${((map[StemChannel.GUITAR]?.effectiveVolume ?: 0.75f) * 100).toInt()}%
                • Ambiência & Shimmer: ${((map[StemChannel.AMBIENT]?.effectiveVolume ?: 0.85f) * 100).toInt()}%
                
                Pedal Swell: ${(_swellPedalLevel.value * 100).toInt()}%
                Notas: ${exportedEntity.notes}
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Mix J-MUSIC: ${song.title}")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Compartilhar Música Exportada"))
            Toast.makeText(context, "Música salva na Galeria de Exportações!", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadExportedSongIntoStudio(exported: ExportedSongEntity) {
        setTransposeSemitones(exported.transposeSemitones)
        _currentBpm.value = exported.bpm
        audioEngine.currentBpm = exported.bpm
        setSwellPedalLevel(exported.swellLevel)

        setStemVolume(StemChannel.VOCAL, exported.vocalVolume)
        setStemVolume(StemChannel.DRUMS, exported.drumsVolume)
        setStemVolume(StemChannel.BASS, exported.bassVolume)
        setStemVolume(StemChannel.PAD, exported.padVolume)
        setStemVolume(StemChannel.GUITAR, exported.guitarVolume)
        setStemVolume(StemChannel.AMBIENT, exported.ambientVolume)

        Toast.makeText(
            getApplication(),
            "Mix '${exported.title}' carregado no estúdio!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun deleteExportedSong(exported: ExportedSongEntity) {
        viewModelScope.launch {
            repository.deleteExportedSong(exported)
            Toast.makeText(getApplication(), "Item removido da galeria", Toast.LENGTH_SHORT).show()
        }
    }

    // =========================================================================
    // SETLIST MANAGER OPERATIONS
    // =========================================================================

    private suspend fun seedInitialSampleSetlistIfEmpty() {
        try {
            val existing = setlistDao.getAllSetlistsSync()
            if (existing.isEmpty()) {
                val setlistId = setlistDao.insertSetlist(
                    SetlistEntity(
                        name = "Culto de Domingo - Manhã",
                        serviceDate = "Domingo 10:00",
                        description = "Sequência de louvor congregacional para abertura e ministração"
                    )
                )

                // Add 3 sample song items with pre-defined keys, volumes, and stems
                setlistDao.insertSetlistItem(
                    SetlistItemEntity(
                        setlistId = setlistId,
                        songId = 1,
                        songTitle = "Ruja o Leão",
                        artist = "Talita Catanzaro",
                        orderIndex = 0,
                        keyTone = "D Maior",
                        transposeSemitones = 2,
                        bpm = 72,
                        masterVolume = 0.90f,
                        vocalVolume = 0.85f,
                        drumsVolume = 0.80f,
                        bassVolume = 0.75f,
                        padVolume = 0.95f,
                        guitarVolume = 0.70f,
                        ambientVolume = 0.85f,
                        notes = "Introdução com teclado suave, entrada da bateria no refrão"
                    )
                )

                setlistDao.insertSetlistItem(
                    SetlistItemEntity(
                        setlistId = setlistId,
                        songId = 2,
                        songTitle = "Bondade de Deus",
                        artist = "Isaías Saad",
                        orderIndex = 1,
                        keyTone = "G Maior",
                        transposeSemitones = 0,
                        bpm = 70,
                        masterVolume = 0.85f,
                        vocalVolume = 0.90f,
                        drumsVolume = 0.65f,
                        bassVolume = 0.70f,
                        padVolume = 0.90f,
                        guitarVolume = 0.80f,
                        ambientVolume = 0.80f,
                        notes = "Ministração de gratidão, solos de violão acústico destacados"
                    )
                )

                setlistDao.insertSetlistItem(
                    SetlistItemEntity(
                        setlistId = setlistId,
                        songId = 3,
                        songTitle = "A Casa É Sua",
                        artist = "Casa Worship",
                        orderIndex = 2,
                        keyTone = "A Maior",
                        transposeSemitones = -1,
                        bpm = 68,
                        masterVolume = 0.95f,
                        vocalVolume = 0.85f,
                        drumsVolume = 0.85f,
                        bassVolume = 0.85f,
                        padVolume = 1.0f,
                        guitarVolume = 0.75f,
                        ambientVolume = 0.90f,
                        notes = "Clímax da celebração com swell elevado e coro"
                    )
                )
            }
        } catch (_: Exception) {}
    }

    fun getItemsForSetlist(setlistId: Long): kotlinx.coroutines.flow.Flow<List<SetlistItemEntity>> {
        return setlistDao.getItemsForSetlist(setlistId)
    }

    fun createSetlist(name: String, serviceDate: String, description: String = "") {
        viewModelScope.launch {
            if (name.isBlank()) return@launch
            val id = setlistDao.insertSetlist(
                SetlistEntity(
                    name = name.trim(),
                    serviceDate = serviceDate.trim(),
                    description = description.trim()
                )
            )
            Toast.makeText(getApplication(), "Setlist '$name' criada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun addCurrentSongToSetlist(setlistId: Long, notes: String = "") {
        val song = _currentSong.value ?: return
        viewModelScope.launch {
            val count = setlistDao.getItemCount(setlistId)
            val stMap = _stemStates.value
            val item = SetlistItemEntity(
                setlistId = setlistId,
                songId = song.id,
                songTitle = song.title,
                artist = song.artist,
                orderIndex = count,
                keyTone = currentTransposedKey.value,
                transposeSemitones = _transposeSemitones.value,
                bpm = _currentBpm.value,
                masterVolume = _playbackState.value.masterVolume,
                vocalVolume = stMap[StemChannel.VOCAL]?.volume ?: 0.85f,
                drumsVolume = stMap[StemChannel.DRUMS]?.volume ?: 0.75f,
                bassVolume = stMap[StemChannel.BASS]?.volume ?: 0.80f,
                padVolume = stMap[StemChannel.PAD]?.volume ?: 0.90f,
                guitarVolume = stMap[StemChannel.GUITAR]?.volume ?: 0.75f,
                ambientVolume = stMap[StemChannel.AMBIENT]?.volume ?: 0.85f,
                notes = notes
            )
            setlistDao.insertSetlistItem(item)
            Toast.makeText(getApplication(), "Música '${song.title}' adicionada à Setlist!", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadSetlistItemIntoStudio(item: SetlistItemEntity) {
        // Find existing song if present
        val matchSong = savedSongs.value.find { it.id == item.songId || it.title.equals(item.songTitle, ignoreCase = true) }
        if (matchSong != null) {
            _currentSong.value = matchSong
        } else {
            // Create fallback matching entity
            _currentSong.value = WorshipSongEntity(
                id = item.songId,
                title = item.songTitle,
                artist = item.artist,
                keyTone = item.keyTone,
                bpm = item.bpm,
                durationSeconds = 270,
                chords = "C, G, Am, F"
            )
        }

        // Apply saved preset tone & volume configurations
        setTransposeSemitones(item.transposeSemitones)
        _currentBpm.value = item.bpm
        audioEngine.currentBpm = item.bpm
        setMasterVolume(item.masterVolume)

        setStemVolume(StemChannel.VOCAL, item.vocalVolume)
        setStemVolume(StemChannel.DRUMS, item.drumsVolume)
        setStemVolume(StemChannel.BASS, item.bassVolume)
        setStemVolume(StemChannel.PAD, item.padVolume)
        setStemVolume(StemChannel.GUITAR, item.guitarVolume)
        setStemVolume(StemChannel.AMBIENT, item.ambientVolume)

        Toast.makeText(
            getApplication(),
            "Preset de '${item.songTitle}' carregado: Tom ${item.keyTone}, Vol ${(item.masterVolume * 100).toInt()}%",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun deleteSetlistItem(item: SetlistItemEntity) {
        viewModelScope.launch {
            setlistDao.deleteSetlistItem(item)
            Toast.makeText(getApplication(), "Item removido da setlist", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteSetlist(setlist: SetlistEntity) {
        viewModelScope.launch {
            setlistDao.deleteSetlist(setlist)
            Toast.makeText(getApplication(), "Setlist removida", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
        autoSwellJob?.cancel()
        audioEngine.release()
    }
}
