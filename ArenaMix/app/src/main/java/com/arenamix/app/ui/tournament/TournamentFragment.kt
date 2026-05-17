package com.arenamix.app.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arenamix.app.R
import com.arenamix.app.databinding.FragmentTournamentBinding
import com.google.android.material.tabs.TabLayoutMediator

class TournamentFragment : Fragment() {

    private var _binding: FragmentTournamentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val pagerAdapter = TournamentPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = pagerAdapter

        val tabTitles = listOf(
            getString(R.string.tab_info),
            getString(R.string.tab_teams),
            getString(R.string.tab_matches)
        )

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Start on the PARTIDOS tab (index 2)
        binding.viewPager.currentItem = 2

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_tournament_back)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
