package com.arenamix.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arenamix.app.R
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FAB → Create tournament
        binding.fabCreate.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_create_tournament)
        }

        // Tournament cards → open tournament detail
        binding.cardTournament1.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_tournament)
        }
        binding.cardTournament2.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_tournament)
        }

        // Sport selection
        val sports = listOf(binding.homeSportFutbol, binding.homeSportPadel, binding.homeSportBasket)
        sports.forEach { card ->
            card.setOnClickListener {
                sports.forEach { it.setBackgroundResource(R.drawable.bg_sport_card) }
                card.setBackgroundResource(R.drawable.bg_sport_card_selected)
            }
        }

        // Bottom nav items
        binding.navItemTournaments.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_tournament)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
