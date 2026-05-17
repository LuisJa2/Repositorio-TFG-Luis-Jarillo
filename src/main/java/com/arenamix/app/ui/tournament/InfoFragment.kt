package com.arenamix.app.ui.tournament

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arenamix.app.R
import com.arenamix.app.data.TournamentRepository
import com.arenamix.app.model.Sport

class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(resources.getColor(R.color.background, null))
        }

        val tournament = TournamentRepository.getAll().lastOrNull()
        if (tournament != null) {
            fun row(label: String, value: String) = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val lv = TextView(requireContext()).apply {
                    text = label
                    textSize = 12f
                    setTextColor(resources.getColor(R.color.text_secondary, null))
                    setPadding(0, 24, 0, 4)
                }
                val tv = TextView(requireContext()).apply {
                    text = value
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.text_primary, null))
                    setPadding(0, 0, 0, 0)
                }
                addView(lv)
                addView(tv)
            }

            layout.addView(row("Nombre", tournament.name))
            layout.addView(row("Deporte", when (tournament.sport) {
                Sport.FUTBOL -> "Fútbol"
                Sport.PADEL -> "Padel"
                Sport.BASKET -> "Basket"
            }))
            layout.addView(row("Formato", when (tournament.format) {
                com.arenamix.app.model.TournamentFormat.TOURNAMENT -> "Eliminatoria"
                com.arenamix.app.model.TournamentFormat.GROUPS_TOURNAMENT -> "Fase de grupos + torneo"
                com.arenamix.app.model.TournamentFormat.LIGA -> "Liga"
            }))
            layout.addView(row("Equipos", "${tournament.teams.size}"))
            layout.addView(row("Partidos", "${tournament.matches.size}"))
            layout.addView(row("En directo",
                "${tournament.matches.count { it.isLive }} partidos LIVE"))
        } else {
            layout.addView(TextView(requireContext()).apply {
                text = "No hay información disponible"
                textSize = 15f
            })
        }

        return layout
    }
}
