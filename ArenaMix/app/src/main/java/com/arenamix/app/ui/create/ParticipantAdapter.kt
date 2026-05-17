package com.arenamix.app.ui.create

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arenamix.app.databinding.ItemParticipantInputBinding

class ParticipantAdapter(
    private val names: MutableList<String>,
    private val onChanged: (Int, String) -> Unit
) : RecyclerView.Adapter<ParticipantAdapter.VH>() {

    inner class VH(val binding: ItemParticipantInputBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        fun bind(index: Int, name: String) {
            // Remove old watcher before updating text to avoid firing callbacks
            textWatcher?.let { binding.etParticipantName.removeTextChangedListener(it) }
            binding.etParticipantName.setText(name)
            binding.etParticipantName.hint = "Equipo ${index + 1}"

            val watcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    onChanged(index, s?.toString() ?: "")
                }
            }
            binding.etParticipantName.addTextChangedListener(watcher)
            textWatcher = watcher
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemParticipantInputBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(position, names[position])
    }

    override fun getItemCount() = names.size
}
