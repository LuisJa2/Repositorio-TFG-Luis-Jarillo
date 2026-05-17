package com.arenamix.app.ui.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arenamix.app.R
import com.arenamix.app.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to Home on any of these buttons
        binding.btnExplore.setOnClickListener { navigateToHome() }
        binding.tvGuest.setOnClickListener { navigateToHome() }
        binding.btnRegister.setOnClickListener { navigateToHome() }
        binding.btnLogin.setOnClickListener { navigateToHome() }

        // Sport selection on Welcome screen
        val sports = listOf(binding.sportFutbol, binding.sportPadel, binding.sportBasket)
        sports.forEach { card ->
            card.setOnClickListener {
                sports.forEach { it.setBackgroundResource(R.drawable.bg_sport_card) }
                card.setBackgroundResource(R.drawable.bg_sport_card_selected)
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_welcome_to_home)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
