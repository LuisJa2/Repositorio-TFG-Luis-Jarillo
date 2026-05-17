package com.arenamix.app.ui.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arenamix.app.model.Sport
import com.arenamix.app.model.TournamentFormat

class SharedTournamentViewModel : ViewModel() {
    val selectedSport = MutableLiveData<Sport>(Sport.PADEL)
    val selectedFormat = MutableLiveData<TournamentFormat>(TournamentFormat.TOURNAMENT)
    val tournamentName = MutableLiveData<String>("")
    val participantNames = MutableLiveData<MutableList<String>>(mutableListOf())
}
