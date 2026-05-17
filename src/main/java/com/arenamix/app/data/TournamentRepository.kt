package com.arenamix.app.data

import com.arenamix.app.model.*

object TournamentRepository {

    private val tournaments = mutableListOf<Tournament>()

    init {
        // Seed with a sample 8-team tournament structure (QF -> SF -> Final)
        val sampleTeams = (1..8).map { Team(it, "Equipo ${'A' + (it - 1)}") }
        val sampleMatches = generateBracket(sampleTeams, baseId = 100)
        
        // Add some sample results for the first QF
        sampleMatches[0].scoreA = 2
        sampleMatches[0].scoreB = 1
        sampleMatches[0].winnerId = sampleMatches[0].teamA?.id
        sampleMatches[4].teamA = sampleMatches[0].teamA // Advance to SF1
        
        tournaments.add(
            Tournament(
                id = 1, 
                name = "Copa Arena Mix",
                sport = Sport.FUTBOL, 
                format = TournamentFormat.TOURNAMENT,
                teams = sampleTeams, 
                matches = sampleMatches
            )
        )
    }

    fun getAll(): List<Tournament> = tournaments.toList()

    fun getById(id: Int): Tournament? = tournaments.find { it.id == id }

    fun add(tournament: Tournament) {
        tournaments.add(tournament)
    }

    /**
     * Updates the score of a match and advances the winner to the next round.
     */
    fun updateMatchScore(matchId: Int, scoreA: Int, scoreB: Int) {
        var parentTournament: Tournament? = null
        var matchToUpdate: Match? = null

        // Find the match and its tournament
        for (t in tournaments) {
            matchToUpdate = t.matches.find { it.id == matchId }
            if (matchToUpdate != null) {
                parentTournament = t
                break
            }
        }

        matchToUpdate?.let { match ->
            match.scoreA = scoreA
            match.scoreB = scoreB
            
            // Determine winner
            match.winnerId = when {
                scoreA > scoreB -> match.teamA?.id
                scoreB > scoreA -> match.teamB?.id
                else -> null
            }

            val winner = if (match.winnerId == match.teamA?.id) match.teamA else match.teamB
            
            // Progression: Find which future match depends on this one
            parentTournament?.matches?.forEach { nextMatch ->
                if (nextMatch.parentMatchAId == match.id) {
                    nextMatch.teamA = winner
                    // Reset scores of the next match if the participant changed
                    nextMatch.scoreA = 0
                    nextMatch.winnerId = null
                } else if (nextMatch.parentMatchBId == match.id) {
                    nextMatch.teamB = winner
                    nextMatch.scoreB = 0
                    nextMatch.winnerId = null
                }
            }
        }
    }

    /**
     * Generates a fixed 8-slot bracket structure.
     */
    fun generateBracket(teams: List<Team>, baseId: Int = (System.currentTimeMillis() % 10000).toInt()): List<Match> {
        val mid = baseId
        
        // Quarter-finals (QF1, QF2, QF3, QF4)
        val qf1 = Match(id = mid + 1, round = Round.QUARTER_FINAL, teamA = teams.getOrNull(0), teamB = teams.getOrNull(1))
        val qf2 = Match(id = mid + 2, round = Round.QUARTER_FINAL, teamA = teams.getOrNull(2), teamB = teams.getOrNull(3))
        val qf3 = Match(id = mid + 3, round = Round.QUARTER_FINAL, teamA = teams.getOrNull(4), teamB = teams.getOrNull(5))
        val qf4 = Match(id = mid + 4, round = Round.QUARTER_FINAL, teamA = teams.getOrNull(6), teamB = teams.getOrNull(7))
        
        // Semifinals (SF1, SF2)
        val sf1 = Match(id = mid + 5, round = Round.SEMI_FINAL, parentMatchAId = qf1.id, parentMatchBId = qf2.id)
        val sf2 = Match(id = mid + 6, round = Round.SEMI_FINAL, parentMatchAId = qf3.id, parentMatchBId = qf4.id)
        
        // Final
        val finalMatch = Match(id = mid + 7, round = Round.FINAL, parentMatchAId = sf1.id, parentMatchBId = sf2.id)
        
        return listOf(qf1, qf2, qf3, qf4, sf1, sf2, finalMatch)
    }

    fun nextId(): Int = (tournaments.maxOfOrNull { it.id } ?: 0) + 1
}
