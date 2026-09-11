package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin

enum class InstrumentLayer(val label: String, val icon: String) {
    AMBIENT_PAD("Pad Worship", "🎛️"),
    ACOUSTIC_PIANO("Piano Acústico", "🎹"),
    SHIMMER_STRINGS("Cordas Shimmer", "🎻"),
    WORSHIP_CHOIR("Voz Celestial", "🕊️")
}

data class ChordNoteFrequencies(
    val chordName: String,
    val isMinor: Boolean,
    val frequencies: List<Float> // Base frequencies in Hz
)

class WorshipAudioEngine {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    private var audioJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    // Master & Swell parameters
    @Volatile var masterVolume: Float = 0.85f
    @Volatile var swellPedalLevel: Float = 0.75f // 0.0f (mute/closed) to 1.0f (full open swell)

    // Transposition in semitones (-6 to +6)
    @Volatile var transposeSemitones: Int = 0

    // BPM for rhythmic pulse & drum timing
    @Volatile var currentBpm: Int = 72

    // 6-Stem Mixer Volumes (0.0f .. 1.0f)
    @Volatile var stemVocalVolume: Float = 0.85f
    @Volatile var stemDrumsVolume: Float = 0.75f
    @Volatile var stemBassVolume: Float = 0.80f
    @Volatile var stemPadVolume: Float = 0.90f
    @Volatile var stemGuitarVolume: Float = 0.75f
    @Volatile var stemAmbientVolume: Float = 0.85f

    // Equalizer Band Gains (-12dB to +12dB converted to linear scale: 0.25f to 4.0f)
    @Volatile var eqGainLow: Float = 1.0f     // 60 Hz Sub/Bass
    @Volatile var eqGainMidLow: Float = 1.0f  // 250 Hz Low-Mid
    @Volatile var eqGainMid: Float = 1.0f     // 1 kHz Mid
    @Volatile var eqGainMidHigh: Float = 1.0f // 4 kHz Presence
    @Volatile var eqGainHigh: Float = 1.0f    // 12 kHz Shimmer/Air

    // Live Audio Visualizer / Spectrum amplitudes for UI (5 bands: 0.0f..1.0f)
    @Volatile var visualizerBandLevels = floatArrayOf(0.1f, 0.1f, 0.1f, 0.1f, 0.1f)

    @Volatile var activeLayers = mutableSetOf(
        InstrumentLayer.AMBIENT_PAD,
        InstrumentLayer.ACOUSTIC_PIANO,
        InstrumentLayer.SHIMMER_STRINGS
    )

    // Current sounding chord state
    @Volatile private var activeChord: ChordNoteFrequencies? = null
    @Volatile private var chordTriggerTimeSamples: Long = 0
    @Volatile private var isSounding: Boolean = false
    @Volatile private var currentSmoothedVolume: Float = 0.75f

    companion object {
        private const val TAG = "WorshipAudioEngine"

        val CHORD_DEFINITIONS = mapOf(
            // Major variations
            "C" to listOf(130.81f, 196.00f, 261.63f, 329.63f, 392.00f, 523.25f), // C3, G3, C4, E4, G4, C5
            "G" to listOf(98.00f, 146.83f, 196.00f, 246.94f, 293.66f, 392.00f),  // G2, D3, G3, B3, D4, G4
            "D" to listOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f), // D3, A3, D4, F#4, A4, D5
            "A" to listOf(110.00f, 164.81f, 220.00f, 277.18f, 329.63f, 440.00f), // A2, E3, A3, C#4, E4, A4
            "Em" to listOf(82.41f, 123.47f, 164.81f, 196.00f, 246.94f, 329.63f), // E2, B2, E3, G3, B3, E4
            "Am" to listOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 440.00f), // A2, E3, A3, C4, E4, A4
            "Dm" to listOf(146.83f, 220.00f, 293.66f, 349.23f, 440.00f, 587.33f), // D3, A3, D4, F4, A4, D5
            "F" to listOf(87.31f, 130.81f, 174.61f, 220.00f, 261.63f, 349.23f),  // F2, C3, F3, A3, C4, F4

            // Minor inverted / alternative versions for toggle
            "Cm" to listOf(130.81f, 196.00f, 261.63f, 311.13f, 392.00f, 523.25f), // C Eb G
            "Gm" to listOf(98.00f, 146.83f, 196.00f, 233.08f, 293.66f, 392.00f),  // G Bb D
            "D_min" to listOf(146.83f, 220.00f, 293.66f, 349.23f, 440.00f, 587.33f),
            "A_min" to listOf(110.00f, 164.81f, 220.00f, 261.63f, 329.63f, 440.00f),
            "E" to listOf(82.41f, 123.47f, 164.81f, 207.65f, 246.94f, 329.63f),   // E Major
            "A_maj" to listOf(110.00f, 164.81f, 220.00f, 277.18f, 329.63f, 440.00f),
            "D_maj" to listOf(146.83f, 220.00f, 293.66f, 369.99f, 440.00f, 587.33f),
            "Fm" to listOf(87.31f, 130.81f, 174.61f, 207.65f, 261.63f, 349.23f)
        )
    }

    init {
        initAudioTrack()
    }

    private fun initAudioTrack() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(sampleRate / 4)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            startSynthesisLoop()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AudioTrack: ${e.message}")
        }
    }

    private fun startSynthesisLoop() {
        audioJob?.cancel()
        audioJob = scope.launch {
            val chunkSize = 512
            val buffer = ShortArray(chunkSize)
            var sampleIndex = 0L

            while (isActive) {
                val chord = activeChord
                val sounding = isSounding && chord != null
                val targetLevel = if (sounding) (swellPedalLevel * masterVolume) else 0f

                // Pitch multiplier based on semitone transposition: 2^(st/12)
                val pitchFactor = 2.0.pow(transposeSemitones.toDouble() / 12.0)

                // Beat length in seconds from current BPM
                val beatIntervalSec = 60.0 / currentBpm.coerceIn(40, 220).toDouble()

                var energyLow = 0.0
                var energyMidLow = 0.0
                var energyMid = 0.0
                var energyMidHigh = 0.0
                var energyHigh = 0.0

                // Smooth volume interpolation to avoid clicks/pops
                for (i in 0 until chunkSize) {
                    currentSmoothedVolume += (targetLevel - currentSmoothedVolume) * 0.004f

                    if (currentSmoothedVolume < 0.001f || chord == null) {
                        buffer[i] = 0
                    } else {
                        val t = sampleIndex.toDouble() / sampleRate
                        val noteAge = (sampleIndex - chordTriggerTimeSamples).toDouble() / sampleRate

                        var sampleSum = 0.0

                        // Base frequencies shifted by pitch factor
                        val rootFreq = (chord.frequencies.firstOrNull() ?: 130.81f) * pitchFactor.toFloat()

                        // --- STEM 1: Pad Layer (Worship Ambient pad) ---
                        if (activeLayers.contains(InstrumentLayer.AMBIENT_PAD) && stemPadVolume > 0.01f) {
                            val padEnvelope = (1.0 - exp(-noteAge * 1.8)).coerceIn(0.0, 1.0)
                            val padGain = stemPadVolume * eqGainMidLow * 0.14 * padEnvelope
                            for (baseFreq in chord.frequencies) {
                                val freq = baseFreq * pitchFactor.toFloat()
                                val osc1 = sin(2.0 * PI * freq * t)
                                val osc2 = sin(2.0 * PI * (freq * 1.003) * t) * 0.7
                                val sub = sin(2.0 * PI * (freq * 0.5) * t) * 0.35
                                val padVal = (osc1 + osc2 + sub) * padGain
                                sampleSum += padVal
                                energyMidLow += Math.abs(padVal)
                            }
                        }

                        // --- STEM 2: Piano & Acoustic Guitar Layer ---
                        if (activeLayers.contains(InstrumentLayer.ACOUSTIC_PIANO) && (stemGuitarVolume > 0.01f || stemPadVolume > 0.01f)) {
                            val pianoEnvelope = (exp(-noteAge * 1.4)).coerceIn(0.0, 1.0)
                            if (pianoEnvelope > 0.01) {
                                val pianoGain = ((stemGuitarVolume + stemPadVolume) * 0.5f) * eqGainMid * 0.16 * pianoEnvelope
                                for (baseFreq in chord.frequencies) {
                                    val freq = baseFreq * pitchFactor.toFloat()
                                    val f1 = sin(2.0 * PI * freq * t)
                                    val f2 = sin(2.0 * PI * (freq * 2.0) * t) * 0.4
                                    val f3 = sin(2.0 * PI * (freq * 3.0) * t) * 0.15
                                    val pianoVal = (f1 + f2 + f3) * pianoGain
                                    sampleSum += pianoVal
                                    energyMid += Math.abs(pianoVal)
                                }
                            }
                        }

                        // --- STEM 3: Shimmer Strings & Ambient Layer ---
                        if (activeLayers.contains(InstrumentLayer.SHIMMER_STRINGS) && stemAmbientVolume > 0.01f) {
                            val shimmerEnv = (1.0 - exp(-noteAge * 0.9)).coerceIn(0.0, 1.0)
                            val shimmerGain = stemAmbientVolume * eqGainHigh * 0.08 * shimmerEnv
                            for (baseFreq in chord.frequencies.takeLast(4)) {
                                val freq = baseFreq * pitchFactor.toFloat()
                                val shimFreq = freq * 2.0
                                val shimmerOsc = sin(2.0 * PI * shimFreq * t + sin(2.0 * PI * 1.5 * t) * 0.1)
                                val shimVal = shimmerOsc * shimmerGain
                                sampleSum += shimVal
                                energyHigh += Math.abs(shimVal)
                            }
                        }

                        // --- STEM 4: Worship Vocal Choir ---
                        if (activeLayers.contains(InstrumentLayer.WORSHIP_CHOIR) && stemVocalVolume > 0.01f) {
                            val vocalEnv = (1.0 - exp(-noteAge * 1.2)).coerceIn(0.0, 1.0)
                            val vocalGain = stemVocalVolume * eqGainMidHigh * 0.11 * vocalEnv
                            for (baseFreq in chord.frequencies.take(4)) {
                                val freq = baseFreq * pitchFactor.toFloat()
                                val formant = sin(2.0 * PI * freq * t) * 0.6 +
                                        sin(2.0 * PI * (freq * 1.5) * t) * 0.35
                                val vocalVal = formant * vocalGain
                                sampleSum += vocalVal
                                energyMidHigh += Math.abs(vocalVal)
                            }
                        }

                        // --- STEM 5: Bass Sub Line (Deep warm worship foundation) ---
                        if (stemBassVolume > 0.01f) {
                            val bassFreq = (rootFreq * 0.5f).coerceAtLeast(35f)
                            val bassEnv = (1.0 - exp(-noteAge * 2.5)).coerceIn(0.0, 1.0)
                            val bassGain = stemBassVolume * eqGainLow * 0.18 * bassEnv
                            val subOsc = sin(2.0 * PI * bassFreq * t) +
                                    sin(2.0 * PI * (bassFreq * 2.0) * t) * 0.25
                            val bassVal = subOsc * bassGain
                            sampleSum += bassVal
                            energyLow += Math.abs(bassVal)
                        }

                        // --- STEM 6: Worship Drums Pulse (Gentle heartbeat kick & rim click) ---
                        if (stemDrumsVolume > 0.01f) {
                            val beatTime = t % beatIntervalSec
                            val kickEnv = exp(-beatTime * 18.0) // quick percussive decay
                            if (kickEnv > 0.005) {
                                val kickFreq = 55.0 + 80.0 * kickEnv // pitch drop
                                val kickVal = sin(2.0 * PI * kickFreq * beatTime) * kickEnv * stemDrumsVolume * eqGainLow * 0.22
                                sampleSum += kickVal
                                energyLow += Math.abs(kickVal)
                            }
                        }

                        // Apply master smoothed volume & swell pedal gain
                        val finalSample = (sampleSum * currentSmoothedVolume)
                            .coerceIn(-0.95, 0.95)

                        buffer[i] = (finalSample * Short.MAX_VALUE).toInt().toShort()
                    }
                    sampleIndex++
                }

                // Update visualizer band levels for UI animation
                if (sounding) {
                    val scale = 1.0f / chunkSize
                    visualizerBandLevels = floatArrayOf(
                        (energyLow * scale * 2.5f).toFloat().coerceIn(0.08f, 1.0f),
                        (energyMidLow * scale * 2.5f).toFloat().coerceIn(0.08f, 1.0f),
                        (energyMid * scale * 2.5f).toFloat().coerceIn(0.08f, 1.0f),
                        (energyMidHigh * scale * 2.5f).toFloat().coerceIn(0.08f, 1.0f),
                        (energyHigh * scale * 2.5f).toFloat().coerceIn(0.08f, 1.0f)
                    )
                } else {
                    visualizerBandLevels = floatArrayOf(0.05f, 0.05f, 0.05f, 0.05f, 0.05f)
                }

                audioTrack?.write(buffer, 0, chunkSize)
            }
        }
    }

    fun playChord(chordKey: String, isMinorMode: Boolean = false) {
        val freqs = resolveChordFrequencies(chordKey, isMinorMode)
        activeChord = ChordNoteFrequencies(chordKey, isMinorMode, freqs)
        chordTriggerTimeSamples = (audioTrack?.playbackHeadPosition ?: 0).toLong()
        isSounding = true
    }

    fun stopSound() {
        isSounding = false
    }

    fun setSwell(level: Float) {
        swellPedalLevel = level.coerceIn(0f, 1f)
    }

    fun toggleLayer(layer: InstrumentLayer) {
        if (activeLayers.contains(layer)) {
            if (activeLayers.size > 1) {
                activeLayers.remove(layer)
            }
        } else {
            activeLayers.add(layer)
        }
    }

    private fun resolveChordFrequencies(chordKey: String, isMinorMode: Boolean): List<Float> {
        val cleanKey = chordKey.trim()
        val mappedKey = when {
            isMinorMode && cleanKey == "C" -> "Cm"
            isMinorMode && cleanKey == "G" -> "Gm"
            isMinorMode && cleanKey == "D" -> "Dm"
            isMinorMode && cleanKey == "A" -> "Am"
            !isMinorMode && cleanKey == "Em" -> "E"
            !isMinorMode && cleanKey == "Am" -> "A_maj"
            !isMinorMode && cleanKey == "Dm" -> "D_maj"
            isMinorMode && cleanKey == "F" -> "Fm"
            else -> cleanKey
        }

        return CHORD_DEFINITIONS[mappedKey]
            ?: CHORD_DEFINITIONS[cleanKey]
            ?: CHORD_DEFINITIONS["C"]!!
    }

    fun release() {
        try {
            audioJob?.cancel()
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing audio: ${e.message}")
        }
    }
}
