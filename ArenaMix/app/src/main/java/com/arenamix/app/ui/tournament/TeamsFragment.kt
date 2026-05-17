package com.arenamix.app.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.databinding.FragmentTeamsInfoBinding

class TeamsFragment : Fragment() {

    private var _binding: FragmentTeamsInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamsInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tournament = TournamentRepository.getAll().lastOrNull()
        tournament?.let {
            binding.rvTeams.layoutManager = LinearLayoutManager(requireContext())
            binding.rvTeams.adapter = TeamsAdapter(it.teams)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
