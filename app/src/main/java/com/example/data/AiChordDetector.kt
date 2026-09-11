package com.example.data

import kotlinx.coroutines.delay

data class DetectedChord(
    val id: Int,
    val name: String,
    val root: String,
    val type: String, // "Maior", "Menor", "Sus4"
    val timestampFormatted: String,
    val section: String,
    val colorHex: Long, // Visual accent color
    val isMinor: Boolean = false
)

data class ChordDetectionResult(
    val songTitle: String,
    val mainKey: String,
    val bpm: Int,
    val chordsSummary: List<String>,
    val detectedChords: List<DetectedChord>
)

object AiChordDetector {

    // Color palettes for chords (Moises style)
    private const val COLOR_EMERALD = 0xFF00E676
    private const val COLOR_MINT = 0xFF4ADE80
    private const val COLOR_CYAN = 0xFF06B6D4
    private const val COLOR_SKY = 0xFF38BDF8
    private const val COLOR_GOLD = 0xFFFBBF24
    private const val COLOR_ROSE = 0xFFF43F5E
    private const val COLOR_PURPLE = 0xFFA855F7
    private const val COLOR_INDIGO = 0xFF6366F1

    suspend fun analyzeSong(
        songTitle: String,
        durationSeconds: Int,
        onProgress: (Float, String) -> Unit
    ): ChordDetectionResult {
        // AI Audio Spectral Extraction simulation
        val steps = listOf(
            0.15f to "Separando hastes de áudio e frequências fundamentais...",
            0.35f to "Analisando tonalidade e cadência harmônica cristã...",
            0.60f to "Rede neural detectando progressão de acordes worship...",
            0.85f to "Mapeando seções: Intro, Verso, Refrão e Ponte...",
            1.00f to "Detecção concluída com sucesso!"
        )

        for ((progress, status) in steps) {
            delay(400)
            onProgress(progress, status)
        }

        // Generate tailored chords based on common worship anthems or song title
        val titleLower = songTitle.lowercase()
        return when {
            titleLower.contains("oceans") -> {
                ChordDetectionResult(
                    songTitle = songTitle,
                    mainKey = "D Maior",
                    bpm = 66,
                    chordsSummary = listOf("D", "A", "Em", "G", "Am"),
                    detectedChords = listOf(
                        DetectedChord(1, "D", "D", "Maior", "00:04", "Intro", COLOR_SKY, false),
                        DetectedChord(2, "A", "A", "Maior", "00:18", "Intro", COLOR_CYAN, false),
                        DetectedChord(3, "Em", "E", "Menor", "00:32", "Verso 1", COLOR_PURPLE, true),
                        DetectedChord(4, "G", "G", "Maior", "00:48", "Verso 1", COLOR_EMERALD, false),
                        DetectedChord(5, "D", "D", "Maior", "01:12", "Refrão", COLOR_SKY, false),
                        DetectedChord(6, "A", "A", "Maior", "01:28", "Refrão", COLOR_CYAN, false),
                        DetectedChord(7, "Em", "E", "Menor", "01:44", "Ponte", COLOR_PURPLE, true),
                        DetectedChord(8, "G", "G", "Maior", "02:05", "Ponte", COLOR_EMERALD, false),
                        DetectedChord(9, "Am", "A", "Menor", "02:30", "Outro", COLOR_ROSE, true)
                    )
                )
            }
            titleLower.contains("bondade") || titleLower.contains("goodness") -> {
                ChordDetectionResult(
                    songTitle = songTitle,
                    mainKey = "G Maior",
                    bpm = 70,
                    chordsSummary = listOf("G", "C", "D", "Em", "Am"),
                    detectedChords = listOf(
                        DetectedChord(1, "G", "G", "Maior", "00:03", "Intro", COLOR_EMERALD, false),
                        DetectedChord(2, "C", "C", "Maior", "00:15", "Verso 1", COLOR_MINT, false),
                        DetectedChord(3, "G", "G", "Maior", "00:30", "Verso 1", COLOR_EMERALD, false),
                        DetectedChord(4, "D", "D", "Maior", "00:45", "Pré-Refrão", COLOR_SKY, false),
                        DetectedChord(5, "Em", "E", "Menor", "01:00", "Refrão", COLOR_PURPLE, true),
                        DetectedChord(6, "C", "C", "Maior", "01:18", "Refrão", COLOR_MINT, false),
                        DetectedChord(7, "G", "G", "Maior", "01:34", "Refrão", COLOR_EMERALD, false),
                        DetectedChord(8, "D", "D", "Maior", "01:50", "Refrão", COLOR_SKY, false),
                        DetectedChord(9, "Am", "A", "Menor", "02:15", "Ponte", COLOR_ROSE, true)
                    )
                )
            }
            titleLower.contains("rompendo") -> {
                ChordDetectionResult(
                    songTitle = songTitle,
                    mainKey = "F Maior",
                    bpm = 74,
                    chordsSummary = listOf("F", "C", "Dm", "Bb", "Gm", "Am"),
                    detectedChords = listOf(
                        DetectedChord(1, "F", "F", "Maior", "00:05", "Intro", COLOR_GOLD, false),
                        DetectedChord(2, "C", "C", "Maior", "00:20", "Verso", COLOR_MINT, false),
                        DetectedChord(3, "Dm", "D", "Menor", "00:36", "Verso", COLOR_INDIGO, true),
                        DetectedChord(4, "F", "F", "Maior", "00:52", "Pré-Refrão", COLOR_GOLD, false),
                        DetectedChord(5, "C", "C", "Maior", "01:10", "Refrão", COLOR_MINT, false),
                        DetectedChord(6, "Dm", "D", "Menor", "01:28", "Refrão", COLOR_INDIGO, true),
                        DetectedChord(7, "Am", "A", "Menor", "01:50", "Ponte", COLOR_ROSE, true)
                    )
                )
            }
            else -> {
                // Default C / Worship Standard Anthem
                ChordDetectionResult(
                    songTitle = songTitle,
                    mainKey = "C Maior",
                    bpm = 72,
                    chordsSummary = listOf("C", "G", "D", "Am", "F", "Em"),
                    detectedChords = listOf(
                        DetectedChord(1, "C", "C", "Maior", "00:04", "Intro", COLOR_MINT, false),
                        DetectedChord(2, "G", "G", "Maior", "00:19", "Intro", COLOR_EMERALD, false),
                        DetectedChord(3, "Am", "A", "Menor", "00:35", "Verso", COLOR_ROSE, true),
                        DetectedChord(4, "F", "F", "Maior", "00:51", "Verso", COLOR_GOLD, false),
                        DetectedChord(5, "C", "C", "Maior", "01:12", "Refrão", COLOR_MINT, false),
                        DetectedChord(6, "G", "G", "Maior", "01:28", "Refrão", COLOR_EMERALD, false),
                        DetectedChord(7, "D", "D", "Maior", "01:44", "Ponte", COLOR_SKY, false),
                        DetectedChord(8, "Em", "E", "Menor", "02:02", "Ponte", COLOR_PURPLE, true),
                        DetectedChord(9, "Am", "A", "Menor", "02:24", "Final", COLOR_ROSE, true),
                        DetectedChord(10, "F", "F", "Maior", "02:45", "Swell Outro", COLOR_GOLD, false)
                    )
                )
            }
        }
    }
}
