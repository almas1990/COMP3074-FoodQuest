package com.comp3074.foodquest.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import com.comp3074.foodquest.R

class QuestDetailFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val id = requireArguments().getLong("questId")
        model.quests.observe(viewLifecycleOwner) { quests ->
            body.removeAllViews()
            back(body)
            val quest = quests.find { it.id == id }
            if (quest == null) {
                label(body, if (model.busy.value == true) "Loading quest..." else "Quest is no longer available.")
                return@observe
            }
            heading(body, quest.name)
            label(body, "${quest.points} points | ${quest.difficulty} | ${if (quest.completed) "Completed" else "Available"}")
            heading(body, quest.place)
            label(body, quest.address)
            label(body, quest.task)
            label(body, quest.tags.joinToString(" / "))
            label(body, if (quest.rating == 0) "Not rated" else "Your rating: ${quest.rating}/5")
            val complete = action(body, if (quest.completed) "Completed" else "Complete quest") { model.complete(id) }
            complete.id = R.id.complete_quest
            complete.isEnabled = !quest.completed && model.busy.value != true
            action(body, "Rate quest") {
                AlertDialog.Builder(requireContext()).setTitle("Your rating")
                    .setItems(arrayOf("1 star", "2 stars", "3 stars", "4 stars", "5 stars")) { _, rating -> model.rate(id, rating + 1) }.show()
            }
            action(body, "Map and directions") { host.open(MapFragment.newInstance(id)) }
            action(body, "Share by email") { ExternalActions.email(requireContext(), "", quest.name,
                "${quest.name}\n${quest.place}\n${quest.address}\nChallenge: ${quest.task}\nTags: ${quest.tags.joinToString()}\nRating: ${quest.rating}/5\nPoints: ${quest.points}\nCompleted: ${quest.completed}") }
            action(body, "Edit quest") { host.open(QuestFormFragment.newInstance(id)) }
            action(body, "Delete quest") {
                AlertDialog.Builder(requireContext()).setTitle("Delete ${quest.name}?")
                    .setMessage("This removes the quest and any points earned from it.")
                    .setNegativeButton("Cancel", null).setPositiveButton("Delete") { _, _ ->
                        model.delete(id) { if (isAdded) host.openRoot(QuestListFragment()) }
                    }.show()
            }
        }
        model.busy.observe(viewLifecycleOwner) { busy ->
            val quest = model.quests.value.orEmpty().find { it.id == id }
            view.findViewById<Button>(R.id.complete_quest)?.isEnabled = !busy && quest?.completed == false
        }
    }

    companion object {
        fun newInstance(id: Long) = QuestDetailFragment().apply { arguments = bundleOf("questId" to id) }
    }
}
