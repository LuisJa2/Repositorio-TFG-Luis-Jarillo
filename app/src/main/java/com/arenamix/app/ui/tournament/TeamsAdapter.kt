package com.arenamix.app.ui.tournament

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arenamix.app.databinding.ItemTeamBinding
import com.arenamix.app.model.Team

class TeamsAdapter(private val teams: List<Team>) :
    RecyclerView.Adapter<TeamsAdapter.VH>() {

    inner class VH(val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val team = teams[position]
        holder.binding.tvTeamNumber.text = (position + 1).toString()
        holder.binding.tvTeamName.text = team.name
        holder.binding.tvLiveBadge.visibility = if (team.isLive) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = teams.size
}
