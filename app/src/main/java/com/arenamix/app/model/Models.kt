package com.arenamix.app.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class Sport { FUTBOL, PADEL, BASKET }
enum class TournamentFormat { TOURNAMENT, GROUPS_TOURNAMENT, LIGA }

@Parcelize
data class Team(
    val id: Int,
    val name: String,
    var isLive: Boolean = false
) : Parcelable

@Parcelize
data class Match(
    val id: Int,
    val teamA: Team,
    val teamB: Team,
    val round: Round,
    var scoreA: Int = 0,
    var scoreB: Int = 0,
    var isLive: Boolean = false,
    var winnerId: Int? = null
) : Parcelable

enum class Round {
    QUARTER_FINAL, SEMI_FINAL, FINAL
}

@Parcelize
data class Tournament(
    val id: Int,
    val name: String,
    val sport: Sport,
    val format: TournamentFormat,
    val teams: List<Team>,
    val matches: List<Match>
) : Parcelable
