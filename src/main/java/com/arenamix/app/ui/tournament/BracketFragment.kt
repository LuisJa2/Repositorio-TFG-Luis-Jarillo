package com.arenamix.app.ui.tournament

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arenamix.app.R
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.databinding.FragmentBracketBinding
import com.arenamix.app.model.Match

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
        refreshBracket()

        binding.bracketView.onMatchClickListener = { match ->
            showScoreInputDialog(match)
        }
    }

    private fun refreshBracket() {
        val tournament = TournamentRepository.getAll().lastOrNull()
        tournament?.let {
            binding.bracketView.setMatches(it.matches)
        }
    }

    private fun showScoreInputDialog(match: Match) {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val tvA = TextView(context).apply { text = match.teamA?.name ?: "TBD" }
        val etA = EditText(context).apply { 
            hint = "Goles/Puntos"
            setText(match.scoreA.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            isEnabled = match.teamA != null
        }

        val tvB = TextView(context).apply { 
            text = match.teamB?.name ?: "TBD"
            setPadding(0, 20, 0, 0) 
        }
        val etB = EditText(context).apply { 
            hint = "Goles/Puntos"
            setText(match.scoreB.toString())
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            isEnabled = match.teamB != null
        }

        layout.addView(tvA)
        layout.addView(etA)
        layout.addView(tvB)
        layout.addView(etB)

        AlertDialog.Builder(context)
            .setTitle("Introducir Resultado")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val scoreA = etA.text.toString().toIntOrNull() ?: 0
                val scoreB = etB.text.toString().toIntOrNull() ?: 0
                TournamentRepository.updateMatchScore(match.id, scoreA, scoreB)
                refreshBracket()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
