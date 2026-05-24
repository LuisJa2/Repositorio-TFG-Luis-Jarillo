package com.arenamix.app.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.databinding.FragmentBracketBinding

class BracketFragment : Fragment() {

    private var _binding: FragmentBracketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBracketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Load the most recently added / first tournament
        val tournament = TournamentRepository.getAll().lastOrNull()
        tournament?.let {
            binding.bracketView.setMatches(it.matches)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
