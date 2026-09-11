package com.example.data

data class SyncedLyricLine(
    val id: String,
    val timeSeconds: Int,
    val text: String,
    val section: String = "" // e.g. "Intro", "Verso 1", "Refrão", "Ponte"
) {
    val timeFormatted: String
        get() {
            val m = timeSeconds / 60
            val s = timeSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

object WorshipLyricsDatabase {

    fun getDefaultLyricsForSong(songTitle: String): List<SyncedLyricLine> {
        return when {
            songTitle.contains("Ele a Glória", ignoreCase = true) -> listOf(
                SyncedLyricLine("1", 4, "Porque Dele e por Ele", "Verso"),
                SyncedLyricLine("2", 12, "Para Ele são todas as coisas", "Verso"),
                SyncedLyricLine("3", 20, "Porque Dele e por Ele", "Verso"),
                SyncedLyricLine("4", 28, "Para Ele são todas as coisas", "Verso"),
                SyncedLyricLine("5", 38, "A Ele a glória, a Ele a glória!", "Refrão"),
                SyncedLyricLine("6", 48, "A Ele a glória, pra sempre, amém!", "Refrão"),
                SyncedLyricLine("7", 62, "Quão insondáveis são os Teus caminhos", "Ponte"),
                SyncedLyricLine("8", 78, "E inescrutáveis os Teus juízos!", "Ponte"),
                SyncedLyricLine("9", 95, "Quem conheceu a mente do Senhor?", "Ponte"),
                SyncedLyricLine("10", 112, "Quem foi Seu conselheiro?", "Ponte"),
                SyncedLyricLine("11", 130, "A Ele a glória, pra sempre, amém!", "Refrão Final")
            )

            songTitle.contains("Oceans", ignoreCase = true) -> listOf(
                SyncedLyricLine("1", 6, "You call me out upon the waters", "Verso 1"),
                SyncedLyricLine("2", 16, "The great unknown where feet may fail", "Verso 1"),
                SyncedLyricLine("3", 26, "And there I find You in the mystery", "Verso 1"),
                SyncedLyricLine("4", 36, "In oceans deep, my faith will stand", "Verso 1"),
                SyncedLyricLine("5", 48, "And I will call upon Your name", "Refrão"),
                SyncedLyricLine("6", 60, "And keep my eyes above the waves", "Refrão"),
                SyncedLyricLine("7", 74, "When oceans rise, my soul will rest in Your embrace", "Refrão"),
                SyncedLyricLine("8", 90, "For I am Yours and You are mine", "Refrão"),
                SyncedLyricLine("9", 108, "Spirit lead me where my trust is without borders", "Ponte"),
                SyncedLyricLine("10", 124, "Let me walk upon the waters wherever You would call me", "Ponte")
            )

            songTitle.contains("Bondade", ignoreCase = true) || songTitle.contains("Goodness", ignoreCase = true) -> listOf(
                SyncedLyricLine("1", 5, "Te amo, Deus, Tua graça nunca falha", "Verso 1"),
                SyncedLyricLine("2", 16, "Todos os dias eu estou em Tuas mãos", "Verso 1"),
                SyncedLyricLine("3", 28, "Desde o amanhecer até o sol se pôr", "Verso 1"),
                SyncedLyricLine("4", 40, "Eu cantarei da bondade de Deus!", "Verso 1"),
                SyncedLyricLine("5", 52, "És fiel em todo o tempo", "Refrão"),
                SyncedLyricLine("6", 64, "Em todo o tempo Tu és tão, tão bom!", "Refrão"),
                SyncedLyricLine("7", 78, "Com todo o fôlego que tenho", "Refrão"),
                SyncedLyricLine("8", 92, "Eu cantarei da bondade de Deus!", "Refrão"),
                SyncedLyricLine("9", 110, "Tua bondade me seguirá, me seguirá, Senhor!", "Ponte")
            )

            songTitle.contains("Rompendo", ignoreCase = true) -> listOf(
                SyncedLyricLine("1", 5, "Cada passo que eu der", "Verso 1"),
                SyncedLyricLine("2", 15, "Para trás não vou olhar", "Verso 1"),
                SyncedLyricLine("3", 25, "Vou romper em fé", "Verso 1"),
                SyncedLyricLine("4", 35, "Minha história Deus vai transformar", "Verso 1"),
                SyncedLyricLine("5", 48, "Rompendo em fé, com ousadia", "Refrão"),
                SyncedLyricLine("6", 60, "Conquistando a terra prometida!", "Refrão"),
                SyncedLyricLine("7", 75, "Nada vai me parar, nada vai me deter", "Refrão"),
                SyncedLyricLine("8", 90, "Porque o Senhor comigo está!", "Refrão")
            )

            else -> listOf(
                SyncedLyricLine("1", 4, "Santo, Santo, Santo é o Senhor", "Intro"),
                SyncedLyricLine("2", 16, "Digno de glória, honra e louvor", "Verso 1"),
                SyncedLyricLine("3", 28, "Toda a terra se prostra aos Teus pés", "Verso 1"),
                SyncedLyricLine("4", 42, "Aleluia, Tu és soberano e fiel!", "Refrão"),
                SyncedLyricLine("5", 60, "Derrama Tua glória neste lugar", "Ponte"),
                SyncedLyricLine("6", 80, "Sentimos Tua presença nos abraçar", "Ponte"),
                SyncedLyricLine("7", 100, "Pra sempre reina, amém!", "Refrão Final")
            )
        }
    }
}
