package com.arenamix.app.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.arenamix.app.R
import com.arenamix.app.databinding.FragmentCreateTournamentBinding
import com.arenamix.app.model.Sport
import com.arenamix.app.model.TournamentFormat

class CreateTournamentFragment : Fragment() {

    private var _binding: FragmentCreateTournamentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedTournamentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTournamentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Sport selection
        val sportCards = mapOf(
            binding.createSportFutbol to Sport.FUTBOL,
            binding.createSportPadel to Sport.PADEL,
            binding.createSportBasket to Sport.BASKET
        )
        sportCards.forEach { (card, sport) ->
            card.setOnClickListener {
                viewModel.selectedSport.value = sport
                updateSportSelection(sport)
            }
        }

        // Format selection
        val formatCards = mapOf(
            binding.formatTorneo to TournamentFormat.TOURNAMENT,
            binding.formatGroups to TournamentFormat.GROUPS_TOURNAMENT,
            binding.formatLiga to TournamentFormat.LIGA
        )
        formatCards.forEach { (card, format) ->
            card.setOnClickListener {
                viewModel.selectedFormat.value = format
                updateFormatSelection(format)
            }
        }

        // Restore UI state from ViewModel
        viewModel.selectedSport.value?.let { updateSportSelection(it) }
        viewModel.selectedFormat.value?.let { updateFormatSelection(it) }

        binding.btnContinue.setOnClickListener {
            val name = binding.etTournamentName.text?.toString()?.trim() ?: ""
            viewModel.tournamentName.value = name
            // Init participants list with 8 empty slots by default
            if (viewModel.participantNames.value.isNullOrEmpty()) {
                viewModel.participantNames.value = MutableList(8) { "" }
            }
            findNavController().navigate(R.id.action_create_to_participants)
        }
    }

    private fun updateSportSelection(selected: Sport) {
        binding.createSportFutbol.setBackgroundResource(
            if (selected == Sport.FUTBOL) R.drawable.bg_sport_card_selected else R.drawable.bg_sport_card
        )
        binding.createSportPadel.setBackgroundResource(
            if (selected == Sport.PADEL) R.drawable.bg_sport_card_selected else R.drawable.bg_sport_card
        )
        binding.createSportBasket.setBackgroundResource(
            if (selected == Sport.BASKET) R.drawable.bg_sport_card_selected else R.drawable.bg_sport_card
        )
    }

    private fun updateFormatSelection(selected: TournamentFormat) {
        binding.formatTorneo.setBackgroundResource(
            if (selected == TournamentFormat.TOURNAMENT) R.drawable.bg_format_card_selected else R.drawable.bg_format_card
        )
        binding.formatGroups.setBackgroundResource(
            if (selected == TournamentFormat.GROUPS_TOURNAMENT) R.drawable.bg_format_card_selected else R.drawable.bg_format_card
        )
        binding.formatLiga.setBackgroundResource(
            if (selected == TournamentFormat.LIGA) R.drawable.bg_format_card_selected else R.drawable.bg_format_card
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
