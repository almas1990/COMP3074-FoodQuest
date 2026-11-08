package com.comp3074.foodquest.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comp3074.foodquest.R
import com.comp3074.foodquest.data.Quest

class QuestAdapter(private val onSelected: (Long) -> Unit) : ListAdapter<Quest, QuestAdapter.Holder>(Difference) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_quest, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), onSelected)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.row_name)
        private val place: TextView = view.findViewById(R.id.row_place)
        private val progress: TextView = view.findViewById(R.id.row_progress)
        private val tags: TextView = view.findViewById(R.id.row_tags)

        fun bind(quest: Quest, onSelected: (Long) -> Unit) {
            name.text = quest.name
            place.text = quest.place
            val context = itemView.context
            progress.text = context.getString(R.string.quest_progress, quest.points, quest.difficulty,
                context.getString(if (quest.completed) R.string.completed else R.string.available), quest.rating)
            tags.text = quest.tags.joinToString(" / ")
            itemView.setOnClickListener { onSelected(quest.id) }
        }
    }

    private object Difference : DiffUtil.ItemCallback<Quest>() {
        override fun areItemsTheSame(old: Quest, new: Quest) = old.id == new.id
        override fun areContentsTheSame(old: Quest, new: Quest) = old == new
    }
}
