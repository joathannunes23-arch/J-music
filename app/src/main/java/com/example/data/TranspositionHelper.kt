package com.example.data

object TranspositionHelper {

    private val NOTES_SHARP = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    private val NOTES_FLAT  = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    // Map common note names to chromatic index 0..11
    private val NOTE_INDEX_MAP = mapOf(
        "C" to 0, "B#" to 0,
        "C#" to 1, "DB" to 1, "Db" to 1,
        "D" to 2,
        "D#" to 3, "EB" to 3, "Eb" to 3,
        "E" to 4, "FB" to 4,
        "F" to 5, "E#" to 5,
        "F#" to 6, "GB" to 6, "Gb" to 6,
        "G" to 7,
        "G#" to 8, "AB" to 8, "Ab" to 8,
        "A" to 9,
        "A#" to 10, "BB" to 10, "Bb" to 10,
        "B" to 11, "CB" to 11
    )

    fun transposeKey(originalKey: String, semitones: Int): String {
        if (semitones == 0) return originalKey

        // Parse e.g. "C Maior", "G Maior", "D Menor"
        val parts = originalKey.trim().split(" ")
        val root = parts.firstOrNull() ?: originalKey
        val suffix = if (parts.size > 1) " " + parts.drop(1).joinToString(" ") else ""

        val transposedRoot = transposeChord(root, semitones)
        return "$transposedRoot$suffix"
    }

    fun transposeChord(chord: String, semitones: Int): String {
        if (semitones == 0) return chord
        val trimmed = chord.trim()
        if (trimmed.isEmpty()) return ""

        // Handle slash chords like G/B
        if (trimmed.contains("/")) {
            val slashParts = trimmed.split("/")
            val mainChord = transposeSingleChord(slashParts[0], semitones)
            val bassChord = transposeSingleChord(slashParts[1], semitones)
            return "$mainChord/$bassChord"
        }

        return transposeSingleChord(trimmed, semitones)
    }

    private fun transposeSingleChord(chord: String, semitones: Int): String {
        val root = extractRootNote(chord) ?: return chord
        val suffix = chord.substring(root.length)

        val idx = NOTE_INDEX_MAP[root] ?: return chord
        val newIdx = Math.floorMod(idx + semitones, 12)

        // Prefer sharp or flat depending on key context, default to sharp
        val newRoot = NOTES_SHARP[newIdx]
        return "$newRoot$suffix"
    }

    private fun extractRootNote(chord: String): String? {
        if (chord.length >= 2) {
            val twoChar = chord.substring(0, 2)
            if (twoChar in NOTE_INDEX_MAP) {
                return twoChar
            }
        }
        if (chord.isNotEmpty()) {
            val oneChar = chord.substring(0, 1)
            if (oneChar in NOTE_INDEX_MAP) {
                return oneChar
            }
        }
        return null
    }

    fun transposeChordSequence(chordCsv: String, semitones: Int): String {
        if (semitones == 0) return chordCsv
        return chordCsv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(", ") { transposeChord(it, semitones) }
    }
}
