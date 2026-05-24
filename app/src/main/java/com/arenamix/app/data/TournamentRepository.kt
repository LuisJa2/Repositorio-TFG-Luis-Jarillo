package com.arenamix.app.data

import com.arenamix.app.model.*

object TournamentRepository {

    private val tournaments = mutableListOf<Tournament>()

    init {
        // Seed with sample data matching the mockup
        val teams8 = listOf(
            Team(1, "Equipo A", isLive = true),
            Team(2, "Equipo B"),
            Team(3, "Equipo C", isLive = true),
            Team(4, "Equipo A"),
            Team(5, "Equipo E", isLive = true),
            Team(6, "Equipo F"),
            Team(7, "Equipo G"),
            Team(8, "Equipo H")
        )
        val matches = listOf(
            Match(1, teams8[0], teams8[1], Round.QUARTER_FINAL, isLive = true),
            Match(2, teams8[2], teams8[3], Round.QUARTER_FINAL, isLive = true),
            Match(3, teams8[4], teams8[5], Round.QUARTER_FINAL, isLive = true),
            Match(4, teams8[6], teams8[7], Round.SEMI_FINAL),
            Match(5, teams8[2], Team(9, "Equipo A"), Round.SEMI_FINAL, isLive = true),
            Match(6, Team(10, "Equipo A"), Team(11, "Equipo E"), Round.FINAL)
        )
        tournaments.add(
            Tournament(
                id = 1, name = "Liga local",
                sport = Sport.FUTBOL, format = TournamentFormat.TOURNAMENT,
                teams = teams8, matches = matches
            )
        )

        val teamsP = listOf(
            Team(12, "Pareja A", isLive = true),
            Team(13, "Pareja B"),
            Team(14, "Pareja C"),
            Team(15, "Pareja D")
        )
        tournaments.add(
            Tournament(
                id = 2, name = "Torneo urbanizaciones",
                sport = Sport.PADEL, format = TournamentFormat.TOURNAMENT,
                teams = teamsP,
                matches = listOf(
                    Match(7, teamsP[0], teamsP[1], Round.SEMI_FINAL, isLive = true),
                    Match(8, teamsP[2], teamsP[3], Round.SEMI_FINAL)
                )
            )
        )
    }

    fun getAll(): List<Tournament> = tournaments.toList()

    fun getById(id: Int): Tournament? = tournaments.find { it.id == id }

    fun add(tournament: Tournament) {
        tournaments.add(tournament)
    }

    fun generateBracket(teams: List<Team>): List<Match> {
        val matches = mutableListOf<Match>()
        var matchId = (tournaments.flatMap { it.matches }.maxOfOrNull { it.id } ?: 0) + 1
        val shuffled = teams.shuffled()
        val rounds = mutableListOf<Round>()
        when (teams.size) {
            in 5..8 -> rounds.addAll(listOf(Round.QUARTER_FINAL, Round.SEMI_FINAL, Round.FINAL))
            in 3..4 -> rounds.addAll(listOf(Round.SEMI_FINAL, Round.FINAL))
            else -> rounds.add(Round.FINAL)
        }
        val pairs = shuffled.chunked(2)
        pairs.forEachIndexed { i, pair ->
            if (pair.size == 2) {
                val round = if (teams.size >= 5) Round.QUARTER_FINAL else Round.SEMI_FINAL
                matches.add(Match(matchId++, pair[0], pair[1], round))
            }
        }
        return matches
    }

    fun nextId(): Int = (tournaments.maxOfOrNull { it.id } ?: 0) + 1
}
