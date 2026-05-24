package com.arenamix.app.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arenamix.app.R
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.databinding.FragmentParticipantsBinding
import com.arenamix.app.model.Team
import com.arenamix.app.model.Tournament

class ParticipantsFragment : Fragment() {

    private var _binding: FragmentParticipantsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedTournamentViewModel by activityViewModels()
    private lateinit var adapter: ParticipantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParticipantsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val names = viewModel.participantNames.value ?: mutableListOf()

        adapter = ParticipantAdapter(names) { index, value ->
            names[index] = value
            viewModel.participantNames.value = names
        }

        binding.rvParticipants.layoutManager = LinearLayoutManager(requireContext())
        binding.rvParticipants.adapter = adapter

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnAddParticipant.setOnClickListener {
            names.add("")
            viewModel.participantNames.value = names
            adapter.notifyItemInserted(names.size - 1)
        }

        binding.btnCreate.setOnClickListener {
            val validNames = (viewModel.participantNames.value ?: emptyList())
                .filter { it.isNotBlank() }

            if (validNames.size < 2) {
                Toast.makeText(requireContext(),
                    "Añade al menos 2 participantes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val teams = validNames.mapIndexed { i, n -> Team(i + 1, n) }
            val matches = TournamentRepository.generateBracket(teams)
            val tournament = Tournament(
                id = TournamentRepository.nextId(),
                name = viewModel.tournamentName.value ?: "Nuevo Torneo",
                sport = viewModel.selectedSport.value!!,
                format = viewModel.selectedFormat.value!!,
                teams = teams,
                matches = matches
            )
            TournamentRepository.add(tournament)

            // Reset ViewModel
            viewModel.participantNames.value = mutableListOf()
            viewModel.tournamentName.value = ""

            findNavController().navigate(R.id.action_participants_to_tournament)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
